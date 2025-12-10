<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFoodItemList, deleteFoodItem, updateFoodItemStatus, createFoodItem, updateFoodItem, getCanteenList, uploadImage } from '@/api'
import { Plus } from '@element-plus/icons-vue'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const canteenList = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, canteenId: '', name: '' })
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加菜品')
const formLoading = ref(false)
const formRef = ref(null)
const form = ref({ id: null, name: '', price: 0, category: '', canteenId: null, image: '', description: '', status: 'available' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getFoodItemList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取菜品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchCanteens = async () => {
  try {
    const res = await getCanteenList({ page: 1, size: 100 })
    canteenList.value = res.data?.records || res.data || []
  } catch (error) {
    console.error('获取食堂列表失败:', error)
  }
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 'available' ? 'offline' : 'available'
  try {
    await updateFoodItemStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该菜品?', '警告', { type: 'warning' })
    await deleteFoodItem(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const getStatusConfig = (status) => {
  const map = { 
    available: { text: '上架中', type: 'success', class: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30' },
    soldout: { text: '已售罄', type: 'warning', class: 'bg-amber-500/20 text-amber-400 border-amber-500/30' },
    offline: { text: '已下架', type: 'danger', class: 'bg-rose-500/20 text-rose-400 border-rose-500/30' }
  }
  return map[status] || map.offline
}

const handleAdd = () => {
  form.value = { id: null, name: '', price: 0, category: '', canteenId: null, image: '', description: '', status: 'available' }
  dialogTitle.value = '添加菜品'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  form.value = { ...row }
  dialogTitle.value = '编辑菜品'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  formLoading.value = true
  try {
    if (form.value.id) {
      await updateFoodItem(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createFoodItem(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    formLoading.value = false
  }
}

const handleUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    if (res.code === 200 && res.data?.url) {
      form.value.image = res.data.url
      ElMessage.success('上传成功')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchData()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的菜品')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个菜品?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteFoodItem(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

const handleExport = () => {
  ElMessage.success('正在导出数据...')
}

onMounted(() => {
  fetchCanteens()
  fetchData()
})
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-orange-500"><Dish /></el-icon>
        菜品管理
      </h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.name" placeholder="搜索菜品名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.canteenId" placeholder="选择食堂" clearable @change="handleSearch" style="width: 150px">
          <el-option v-for="item in canteenList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">导出</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">
          <el-icon class="mr-1"><Plus /></el-icon>添加菜品
        </el-button>
      </div>
    </div>
    
    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange" row-class-name="hover-row-glow">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80">
            <template #default="{ row }">
              <span class="font-mono text-slate-400">#{{ row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="菜品信息" min-width="220">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-lg border border-white/10 overflow-hidden bg-slate-800 shrink-0">
                   <el-image 
                    v-if="row.image" 
                    :src="row.image" 
                    :preview-src-list="[row.image]" 
                    fit="cover" 
                    class="w-full h-full"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center">
                    <el-icon class="text-slate-600"><Picture /></el-icon>
                  </div>
                </div>
                <div>
                  <div class="text-white font-bold">{{ row.name }}</div>
                  <div class="text-xs text-slate-400 mt-0.5 inline-block bg-white/5 px-1.5 py-0.5 rounded border border-white/5">
                    {{ row.category }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="price" label="价格" width="120">
            <template #default="{ row }">
              <span class="text-emerald-400 font-mono text-lg font-bold">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="monthlySales" label="月销量" width="120">
            <template #default="{ row }">
              <div class="flex items-center gap-1">
                <el-icon class="text-orange-500 text-xs"><Trophy /></el-icon>
                <span class="text-white font-mono">{{ row.monthlySales }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="rating" label="评分" width="100">
            <template #default="{ row }">
              <span class="text-yellow-400 font-bold flex items-center gap-1">
                <el-icon><StarFilled /></el-icon>
                {{ row.rating }}
              </span>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag 
                :type="getStatusConfig(row.status).type" 
                class="!bg-transparent !border"
                :class="getStatusConfig(row.status).class"
                size="small"
                effect="dark"
              >
                {{ getStatusConfig(row.status).text }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleStatusChange(row)">
                {{ row.status === 'available' ? '下架' : '上架' }}
              </el-button>
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="flex justify-end p-4">
          <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
        </div>
      </div>
    </BlurReveal>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" class="dark-dialog">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="菜品名称"><el-input v-model="form.name" placeholder="请输入菜品名称" /></el-form-item>
        <el-form-item label="所属食堂">
          <el-select v-model="form.canteenId" placeholder="选择食堂" style="width: 100%">
            <el-option v-for="item in canteenList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品图片">
          <el-upload 
            class="image-uploader border border-dashed border-slate-600 hover:border-orange-500 rounded-lg transition-colors"
            :show-file-list="false" 
            :http-request="handleUpload" 
            accept="image/*"
          >
            <img v-if="form.image" :src="form.image" class="w-[120px] h-[80px] object-cover rounded-lg" />
            <el-icon v-else class="text-slate-500 text-2xl w-[120px] h-[80px] flex items-center justify-center"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" placeholder="如：主食/小菜/饮品" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入菜品描述" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
:deep(.hover-row-glow:hover > td) {
  background-color: rgba(99, 102, 241, 0.08) !important;
}
</style>