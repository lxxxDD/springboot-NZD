<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActivityList, getActivityById, updateActivityStatus, deleteActivity, createActivity } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

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
  maxParticipants: 100
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
  dialogTitle.value = '发布活动'
  dialogVisible.value = true
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
  const data = tableData.value.map(row => ({
    'ID': row.id,
    '活动名称': row.title,
    '地点': row.location,
    '报名人数': `${row.currentParticipants || 0}/${row.maxParticipants || '不限'}`,
    '状态': getStatusText(row.status),
    '开始时间': row.startTime
  }))
  const csv = [
    Object.keys(data[0]).join(','),
    ...data.map(row => Object.values(row).join(','))
  ].join('\n')
  const blob = new Blob([`\ufeff${csv}`], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `活动列表_${new Date().getTime()}.csv`
  link.click()
  ElMessage.success('导出成功')
}

const getStatusType = (status) => {
  const map = { upcoming: 'info', ongoing: 'success', ended: '', cancelled: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { upcoming: '即将开始', ongoing: '进行中', ended: '已结束', cancelled: '已取消' }
  return map[status] || status
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white">活动管理</h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.title" placeholder="搜索活动名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态筛选" clearable @change="handleSearch" style="width: 150px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">导出</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">发布活动</el-button>
      </div>
    </div>
    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="活动名称" show-overflow-tooltip />
          <el-table-column prop="location" label="地点" width="150" />
          <el-table-column label="报名人数" width="120">
            <template #default="{ row }">{{ row.currentParticipants || 0 }}/{{ row.maxParticipants || '不限' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="180" />
          <el-table-column label="操作" width="240">
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="活动详情" width="550px" class="dark-dialog">
      <div class="space-y-4">
        <div class="flex"><span class="w-24 text-slate-400">活动ID：</span><span class="text-white">{{ currentDetail.id }}</span></div>
        <div class="flex"><span class="w-24 text-slate-400">活动名称：</span><span class="text-white">{{ currentDetail.title }}</span></div>
        <div class="flex"><span class="w-24 text-slate-400">地点：</span><span class="text-white">{{ currentDetail.location }}</span></div>
        <div class="flex"><span class="w-24 text-slate-400">报名人数：</span><span class="text-white">{{ currentDetail.currentParticipants || 0 }}/{{ currentDetail.maxParticipants || '不限' }}</span></div>
        <div class="flex"><span class="w-24 text-slate-400">状态：</span><el-tag :type="getStatusType(currentDetail.status)" size="small">{{ getStatusText(currentDetail.status) }}</el-tag></div>
        <div class="flex"><span class="w-24 text-slate-400">开始时间：</span><span class="text-white">{{ currentDetail.startTime }}</span></div>
        <div class="flex"><span class="w-24 text-slate-400">结束时间：</span><span class="text-white">{{ currentDetail.endTime }}</span></div>
        <div><span class="text-slate-400">活动描述：</span><p class="text-white mt-2 p-3 bg-white/5 rounded-lg">{{ currentDetail.description }}</p></div>
      </div>
      <template #footer><el-button @click="detailVisible = false">关闭</el-button></template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" class="dark-dialog">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="活动名称" prop="title"><el-input v-model="form.title" placeholder="请输入活动名称" /></el-form-item>
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
