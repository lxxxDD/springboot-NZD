<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllMarketCategories, createMarketCategory, updateMarketCategory, deleteMarketCategory } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const formRef = ref(null)
const form = ref({ id: null, name: '', icon: '', sortOrder: 0, status: 1 })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const iconOptions = [
  { label: '显示器', value: 'Monitor' },
  { label: '书籍', value: 'Reading' },
  { label: '家居', value: 'HomeFilled' },
  { label: '篮球', value: 'Basketball' },
  { label: '购物袋', value: 'ShoppingBag' },
  { label: '魔法棒', value: 'MagicStick' },
  { label: '票券', value: 'Ticket' },
  { label: '更多', value: 'More' },
  { label: '手机', value: 'Cellphone' },
  { label: '相机', value: 'Camera' },
  { label: '耳机', value: 'Headset' },
  { label: '音乐', value: 'Headset' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllMarketCategories()
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = { id: null, name: '', icon: 'More', sortOrder: 0, status: 1 }
  dialogTitle.value = '添加分类'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogTitle.value = '编辑分类'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除此分类？删除后关联商品的分类将为空', '警告', { type: 'warning' })
    await deleteMarketCategory(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.value.id) {
      await updateMarketCategory(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createMarketCategory(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== false) ElMessage.error('操作失败')
  }
}

const handleStatusChange = async (row) => {
  try {
    await updateMarketCategory(row.id, { status: row.status })
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的分类')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个分类?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteMarketCategory(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white">分类管理</h2>
      <div class="flex gap-4">
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon class="mr-1"><Plus /></el-icon>添加分类
        </el-button>
        <el-button :loading="loading" @click="fetchData">
          <el-icon class="mr-1"><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" row-class-name="hover-row-glow" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="图标" width="80">
            <template #default="{ row }">
              <el-icon :size="24" class="text-indigo-400"><component :is="row.icon" /></el-icon>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="分类名称" />
          <el-table-column prop="sortOrder" label="排序" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </BlurReveal>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" class="dark-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="form.icon" placeholder="选择图标" style="width: 100%">
            <el-option v-for="item in iconOptions" :key="item.value" :label="item.label" :value="item.value">
              <div class="flex items-center gap-2">
                <el-icon><component :is="item.value" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
:deep(.hover-row-glow:hover > td) {
  background-color: rgba(99, 102, 241, 0.08) !important;
}
</style>
