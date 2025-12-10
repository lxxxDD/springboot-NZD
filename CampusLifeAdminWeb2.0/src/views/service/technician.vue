<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTechnicianList, createTechnician, updateTechnician, deleteTechnician } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('添加维修人员')
const formData = ref({
  id: null,
  name: '',
  phone: '',
  specialty: '',
  status: 'active'
})

const specialtyOptions = [
  { label: '电路维修', value: 'Electric' },
  { label: '水管维修', value: 'Water' },
  { label: '网络维修', value: 'Wifi' },
  { label: '家具维修', value: 'Furniture' },
  { label: '空调维修', value: 'AC' },
  { label: '综合维修', value: 'Other' }
]

const statusOptions = [
  { label: '在职', value: 'active' },
  { label: '离职', value: 'inactive' }
]

const getSpecialtyConfig = (val) => {
  const map = {
    'Electric': { text: '电路维修', icon: 'Lightning', class: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30' },
    'Water': { text: '水管维修', icon: 'Drizzling', class: 'bg-blue-500/20 text-blue-400 border-blue-500/30' },
    'Wifi': { text: '网络维修', icon: 'Connection', class: 'bg-purple-500/20 text-purple-400 border-purple-500/30' },
    'Furniture': { text: '家具维修', icon: 'House', class: 'bg-amber-500/20 text-amber-400 border-amber-500/30' },
    'AC': { text: '空调维修', icon: 'WindPower', class: 'bg-cyan-500/20 text-cyan-400 border-cyan-500/30' },
    'Other': { text: '综合维修', icon: 'Tools', class: 'bg-slate-500/20 text-slate-400 border-slate-500/30' }
  }
  return map[val] || map['Other']
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTechnicianList()
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取维修人员列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加维修人员'
  formData.value = { id: null, name: '', phone: '', specialty: '', status: 'active' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑维修人员'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.value.name || !formData.value.phone) {
    ElMessage.warning('请填写姓名和电话')
    return
  }
  // 验证手机号格式
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(formData.value.phone)) {
    ElMessage.warning('请输入正确的11位手机号')
    return
  }
  try {
    if (formData.value.id) {
      await updateTechnician(formData.value.id, formData.value)
      ElMessage.success('更新成功')
    } else {
      await createTechnician(formData.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该维修人员?', '警告', { type: 'warning' })
    await deleteTechnician(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的人员')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个人员?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteTechnician(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-amber-500"><User /></el-icon>
        维修人员管理
      </h2>
      <div class="flex gap-4">
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">
          <el-icon class="mr-1"><Plus /></el-icon>添加人员
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
          <el-table-column prop="id" label="ID" width="80">
            <template #default="{ row }">
              <span class="font-mono text-slate-400">#{{ row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="维修人员" min-width="200">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-gradient-to-br from-amber-500 to-orange-600 flex items-center justify-center text-white font-bold text-lg shadow-lg shadow-amber-500/20">
                  {{ row.name?.charAt(0) }}
                </div>
                <div>
                  <div class="text-white font-bold">{{ row.name }}</div>
                  <div class="text-xs text-slate-500 font-mono">{{ row.phone }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="specialty" label="专长" width="140">
            <template #default="{ row }">
              <div 
                class="px-2.5 py-1 rounded-full border text-xs font-bold inline-flex items-center gap-1"
                :class="getSpecialtyConfig(row.specialty).class"
              >
                {{ getSpecialtyConfig(row.specialty).text }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="avgRating" label="评分" width="120">
            <template #default="{ row }">
              <div class="flex items-center gap-1.5">
                <el-icon class="text-yellow-400"><StarFilled /></el-icon>
                <span class="text-yellow-400 font-bold text-lg">{{ row.avgRating || '5.0' }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="completedCount" label="完成订单" width="120">
            <template #default="{ row }">
              <div class="flex items-center gap-1">
                <el-icon class="text-emerald-500"><Check /></el-icon>
                <span class="text-emerald-400 font-mono font-bold">{{ row.completedCount || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <div 
                class="px-2 py-0.5 rounded-full border text-xs flex items-center gap-1.5 w-fit"
                :class="row.status === 'active' 
                  ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30' 
                  : 'bg-slate-700/50 text-slate-400 border-slate-600/30'"
              >
                <span 
                  class="w-1.5 h-1.5 rounded-full" 
                  :class="row.status === 'active' ? 'bg-emerald-400 animate-pulse' : 'bg-slate-500'"
                ></span>
                {{ row.status === 'active' ? '在职' : '离职' }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </BlurReveal>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" class="dark-dialog">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="专长">
          <el-select v-model="formData.specialty" placeholder="选择专长" class="w-full">
            <el-option v-for="item in specialtyOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status" class="w-full">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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

:deep(.el-select .el-input__wrapper),
:deep(.el-input__wrapper) {
  background-color: rgba(15, 23, 42, 0.9) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
}
</style>
