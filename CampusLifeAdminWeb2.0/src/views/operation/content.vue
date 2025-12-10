<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNewsList, getNewsById, updateNewsStatus, deleteNews, saveNews, uploadImage } from '@/api'
import { Plus } from '@element-plus/icons-vue'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, category: '', title: '' })
const selectedRows = ref([])
const detailVisible = ref(false)
const currentDetail = ref({})
const dialogVisible = ref(false)
const dialogTitle = ref('发布内容')
const formLoading = ref(false)
const form = reactive({
  id: null,
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  category: 'notice',
  status: 'draft'
})

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

const handleDetail = async (row) => {
  try {
    loading.value = true
    const res = await getNewsById(row.id)
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
  form.summary = ''
  form.content = ''
  form.coverImage = ''
  form.category = 'notice'
  form.status = 'draft'
  dialogTitle.value = '发布内容'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑内容'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  formLoading.value = true
  try {
    await saveNews(form)
    ElMessage.success(form.id ? '更新成功' : '发布成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    formLoading.value = false
  }
}

const categoryOptions = [
  { label: '全部', value: '' },
  { label: '通知公告', value: 'notice' },
  { label: '学术动态', value: 'academic' },
  { label: '校园生活', value: 'campus' },
  { label: '活动资讯', value: 'activity' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getNewsList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取新闻列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePublish = async (row) => {
  try {
    await updateNewsStatus(row.id, 'published')
    ElMessage.success('发布成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该内容?', '警告', { type: 'warning' })
    await deleteNews(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const getStatusConfig = (status) => {
  const map = { 
    draft: { text: '草稿', type: 'info', class: 'bg-slate-700/50 text-slate-400 border-slate-600/30' },
    published: { text: '已发布', type: 'success', class: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30' },
    archived: { text: '已归档', type: 'warning', class: 'bg-amber-500/20 text-amber-400 border-amber-500/30' }
  }
  return map[status] || map.draft
}

const getCategoryText = (category) => {
  const map = { notice: '通知公告', academic: '学术动态', campus: '校园生活', activity: '活动资讯' }
  return map[category] || category
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
    ElMessage.warning('请先选择要删除的内容')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 条内容?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteNews(row.id)))
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
        <el-icon class="text-cyan-400"><Document /></el-icon>
        内容发布
      </h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.title" placeholder="搜索标题" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.category" placeholder="分类筛选" clearable @change="handleSearch" style="width: 150px">
          <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">导出</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">
          <el-icon class="mr-1"><Plus /></el-icon>发布内容
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
          
          <el-table-column label="封面" width="100">
            <template #default="{ row }">
              <div class="w-16 h-10 rounded overflow-hidden border border-white/10">
                <el-image v-if="row.coverImage" :src="row.coverImage" :preview-src-list="[row.coverImage]" fit="cover" class="w-full h-full" />
                <div v-else class="w-full h-full bg-slate-800 flex items-center justify-center text-xs text-slate-500">None</div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="title" label="标题" show-overflow-tooltip min-width="200">
             <template #default="{ row }">
               <span class="text-white hover:text-cyan-400 cursor-pointer transition-colors" @click="handleDetail(row)">{{ row.title }}</span>
             </template>
          </el-table-column>
          
          <el-table-column label="分类" width="120">
            <template #default="{ row }">
               <span class="bg-white/5 px-2 py-0.5 rounded text-xs text-slate-300 border border-white/5">{{ getCategoryText(row.category) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="viewCount" label="浏览量" width="100">
            <template #default="{ row }">
              <span class="font-mono text-cyan-500">{{ row.viewCount }}</span>
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
          
          <el-table-column prop="publishTime" label="发布时间" width="180">
            <template #default="{ row }">
              <span class="text-xs text-slate-500 font-mono">{{ row.publishTime }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button type="info" link @click="handleDetail(row)">详情</el-button>
              <el-button v-if="row.status === 'draft'" type="success" link @click="handlePublish(row)">发布</el-button>
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

    <!-- 弹窗部分简化展示，保持原有结构 -->
    <el-dialog v-model="detailVisible" title="内容预览" width="600px" class="dark-dialog">
       <div class="space-y-4">
          <h1 class="text-2xl font-bold text-white">{{ currentDetail.title }}</h1>
          <div class="flex gap-4 text-xs text-slate-500 border-b border-white/10 pb-4">
             <span>{{ currentDetail.publishTime }}</span>
             <span>阅读: {{ currentDetail.viewCount }}</span>
             <el-tag size="small" effect="dark">{{ getCategoryText(currentDetail.category) }}</el-tag>
          </div>
          <div v-if="currentDetail.summary" class="bg-indigo-500/10 p-3 rounded text-indigo-200 text-sm border-l-4 border-indigo-500">
             {{ currentDetail.summary }}
          </div>
          <div class="text-slate-300 leading-relaxed max-h-[400px] overflow-y-auto pr-2 custom-scrollbar" v-html="currentDetail.content"></div>
       </div>
       <template #footer><el-button @click="detailVisible = false" class="!w-full">关闭预览</el-button></template>
    </el-dialog>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" class="dark-dialog">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" placeholder="请输入标题" /></el-form-item>
        <el-form-item label="封面图">
          <el-upload class="image-uploader border border-dashed border-slate-600 hover:border-cyan-500 rounded-lg transition-colors" :show-file-list="false" :http-request="handleUpload" accept="image/*">
            <img v-if="form.coverImage" :src="form.coverImage" class="w-[120px] h-[80px] object-cover rounded-lg" />
            <el-icon v-else class="text-slate-500 text-2xl w-[120px] h-[80px] flex items-center justify-center"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="选择分类">
            <el-option v-for="item in categoryOptions.slice(1)" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入摘要" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入内容" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="draft">草稿</el-radio>
            <el-radio value="published">立即发布</el-radio>
          </el-radio-group>
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