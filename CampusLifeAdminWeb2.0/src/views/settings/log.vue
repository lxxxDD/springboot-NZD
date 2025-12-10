<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemLogs } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 20, level: '', module: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSystemLogs(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取日志失败:', error)
    ElMessage.error('获取日志失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.value.page = 1
  fetchData()
}

const handleReset = () => {
  queryParams.value = { page: 1, size: 20, level: '', module: '' }
  fetchData()
}

const getLevelConfig = (level) => {
  const map = { 
    info: { type: 'info', text: '信息', class: 'bg-slate-500/20 text-slate-400 border-slate-500/30' },
    warning: { type: 'warning', text: '警告', class: 'bg-amber-500/20 text-amber-400 border-amber-500/30' },
    error: { type: 'danger', text: '错误', class: 'bg-rose-500/20 text-rose-400 border-rose-500/30' },
    success: { type: 'success', text: '成功', class: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30' }
  }
  return map[level] || map.info
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="p-6">
    <BlurReveal>
      <div class="bg-slate-800/50 backdrop-blur-sm rounded-2xl border border-slate-700/50 shadow-2xl overflow-hidden">
        <!-- 搜索栏 -->
        <div class="p-6 border-b border-slate-700/50">
          <div class="flex gap-4">
            <el-select v-model="queryParams.level" placeholder="日志级别" clearable style="width: 150px">
              <el-option label="信息" value="info" />
              <el-option label="警告" value="warning" />
              <el-option label="错误" value="error" />
              <el-option label="成功" value="success" />
            </el-select>
            <el-input v-model="queryParams.module" placeholder="模块名称" clearable style="width: 200px" />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
        </div>

        <!-- 表格 -->
        <el-table :data="tableData" v-loading="loading" style="width: 100%" row-class-name="hover-row-glow">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag 
                :type="getLevelConfig(row.type).type" 
                size="small"
                class="!bg-transparent !border"
                :class="getLevelConfig(row.type).class"
              >
                {{ getLevelConfig(row.type).text }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="module" label="模块" width="150" />
          <el-table-column prop="title" label="操作" show-overflow-tooltip />
          <el-table-column prop="operatorName" label="操作人" width="120" />
          <el-table-column prop="createTime" label="时间" width="180" />
        </el-table>

        <!-- 分页 -->
        <div class="flex justify-end p-4">
          <el-pagination 
            v-model:current-page="queryParams.page" 
            v-model:page-size="queryParams.size" 
            :total="total" 
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next" 
            @change="fetchData" 
          />
        </div>
      </div>
    </BlurReveal>
  </div>
</template>

<style scoped>
:deep(.el-table) {
  background: transparent;
  color: #e2e8f0;
}
:deep(.el-table__header-wrapper) {
  background: rgba(30, 41, 59, 0.5);
}
:deep(.el-table th) {
  background: transparent;
  color: #cbd5e1;
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
}
:deep(.el-table tr) {
  background: transparent;
}
:deep(.el-table td) {
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
}
:deep(.hover-row-glow:hover > td) {
  background-color: rgba(99, 102, 241, 0.08) !important;
}
</style>
