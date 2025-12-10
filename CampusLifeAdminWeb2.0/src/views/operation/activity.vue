<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, getActivityById, updateActivityStatus, deleteActivity, createActivity, uploadImage } from '@/api'
import { BASE_URL } from '@/api/request'
import { Plus } from '@element-plus/icons-vue'
import BlurReveal from '@/components/animations/BlurReveal.vue'

// 处理图片URL，补全路径
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_URL + url
}

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, status: '', title: '' })
const selectedRows = ref([])

// 详情弹窗
const detailVisible = ref(false)
const currentDetail = ref({})

// 编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('发布活动')
const formLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  title: '',
  description: '',
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 100,
  coverImage: ''
})

const handleDetail = async (row) => {
  try {
    loading.value = true
    const res = await getActivityById(row.id)
    currentDetail.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.id = null
  form.title = ''
  form.description = ''
  form.location = ''
  form.startTime = ''
  form.endTime = ''
  form.maxParticipants = 100
  form.coverImage = ''
  dialogTitle.value = '发布活动'
  dialogVisible.value = true
}

const handleUpload = async (options) => {
  try {
    const res = await uploadImage(options.file)
    if (res.code === 200 && res.data?.url) {
      form.coverImage = res.data.url
      ElMessage.success('上传成功')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑活动'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  formLoading.value = true
  try {
    await createActivity(form)
    ElMessage.success(form.id ? '更新成功' : '发布成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    formLoading.value = false
  }
}

const statusOptions = [
  { label: '全部', value: '' },
  { label: '即将开始', value: 'upcoming' },
  { label: '进行中', value: 'ongoing' },
  { label: '已结束', value: 'ended' },
  { label: '已取消', value: 'cancelled' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getActivityList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认取消该活动?', '警告', { type: 'warning' })
    await updateActivityStatus(row.id, 'cancelled')
    ElMessage.success('活动已取消')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该活动?', '警告', { type: 'warning' })
    await deleteActivity(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
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
    ElMessage.warning('请先选择要删除的活动')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个活动?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteActivity(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

const handleExport = () => {
  ElMessage.success('正在导出数据...')
}

const getStatusConfig = (status) => {
  const map = { 
    upcoming: { text: '即将开始', type: 'info', class: 'bg-indigo-500/20 text-indigo-400 border-indigo-500/30' },
    ongoing: { text: '进行中', type: 'success', class: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30 animate-pulse' },
    ended: { text: '已结束', type: 'info', class: 'bg-slate-500/20 text-slate-400 border-slate-500/30' },
    cancelled: { text: '已取消', type: 'danger', class: 'bg-rose-500/20 text-rose-400 border-rose-500/30' }
  }
  return map[status] || map.upcoming
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-purple-500"><Calendar /></el-icon>
        活动管理
      </h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.title" placeholder="搜索活动名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态筛选" clearable @change="handleSearch" style="width: 150px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">导出</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">
           <el-icon class="mr-1"><Plus /></el-icon>发布活动
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
          
          <el-table-column label="封面" width="120">
            <template #default="{ row }">
              <div class="w-20 h-14 rounded-lg overflow-hidden border border-white/10 relative group">
                <el-image 
                  v-if="row.coverImage" 
                  :src="getImageUrl(row.coverImage)" 
                  :preview-src-list="[getImageUrl(row.coverImage)]" 
                  fit="cover" 
                  class="w-full h-full transform group-hover:scale-110 transition-transform duration-500" 
                />
                <div v-else class="w-full h-full bg-slate-800 flex items-center justify-center text-xs text-slate-500">
                  NO IMG
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="title" label="活动名称" show-overflow-tooltip min-width="180">
             <template #default="{ row }">
               <span class="text-white font-bold">{{ row.title }}</span>
             </template>
          </el-table-column>
          
          <el-table-column prop="location" label="地点" width="150">
            <template #default="{ row }">
              <div class="flex items-center gap-1 text-slate-300">
                <el-icon><Location /></el-icon>
                {{ row.location }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="报名进度" width="160">
            <template #default="{ row }">
              <div class="flex flex-col gap-1">
                 <div class="flex justify-between text-xs font-mono">
                   <span class="text-indigo-400">{{ row.currentParticipants || 0 }}</span>
                   <span class="text-slate-500">/ {{ row.maxParticipants || '∞' }}</span>
                 </div>
                 <el-progress 
                   :percentage="row.maxParticipants ? Math.min(100, Math.round((row.currentParticipants || 0) / row.maxParticipants * 100)) : 0" 
                   :show-text="false" 
                   :stroke-width="4"
                   :color="getStatusConfig(row.status).type === 'danger' ? '#f43f5e' : '#6366f1'"
                 />
              </div>
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
          
          <el-table-column prop="startTime" label="开始时间" width="180">
            <template #default="{ row }">
              <span class="text-xs text-slate-500 font-mono">{{ row.startTime }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button type="info" link @click="handleDetail(row)">详情</el-button>
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 'upcoming'" type="warning" link @click="handleCancel(row)">取消</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="flex justify-end p-4">
          <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
        </div>
      </div>
    </BlurReveal>

    <!-- 详情/编辑弹窗 (省略代码以节省长度，保持功能不变，仅样式类名已在全局生效) -->
    <!-- 弹窗部分复用原逻辑，样式会自动继承 dark-dialog -->
    <el-dialog v-model="detailVisible" title="活动详情" width="550px" class="dark-dialog">
       <!-- ... content ... -->
       <div class="space-y-4">
        <div class="flex justify-between items-start">
           <h3 class="text-xl font-bold text-white">{{ currentDetail.title }}</h3>
           <el-tag :type="getStatusConfig(currentDetail.status).type" effect="dark">{{ getStatusConfig(currentDetail.status).text }}</el-tag>
        </div>
        <div v-if="currentDetail.coverImage" class="rounded-lg overflow-hidden h-40 border border-white/10">
           <img :src="getImageUrl(currentDetail.coverImage)" class="w-full h-full object-cover" />
        </div>
        <div class="grid grid-cols-2 gap-4 text-sm">
           <div class="bg-white/5 p-3 rounded border border-white/5">
              <div class="text-xs text-slate-500 mb-1">时间</div>
              <div class="text-white font-mono">{{ currentDetail.startTime }}</div>
           </div>
           <div class="bg-white/5 p-3 rounded border border-white/5">
              <div class="text-xs text-slate-500 mb-1">地点</div>
              <div class="text-white">{{ currentDetail.location }}</div>
           </div>
        </div>
        <div class="bg-white/5 p-3 rounded border border-white/5 text-sm">
           <div class="text-xs text-slate-500 mb-1">描述</div>
           <div class="text-slate-300 leading-relaxed">{{ currentDetail.description }}</div>
        </div>
       </div>
       <template #footer><el-button @click="detailVisible = false" class="!w-full">关闭</el-button></template>
    </el-dialog>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" class="dark-dialog">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="活动名称" prop="title"><el-input v-model="form.title" placeholder="请输入活动名称" /></el-form-item>
        <el-form-item label="封面图">
          <el-upload class="image-uploader border border-dashed border-slate-600 hover:border-purple-500 rounded-lg transition-colors" :show-file-list="false" :http-request="handleUpload" accept="image/*">
            <img v-if="form.coverImage" :src="getImageUrl(form.coverImage)" class="w-[120px] h-[80px] object-cover rounded-lg" />
            <el-icon v-else class="text-slate-500 text-2xl w-[120px] h-[80px] flex items-center justify-center"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="活动地点" prop="location"><el-input v-model="form.location" placeholder="请输入活动地点" /></el-form-item>
        <el-form-item label="人数上限"><el-input-number v-model="form.maxParticipants" :min="1" :max="10000" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" /></el-form-item>
        <el-form-item label="活动描述"><el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入活动描述" /></el-form-item>
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