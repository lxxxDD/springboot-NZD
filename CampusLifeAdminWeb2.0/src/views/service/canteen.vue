<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCanteenList, deleteCanteen, updateCanteenStatus, createCanteen, updateCanteen, uploadImage } from '@/api'
import { Plus } from '@element-plus/icons-vue'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, name: '' })
const selectedRows = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加食堂')
const formLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  location: '',
  image: '',
  totalSeats: 100,
  openTime: '07:00',
  closeTime: '21:00',
  status: 'open'
})

// 图片上传
const uploading = ref(false)
const handleUpload = async (options) => {
  uploading.value = true
  try {
    const res = await uploadImage(options.file)
    if (res.code === 200 && res.data?.url) {
      form.image = res.data.url
      ElMessage.success('上传成功')
    }
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

const formRules = {
  name: [{ required: true, message: '请输入食堂名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  totalSeats: [{ required: true, message: '请输入座位数', trigger: 'blur' }]
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.location = ''
  form.image = ''
  form.totalSeats = 100
  form.openTime = '07:00'
  form.closeTime = '21:00'
  form.status = 'open'
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '添加食堂'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.id = row.id
  form.name = row.name
  form.location = row.location
  form.image = row.image || ''
  form.totalSeats = row.totalSeats
  form.openTime = row.openTime
  form.closeTime = row.closeTime
  form.status = row.status
  dialogTitle.value = '编辑食堂'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    formLoading.value = true
    try {
      if (form.id) {
        await updateCanteen(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createCanteen(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      formLoading.value = false
    }
  })
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCanteenList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取食堂列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 'open' ? 'closed' : 'open'
  try {
    await updateCanteenStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该食堂?', '警告', { type: 'warning' })
    await deleteCanteen(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const getStatusConfig = (status) => {
  const map = { 
    open: { text: '营业中', class: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30', dot: 'bg-emerald-400' },
    closed: { text: '已打烊', class: 'bg-slate-700/50 text-slate-400 border-slate-600/30', dot: 'bg-slate-400' },
    maintenance: { text: '维护中', class: 'bg-amber-500/20 text-amber-400 border-amber-500/30', dot: 'bg-amber-400' }
  }
  return map[status] || map.closed
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
    ElMessage.warning('请先选择要删除的食堂')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个食堂?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteCanteen(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

const handleExport = () => {
  ElMessage.success('正在导出数据...')
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-pink-500"><Bowl /></el-icon>
        食堂管理
      </h2>
      <div class="flex gap-4">
        <el-input 
          v-model="queryParams.name" 
          placeholder="搜索食堂名称" 
          clearable 
          style="width: 200px" 
          @keyup.enter="handleSearch" 
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">导出</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">
          <el-icon class="mr-1"><Plus /></el-icon>添加食堂
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
          
          <el-table-column label="预览图" width="120">
            <template #default="{ row }">
              <div class="relative w-20 h-14 rounded-lg overflow-hidden border border-white/10 group cursor-pointer hover:border-pink-500/50 transition-colors">
                <el-image 
                  v-if="row.image" 
                  :src="row.image" 
                  :preview-src-list="[row.image]" 
                  fit="cover" 
                  class="w-full h-full transform group-hover:scale-110 transition-transform duration-500" 
                />
                <div v-else class="w-full h-full bg-slate-800 flex items-center justify-center text-xs text-slate-500 flex-col gap-1">
                  <el-icon><Picture /></el-icon>
                  <span>No Img</span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="name" label="食堂名称" min-width="150">
             <template #default="{ row }">
               <span class="text-white font-bold text-base">{{ row.name }}</span>
             </template>
          </el-table-column>
          
          <el-table-column prop="location" label="位置" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="flex items-center gap-1.5 text-slate-300">
                <el-icon><Location /></el-icon>
                {{ row.location }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="totalSeats" label="座位数" width="100">
            <template #default="{ row }">
              <span class="font-mono text-indigo-400">{{ row.totalSeats }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="openTime" label="营业时间" width="180">
            <template #default="{ row }">
              <div class="font-mono text-xs bg-slate-800/50 px-2 py-1 rounded inline-block border border-white/5 text-slate-300">
                {{ row.openTime }} - {{ row.closeTime }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <div 
                class="px-2 py-0.5 rounded-full border text-xs flex items-center gap-1.5 w-fit"
                :class="getStatusConfig(row.status).class"
              >
                <span class="w-1.5 h-1.5 rounded-full animate-pulse" :class="getStatusConfig(row.status).dot"></span>
                {{ getStatusConfig(row.status).text }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button 
                :type="row.status === 'open' ? 'warning' : 'success'" 
                link 
                @click="handleStatusChange(row)"
              >
                {{ row.status === 'open' ? '打烊' : '营业' }}
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" class="dark-dialog">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="食堂名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入食堂名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="食堂图片">
          <el-upload
            class="image-uploader border border-dashed border-slate-600 hover:border-pink-500 rounded-lg transition-colors"
            :show-file-list="false"
            :http-request="handleUpload"
            accept="image/*"
          >
            <img v-if="form.image" :src="form.image" class="w-[120px] h-[80px] object-cover rounded-lg" />
            <el-icon v-else class="text-slate-500 text-2xl w-[120px] h-[80px] flex items-center justify-center"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="座位数" prop="totalSeats">
          <el-input-number v-model="form.totalSeats" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item label="营业时间">
          <div class="flex items-center gap-2">
            <el-time-select v-model="form.openTime" start="05:00" step="00:30" end="12:00" placeholder="开始" />
            <span class="text-white">-</span>
            <el-time-select v-model="form.closeTime" start="12:00" step="00:30" end="23:30" placeholder="结束" />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="营业中" value="open" />
            <el-option label="已打烊" value="closed" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
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