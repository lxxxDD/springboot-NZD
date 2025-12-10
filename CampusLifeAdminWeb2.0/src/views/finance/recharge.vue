<script setup>
import { ref, onMounted } from 'vue'
import { getTransactionList } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, type: 'topup', keyword: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getTransactionList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取充值记录失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchData()
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
         <el-icon class="text-emerald-500"><Wallet /></el-icon>
         充值记录
      </h2>
      <div class="flex gap-4">
        <el-input 
          v-model="queryParams.keyword" 
          placeholder="搜索用户ID" 
          clearable 
          style="width: 200px" 
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" class="btn-glow" @click="handleSearch">搜索</el-button>
        <el-button :loading="loading" @click="fetchData">
          <el-icon class="mr-1"><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" row-class-name="hover-row-glow">
          <el-table-column prop="id" label="ID" width="80">
            <template #default="{ row }">
              <span class="font-mono text-slate-400">#{{ row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="userId" label="用户ID" width="100">
             <template #default="{ row }">
               <span class="text-indigo-300 font-mono">UID:{{ row.userId }}</span>
             </template>
          </el-table-column>
          
          <el-table-column prop="amount" label="充值金额">
            <template #default="{ row }">
              <div class="flex items-center gap-1">
                <span class="text-slate-500 text-xs">CNY</span>
                <span class="text-emerald-400 font-mono text-xl font-bold">+{{ row.amount }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="balanceAfter" label="充值后余额" width="150">
            <template #default="{ row }">
              <span class="text-slate-300 font-mono">¥{{ row.balanceAfter }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="description" label="描述" show-overflow-tooltip>
             <template #default="{ row }">
               <span class="text-slate-400 text-sm">{{ row.description }}</span>
             </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="充值时间" width="180">
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