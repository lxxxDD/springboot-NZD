<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMarketItemList, getMarketItemById, updateMarketItemStatus, deleteMarketItem, getMarketCategories } from '@/api'
import { BASE_URL } from '@/api/request'
import BlurReveal from '@/components/animations/BlurReveal.vue'
import AiScanner from '@/views/market/audit/AiScanner.vue'

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
    // 尝试解析JSON数组
    const parsed = JSON.parse(images)
    return Array.isArray(parsed) ? parsed : [images]
  } catch {
    // 如果不是JSON，按逗号分隔处理
    return images.split(',')
  }
}

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, status: '', keyword: '', category: '' })
const categories = ref([])
const selectedRows = ref([])
const detailVisible = ref(false)
const currentDetail = ref({})
const aiScannerRef = ref(null)
const scanResults = ref([])

// 获取商品的 AI 扫描结果
const getItemRiskStatus = (itemId) => {
  return scanResults.value.find(r => r.itemId === itemId)
}

// 处理 AI 扫描结果更新
const handleScanResultsUpdate = (results) => {
  scanResults.value = results
}

// 判断行是否违规（用于行样式）
const tableRowClassName = ({ row }) => {
  const result = getItemRiskStatus(row.id)
  if (result?.riskStatus === 'danger') return 'danger-row hover-row-glow'
  if (result?.riskStatus === 'error') return 'error-row hover-row-glow'
  return 'hover-row-glow'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMarketItemList(queryParams.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过此商品审核?', '提示')
    await updateMarketItemStatus(row.id, 'approved')
    ElMessage.success('审核通过')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleReject = async (row) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因（用户可见）', '拒绝商品', {
      confirmButtonText: '确认拒绝',
      cancelButtonText: '取消',
      inputPlaceholder: '如：商品信息不完整、违规内容等',
      inputValidator: (val) => !!val?.trim() || '请输入拒绝原因',
      type: 'warning'
    })
    await updateMarketItemStatus(row.id, 'rejected', reason)
    ElMessage.success('已拒绝')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm('确认下架此商品?', '提示', { type: 'warning' })
    await updateMarketItemStatus(row.id, 'inactive')
    ElMessage.success('已下架')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleOnline = async (row) => {
  try {
    await ElMessageBox.confirm('确认重新上架此商品?', '提示')
    await updateMarketItemStatus(row.id, 'active')
    ElMessage.success('已上架')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleViolation = async (row) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入违规原因（用户可见）', '标记违规', {
      confirmButtonText: '确认标记',
      cancelButtonText: '取消',
      inputPlaceholder: '如：虚假信息、违禁物品、欺诈行为等',
      inputValidator: (val) => !!val?.trim() || '请输入违规原因',
      type: 'warning'
    })
    await updateMarketItemStatus(row.id, 'violation', reason)
    ElMessage.success('已标记违规')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除此商品?', '警告', { type: 'warning' })
    await deleteMarketItem(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleDetail = async (row) => {
  try {
    loading.value = true
    const res = await getMarketItemById(row.id)
    currentDetail.value = res.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  } finally {
    loading.value = false
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
    ElMessage.warning('请先选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 件商品?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteMarketItem(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

const handleExport = () => {
  const data = tableData.value.map(row => ({
    'ID': row.id,
    '商品名称': row.title,
    '卖家ID': row.userId,
    '价格': row.price,
    '状态': getStatusText(row.status),
    '发布时间': row.createTime
  }))
  const csv = [
    Object.keys(data[0]).join(','),
    ...data.map(row => Object.values(row).join(','))
  ].join('\n')
  const blob = new Blob([`\ufeff${csv}`], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `二手商品_${new Date().getTime()}.csv`
  link.click()
  ElMessage.success('导出成功')
}

const getStatusType = (status) => {
  const map = { 
    active: 'success',      // 出售中
    inactive: 'info',       // 已下架
    sold: 'warning',        // 已售出
    deleted: 'danger',      // 已删除
    violation: 'danger'     // 违规
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 
    active: '出售中', 
    inactive: '已下架', 
    sold: '已售出', 
    deleted: '已删除',
    violation: '违规'
  }
  return map[status] || status
}

onMounted(async () => {
  fetchData()
  // 加载分类列表
  try {
    const res = await getMarketCategories()
    categories.value = res.data || []
  } catch (e) {
    console.error('获取分类失败', e)
  }
})
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white">二手审核</h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.keyword" placeholder="搜索商品名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.category" placeholder="分类筛选" clearable @change="handleSearch" style="width: 130px">
          <el-option label="全部分类" value="" />
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.name" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="状态筛选" clearable @change="handleSearch" style="width: 130px">
          <el-option label="全部状态" value="" />
          <el-option label="出售中" value="active" />
          <el-option label="已下架" value="inactive" />
          <el-option label="已售出" value="sold" />
          <el-option label="违规" value="violation" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button :loading="loading" @click="fetchData">
          <el-icon class="mr-1"><Refresh /></el-icon>刷新
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">导出</el-button>
      </div>
    </div>
    
    <!-- AI 扫描组件 -->
    <AiScanner 
      ref="aiScannerRef"
      :tableData="tableData"
      :parseImages="parseImages"
      @update:scanResults="handleScanResultsUpdate"
    />
    
    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" @selection-change="handleSelectionChange" :row-class-name="tableRowClassName">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image v-if="row.images" :src="parseImages(row.images)[0]" :preview-src-list="parseImages(row.images)" fit="cover" style="width: 60px; height: 40px; border-radius: 4px;" />
            <span v-else class="text-slate-500">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品名称" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="卖家" width="150">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <el-avatar :size="28" :src="getImageUrl(row.sellerAvatar)">
                <template #error>
                  <div class="w-7 h-7 rounded-full bg-gradient-to-r from-indigo-500 to-purple-500 flex items-center justify-center text-white text-xs">{{ row.sellerName?.charAt(0) || 'U' }}</div>
                </template>
              </el-avatar>
              <span class="text-white text-sm">{{ row.sellerName || '未知' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="AI 建议" width="200">
          <template #default="{ row }">
            <template v-if="getItemRiskStatus(row.id)">
              <el-tag v-if="getItemRiskStatus(row.id).riskStatus === 'danger'" type="danger" size="small" effect="dark">
                🚫 威胁: {{ getItemRiskStatus(row.id).riskLabel }}
              </el-tag>
              <el-tag v-else-if="getItemRiskStatus(row.id).riskStatus === 'safe'" type="success" size="small" effect="dark">
                ✅ 安全
              </el-tag>
              <el-tag v-else type="warning" size="small" effect="dark">
                ⚠️ {{ getItemRiskStatus(row.id).riskLabel }}
              </el-tag>
            </template>
            <span v-else class="text-slate-500 text-xs font-mono">WAITING SCAN</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button type="info" link @click="handleDetail(row)">详情</el-button>
            <!-- 待审核：可通过/拒绝 -->
            <el-button v-if="row.status === 'pending'" type="success" link @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 'pending'" type="warning" link @click="handleReject(row)">拒绝</el-button>
            <!-- 在售中：可下架/标记违规 -->
            <el-button v-if="row.status === 'active'" type="warning" link @click="handleOffline(row)">下架</el-button>
            <el-button v-if="row.status === 'active'" type="danger" link @click="handleViolation(row)">违规</el-button>
            <!-- 已下架/已拒绝：可重新上架 -->
            <el-button v-if="row.status === 'inactive' || row.status === 'rejected'" type="success" link @click="handleOnline(row)">上架</el-button>
            <!-- 违规商品：可解除 -->
            <el-button v-if="row.status === 'violation'" type="primary" link @click="handleOnline(row)">解除</el-button>
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
    <el-dialog v-model="detailVisible" title="商品详情" width="550px" class="dark-dialog">
      <div class="space-y-4">
        <div class="flex"><span class="w-24 text-slate-400">商品ID：</span><span class="text-white">{{ currentDetail.id }}</span></div>
        <div class="flex"><span class="w-24 text-slate-400">商品名称：</span><span class="text-white">{{ currentDetail.title }}</span></div>
        <div class="flex"><span class="w-24 text-slate-400">价格：</span><span class="text-white">￥{{ currentDetail.price }}</span></div>
        <div class="flex items-center"><span class="w-24 text-slate-400">卖家：</span>
          <div class="flex items-center gap-2">
            <el-avatar :size="32" :src="getImageUrl(currentDetail.sellerAvatar)">
              <template #error>
                <div class="w-8 h-8 rounded-full bg-gradient-to-r from-indigo-500 to-purple-500 flex items-center justify-center text-white">{{ currentDetail.sellerName?.charAt(0) || 'U' }}</div>
              </template>
            </el-avatar>
            <span class="text-white">{{ currentDetail.sellerName || '未知' }}</span>
          </div>
        </div>
        <div class="flex"><span class="w-24 text-slate-400">状态：</span><el-tag :type="getStatusType(currentDetail.status)" size="small">{{ getStatusText(currentDetail.status) }}</el-tag></div>
        <div class="flex"><span class="w-24 text-slate-400">发布时间：</span><span class="text-white">{{ currentDetail.createTime }}</span></div>
        <div><span class="text-slate-400">商品描述：</span><p class="text-white mt-2 p-3 bg-white/5 rounded-lg">{{ currentDetail.description }}</p></div>
        <div v-if="currentDetail.images"><span class="text-slate-400">图片：</span><div class="mt-2 flex gap-2"><img v-for="(img, idx) in parseImages(currentDetail.images)" :key="idx" :src="img" class="w-24 h-24 rounded-lg object-cover" /></div></div>
      </div>
      <template #footer><el-button @click="detailVisible = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* AI 扫描违规行高亮样式 - 优化透明度 */
:deep(.danger-row) {
  background: rgba(244, 63, 94, 0.15) !important;
  box-shadow: inset 2px 0 0 #f43f5e;
}

:deep(.danger-row:hover > td) {
  background: rgba(244, 63, 94, 0.25) !important;
}

:deep(.error-row) {
  background: rgba(245, 158, 11, 0.1) !important;
  box-shadow: inset 2px 0 0 #f59e0b;
}

:deep(.error-row:hover > td) {
  background: rgba(245, 158, 11, 0.15) !important;
}

:deep(.hover-row-glow:hover > td) {
  background-color: rgba(99, 102, 241, 0.08) !important;
}
</style>