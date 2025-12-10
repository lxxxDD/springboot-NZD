<script setup>
import { ref, onMounted } from 'vue'
import { getTransactionList } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'
import { getFullImageUrl } from '@/utils/image'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, type: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTransactionList(queryParams.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取交易列表失败:', error)
  } finally {
    loading.value = false
  }
}

const getTypeText = (type) => {
  const map = { 
    topup: { text: '充值', class: 'text-emerald-400' }, 
    recharge: { text: '充值', class: 'text-emerald-400' }, 
    payment: { text: '支出', class: 'text-rose-400' }, 
    consume: { text: '消费', class: 'text-rose-400' }, 
    income: { text: '收入', class: 'text-emerald-400' }, 
    refund: { text: '退款', class: 'text-amber-400' } 
  }
  return map[type] || { text: type, class: 'text-slate-400' }
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <h2 class="text-2xl font-bold text-white mb-6 flex items-center gap-2">
      <el-icon class="text-emerald-400"><Money /></el-icon>
      交易流水
    </h2>
    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" row-class-name="hover-row-glow">
        <el-table-column prop="id" label="ID" width="80">
           <template #default="{ row }">
             <span class="font-mono text-slate-400">#{{ row.id }}</span>
           </template>
        </el-table-column>
        
        <el-table-column label="用户" min-width="180">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-full bg-slate-800 border border-white/10 flex items-center justify-center overflow-hidden">
                 <img v-if="row.avatar" :src="getFullImageUrl(row.avatar)" class="w-full h-full object-cover" />
                 <span v-else class="text-xs text-slate-500 font-bold">{{ row.username?.charAt(0).toUpperCase() }}</span>
              </div>
              <div class="flex flex-col">
                <span class="text-slate-200 text-sm font-bold">{{ row.username || `UID:${row.userId}` }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
             <span :class="getTypeText(row.type).class" class="font-bold text-sm">{{ getTypeText(row.type).text }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="amount" label="金额" width="140">
          <template #default="{ row }">
            <div class="font-mono text-lg font-bold tracking-wide" :class="row.amount > 0 ? 'text-emerald-400' : 'text-rose-400'">
              {{ row.amount > 0 ? '+' : '' }}¥{{ row.amount }}
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="描述" show-overflow-tooltip min-width="200">
           <template #default="{ row }">
             <span class="text-slate-400">{{ row.description }}</span>
           </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="交易时间" width="180">
          <template #default="{ row }">
            <span class="font-mono text-xs text-slate-500">{{ row.createTime }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex justify-end p-4">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
        </div>
      </div>
    </BlurReveal>
  </div>
</template>

<style scoped>
:deep(.hover-row-glow:hover > td) {
  background-color: rgba(99, 102, 241, 0.08) !important;
}
</style>