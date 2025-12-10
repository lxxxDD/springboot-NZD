<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFoodOrderList, getFoodOrderById, updateFoodOrderStatus } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, status: '', orderNo: '' })
const detailVisible = ref(false)
const currentOrder = ref({})

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待支付', value: 'pending' },
  { label: '已支付', value: 'paid' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getFoodOrderList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleComplete = async (row) => {
  try {
    await updateFoodOrderStatus(row.id, 'completed')
    ElMessage.success('订单已完成')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认取消此订单？取消后用户将收到退款', '提示', { type: 'warning' })
    await updateFoodOrderStatus(row.id, 'cancelled')
    ElMessage.success('订单已取消')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleDetail = async (row) => {
  try {
    const res = await getFoodOrderById(row.id)
    currentOrder.value = res.data || row
    detailVisible.value = true
  } catch (error) {
    currentOrder.value = row
    detailVisible.value = true
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchData()
}

const getStatusConfig = (status) => {
  const map = { 
    pending: { text: '待支付', type: 'warning', class: 'bg-amber-500/10 text-amber-500 border-amber-500/20' }, 
    paid: { text: '已支付', type: 'primary', class: 'bg-indigo-500/10 text-indigo-400 border-indigo-500/20' }, 
    completed: { text: '已完成', type: 'success', class: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20' }, 
    cancelled: { text: '已取消', type: 'info', class: 'bg-slate-500/10 text-slate-400 border-slate-500/20' } 
  }
  return map[status] || map.cancelled
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-indigo-400"><Tickets /></el-icon>
        订餐订单
      </h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.orderNo" placeholder="搜索订单号" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态筛选" clearable @change="handleSearch" style="width: 150px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button :loading="loading" @click="fetchData" class="btn-glow">
          <el-icon class="mr-1"><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>
    
    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" row-class-name="hover-row-glow">
          <el-table-column prop="orderNo" label="订单编号" width="220">
            <template #default="{ row }">
              <span class="font-mono text-slate-300 text-sm tracking-wide">{{ row.orderNo }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="userId" label="用户ID" width="100">
             <template #default="{ row }">
               <span class="text-slate-400">#{{ row.userId }}</span>
             </template>
          </el-table-column>
          
          <el-table-column prop="canteenName" label="取餐食堂">
            <template #default="{ row }">
              <div class="flex items-center gap-1.5 text-white">
                <el-icon class="text-pink-500"><Shop /></el-icon>
                {{ row.canteenName }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="totalAmount" label="金额" width="120">
            <template #default="{ row }">
              <span class="text-emerald-400 font-bold font-mono text-lg">¥{{ row.totalAmount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="pickupCode" label="取餐码" width="120">
            <template #default="{ row }">
              <div v-if="row.pickupCode" class="font-mono text-xl tracking-widest text-indigo-400 font-bold bg-indigo-500/10 px-2 py-1 rounded border border-indigo-500/20 text-center">
                {{ row.pickupCode }}
              </div>
              <span v-else class="text-slate-500 text-xs">待生成</span>
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
          
          <el-table-column prop="createTime" label="下单时间" width="180">
            <template #default="{ row }">
              <span class="text-xs text-slate-500 font-mono">{{ row.createTime }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 'paid'" type="success" link @click="handleComplete(row)" class="!font-bold">完成取餐</el-button>
              <el-button v-if="row.status === 'pending'" type="warning" link @click="handleCancel(row)">取消</el-button>
              <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="flex justify-end p-4">
          <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
        </div>
      </div>
    </BlurReveal>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="500px" class="dark-dialog">
      <div class="space-y-4">
        <div class="flex justify-between items-center">
          <span class="font-mono text-slate-400 text-sm">{{ currentOrder.orderNo }}</span>
          <el-tag :type="getStatusConfig(currentOrder.status).type" :class="getStatusConfig(currentOrder.status).class" class="!bg-transparent !border">
            {{ getStatusConfig(currentOrder.status).text }}
          </el-tag>
        </div>
        
        <div class="grid grid-cols-2 gap-3 text-sm">
          <div class="bg-white/5 p-3 rounded border border-white/5">
            <div class="text-slate-500 text-xs mb-1">用户ID</div>
            <div class="text-white font-mono">#{{ currentOrder.userId }}</div>
          </div>
          <div class="bg-white/5 p-3 rounded border border-white/5">
            <div class="text-slate-500 text-xs mb-1">取餐食堂</div>
            <div class="text-white">{{ currentOrder.canteenName }}</div>
          </div>
          <div class="bg-white/5 p-3 rounded border border-white/5">
            <div class="text-slate-500 text-xs mb-1">取餐码</div>
            <div class="text-indigo-400 font-mono text-xl font-bold">{{ currentOrder.pickupCode || '待生成' }}</div>
          </div>
          <div class="bg-white/5 p-3 rounded border border-white/5">
            <div class="text-slate-500 text-xs mb-1">订单金额</div>
            <div class="text-emerald-400 font-mono text-xl font-bold">¥{{ currentOrder.totalAmount }}</div>
          </div>
        </div>

        <div class="bg-white/5 p-3 rounded border border-white/5">
          <div class="text-slate-500 text-xs mb-2">菜品明细</div>
          <div v-if="currentOrder.items?.length" class="space-y-2">
            <div v-for="item in currentOrder.items" :key="item.id" class="flex justify-between items-center text-sm">
              <span class="text-white">{{ item.name }} x{{ item.quantity }}</span>
              <span class="text-slate-400 font-mono">¥{{ item.price * item.quantity }}</span>
            </div>
          </div>
          <div v-else class="text-slate-500 text-sm">暂无明细</div>
        </div>

        <div class="text-xs text-slate-500 font-mono">下单时间：{{ currentOrder.createTime }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false" class="!w-full">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
:deep(.hover-row-glow:hover > td) {
  background-color: rgba(99, 102, 241, 0.08) !important;
}
</style>