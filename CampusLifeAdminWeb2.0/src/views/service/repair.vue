<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRepairList, getRepairById, updateRepairStatus, deleteRepair, assignRepair, getTechnicianList } from '@/api'
import { BASE_URL } from '@/api/request'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({
  page: 1,
  size: 10,
  status: '',
  keyword: ''
})

const selectedRows = ref([])
const detailVisible = ref(false)
const currentDetail = ref({})
const assignVisible = ref(false)
const assigningRow = ref(null)
const technicianList = ref([])
const selectedTechnician = ref('')

const statusVisible = ref(false)
const statusRow = ref(null)
const newStatus = ref('')

const editableStatuses = [
  { label: '已接收', value: 'received' },
  { label: '已派单', value: 'assigned' },
  { label: '处理中', value: 'in_progress' },
  { label: '已完成', value: 'completed' }
]

const statusOptions = [
  { label: '全部', value: '' },
  { label: '已接收', value: 'received' },
  { label: '已派单', value: 'assigned' },
  { label: '处理中', value: 'in_progress' },
  { label: '已完成', value: 'completed' }
]

// 处理图片URL，补全路径
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_URL + url
}

// 解析images字段（JSON数组格式）
const parseImages = (images) => {
  if (!images) return []
  try {
    const parsed = JSON.parse(images)
    return Array.isArray(parsed) ? parsed : [images]
  } catch {
    if (images.startsWith('http')) return [images]
    return [getImageUrl(images)]
  }
}

const handleDetail = async (row) => {
  try {
    loading.value = true
    const res = await getRepairById(row.id)
    currentDetail.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  } finally {
    loading.value = false
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRepairList(queryParams.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取报修列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAssign = async (row) => {
  assigningRow.value = row
  selectedTechnician.value = ''
  try {
    const res = await getTechnicianList()
    technicianList.value = res.data || []
  } catch (error) {
    technicianList.value = []
  }
  assignVisible.value = true
}

const confirmAssign = async () => {
  if (!selectedTechnician.value) {
    ElMessage.warning('请选择维修人员')
    return
  }
  try {
    await assignRepair(assigningRow.value.id, selectedTechnician.value)
    ElMessage.success('指派成功')
    assignVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('指派失败')
  }
}

// 获取可选的下一状态（状态流转限制）
const getAvailableStatuses = (currentStatus) => {
  const flow = {
    received: ['assigned', 'in_progress', 'completed'],  // 已接收 -> 已派单/处理中/已完成
    assigned: ['in_progress', 'completed'],               // 已派单 -> 处理中/已完成
    in_progress: ['completed'],                           // 处理中 -> 已完成
    completed: []                                         // 已完成 -> 不能改
  }
  return flow[currentStatus] || []
}

const handleChangeStatus = (row) => {
  const available = getAvailableStatuses(row.status)
  if (available.length === 0) {
    ElMessage.warning('该状态不能再修改')
    return
  }
  statusRow.value = row
  newStatus.value = ''
  statusVisible.value = true
}

const confirmStatusChange = async () => {
  if (!newStatus.value) {
    ElMessage.warning('请选择状态')
    return
  }
  // 验证状态流转
  const available = getAvailableStatuses(statusRow.value.status)
  if (!available.includes(newStatus.value)) {
    ElMessage.error('无效的状态变更')
    return
  }
  try {
    await updateRepairStatus(statusRow.value.id, newStatus.value)
    ElMessage.success('状态修改成功')
    statusVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('状态修改失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除此报修单?', '警告', { type: 'warning' })
    await deleteRepair(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchData()
}

const handlePageChange = (page) => {
  queryParams.value.page = page
  fetchData()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要删除的报修单')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 条报修单?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteRepair(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleExport = () => {
  ElMessage.success('正在导出数据...')
}

const getStatusConfig = (status) => {
  const map = { 
    received: { type: 'info', text: '已接收', dot: 'bg-slate-400' }, 
    assigned: { type: 'warning', text: '已派单', dot: 'bg-amber-400' }, 
    in_progress: { type: 'primary', text: '处理中', dot: 'bg-indigo-400 animate-pulse' }, 
    completed: { type: 'success', text: '已完成', dot: 'bg-emerald-400' } 
  }
  return map[status] || { type: 'info', text: status, dot: 'bg-slate-400' }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-amber-500"><Tools /></el-icon>
        报修管理
      </h2>
      <div class="flex gap-4">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索位置/类型/描述"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态筛选" clearable @change="handleSearch" style="width: 150px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" class="btn-glow" @click="handleSearch">搜索</el-button>
        <el-button :loading="loading" @click="fetchData">
          <el-icon class="mr-1"><Refresh /></el-icon>刷新
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">导出</el-button>
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
        
        <el-table-column label="现场图片" width="100">
          <template #default="{ row }">
            <div class="relative group cursor-pointer w-[60px] h-[40px] rounded overflow-hidden border border-white/10 group-hover:border-amber-500/50 transition-colors">
              <el-image 
                v-if="row.images" 
                :src="parseImages(row.images)[0]" 
                :preview-src-list="parseImages(row.images)" 
                fit="cover" 
                class="w-full h-full opacity-80 group-hover:opacity-100 transition-opacity"
              />
              <div v-else class="w-full h-full bg-slate-800 flex items-center justify-center text-xs text-slate-600">
                NO IMG
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="userId" label="申报人ID" width="100" />
        <el-table-column prop="location" label="位置" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="flex items-center gap-1.5">
              <el-icon class="text-slate-500"><Location /></el-icon>
              {{ row.location }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <span class="px-2 py-0.5 rounded bg-white/5 text-xs text-slate-300 border border-white/5">{{ row.type }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" show-overflow-tooltip />
        
        <el-table-column label="维修人员" width="120">
          <template #default="{ row }">
            <div v-if="row.technicianName" class="flex items-center gap-1.5">
              <el-icon class="text-emerald-500"><User /></el-icon>
              <span class="text-slate-300 text-sm">{{ row.technicianName }}</span>
            </div>
            <span v-else class="text-slate-600 text-xs">未指派</span>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="w-2 h-2 rounded-full shadow-[0_0_8px]" :class="[getStatusConfig(row.status).dot, `shadow-${getStatusConfig(row.status).type}`]"></span>
              <span :class="`text-${getStatusConfig(row.status).type === 'primary' ? 'indigo' : getStatusConfig(row.status).type}-400`" class="font-bold text-xs">
                {{ getStatusConfig(row.status).text }}
              </span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="提交时间" width="180">
           <template #default="{ row }">
            <span class="text-xs font-mono text-slate-500">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="info" link @click="handleDetail(row)">详情</el-button>
            <el-button type="primary" link @click="handleAssign(row)">指派</el-button>
            <el-button type="warning" link @click="handleChangeStatus(row)">状态</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex justify-end p-4">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="handlePageChange"
          @size-change="fetchData"
        />
        </div>
      </div>
    </BlurReveal>
    <el-dialog v-model="detailVisible" title="维修单详情" width="500px" class="dark-dialog">
      <div class="relative bg-slate-900/50 rounded-lg p-4 border border-white/5">
        <div class="absolute top-0 right-0 p-4">
           <el-tag :type="getStatusConfig(currentDetail.status).type" effect="dark" class="!text-sm">
             {{ getStatusConfig(currentDetail.status).text }}
           </el-tag>
        </div>
        
        <div class="space-y-4 text-sm">
          <div class="flex flex-col">
             <span class="text-xs text-slate-500 uppercase font-mono">Order ID</span>
             <span class="text-white font-mono text-lg">#{{ currentDetail.id }}</span>
          </div>
          
          <div class="grid grid-cols-2 gap-4">
             <div>
               <span class="text-xs text-slate-500 uppercase font-mono block mb-1">Location</span>
               <div class="text-white flex items-center gap-1">
                 <el-icon><Location /></el-icon> {{ currentDetail.location }}
               </div>
             </div>
             <div>
               <span class="text-xs text-slate-500 uppercase font-mono block mb-1">Category</span>
               <div class="text-white">{{ currentDetail.type }}</div>
             </div>
          </div>
          
          <div>
            <span class="text-xs text-slate-500 uppercase font-mono block mb-2">Description</span>
            <p class="text-slate-300 bg-white/5 p-3 rounded leading-relaxed border border-white/5">
              {{ currentDetail.description }}
            </p>
          </div>
          
          <div v-if="currentDetail.images">
            <span class="text-xs text-slate-500 uppercase font-mono block mb-2">Attachments</span>
            <div class="flex gap-2 flex-wrap">
              <img 
                v-for="(img, idx) in parseImages(currentDetail.images)" 
                :key="idx" 
                :src="img" 
                class="w-24 h-24 rounded border border-white/10 object-cover hover:border-amber-500/50 transition-colors" 
              />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false" class="!w-full">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 状态修改弹窗 -->
    <el-dialog v-model="statusVisible" title="修改状态" width="400px" class="dark-dialog">
      <div class="space-y-4">
        <div class="text-sm text-slate-400 mb-4">
          报修单 #{{ statusRow?.id }} - {{ statusRow?.location }}
        </div>
        <el-select v-model="newStatus" placeholder="选择状态" class="w-full" size="large">
          <el-option 
            v-for="item in editableStatuses.filter(s => getAvailableStatuses(statusRow?.status).includes(s.value))" 
            :key="item.value" 
            :label="item.label" 
            :value="item.value"
          >
            <div class="flex items-center gap-2">
              <span class="w-2 h-2 rounded-full" :class="getStatusConfig(item.value).dot"></span>
              <span>{{ item.label }}</span>
            </div>
          </el-option>
        </el-select>
      </div>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStatusChange">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 指派弹窗 -->
    <el-dialog v-model="assignVisible" title="指派维修人员" width="400px" class="dark-dialog">
      <div class="space-y-4">
        <div class="text-sm text-slate-400 mb-4">
          报修单 #{{ assigningRow?.id }} - {{ assigningRow?.location }}
        </div>
        <el-select v-model="selectedTechnician" placeholder="选择维修人员" class="w-full" size="large">
          <el-option 
            v-for="tech in technicianList" 
            :key="tech.id" 
            :label="`${tech.name} - ${tech.specialty || '综合'}`" 
            :value="tech.id"
          >
            <div class="flex justify-between items-center">
              <span>{{ tech.name }}</span>
              <span class="text-xs text-slate-500">{{ tech.specialty || '综合' }}</span>
            </div>
          </el-option>
        </el-select>
      </div>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确认指派</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
:deep(.hover-row-glow:hover > td) {
  background-color: rgba(99, 102, 241, 0.08) !important;
}
</style>