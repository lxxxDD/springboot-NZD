<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus } from '@/api'
import { BASE_URL } from '@/api/request'
import BlurReveal from '@/components/animations/BlurReveal.vue'

// 处理图片URL，补全路径
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_URL + url
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, keyword: '' })
const selectedRows = ref([])

// 详情弹窗
const detailVisible = ref(false)
const currentUser = ref({})

const handleDetail = (row) => {
  currentUser.value = row
  detailVisible.value = true
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}该用户吗？`,
      '提示',
      { type: 'warning' }
    )
    const newStatus = row.status === 1 ? 0 : 1
    await updateUserStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchDisable = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择用户')
    return
  }
  try {
    const activeUsers = selectedRows.value.filter(row => row.status === 1)
    if (activeUsers.length === 0) {
      ElMessage.warning('选中的用户已全部被禁用')
      return
    }
    await ElMessageBox.confirm(`确认禁用选中的 ${activeUsers.length} 个用户?`, '警告', { type: 'warning' })
    await Promise.all(activeUsers.map(row => updateUserStatus(row.id, 0)))
    ElMessage.success('批量禁用成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量操作失败')
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <span class="w-2 h-8 bg-indigo-500 rounded-full shadow-[0_0_10px_rgba(99,102,241,0.5)]"></span>
        用户列表
      </h2>
      <div class="flex gap-4">
        <el-input 
          v-model="queryParams.keyword" 
          placeholder="搜索昵称 / ID / 手机号" 
          class="!w-72 search-glow"
          size="large"
          @keyup.enter="fetchData"
        >
          <template #prefix>
            <el-icon class="text-indigo-400"><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="danger" size="large" @click="handleBatchDisable" :disabled="selectedRows.length === 0">批量禁用</el-button>
        <el-button type="primary" size="large" class="btn-glow" :loading="loading" @click="fetchData">
          <el-icon class="mr-1"><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <BlurReveal :delay="0.2">
      <div class="neon-card p-0 overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%" row-class-name="hover-row-glow" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="100">
            <template #default="{ row }">
              <span class="font-mono text-slate-400">#{{ row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="用户身份" min-width="200">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                <el-avatar :size="36" :src="getImageUrl(row.avatar)" class="border border-white/10 shrink-0">
                  <template #error>
                    <div class="w-full h-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold">
                      {{ row.nickname?.charAt(0) || 'U' }}
                    </div>
                  </template>
                </el-avatar>
                <div>
                  <div class="text-white font-bold text-sm">{{ row.nickname }}</div>
                  <div class="text-xs text-slate-500 font-mono">@{{ row.username }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="phone" label="手机号" width="150">
             <template #default="{ row }">
               <div v-if="row.phone" class="flex items-center gap-2 text-slate-300">
                 <el-icon class="text-emerald-500"><Iphone /></el-icon>
                 <span class="font-mono text-sm">{{ row.phone }}</span>
               </div>
               <span v-else class="text-slate-600 text-xs">未绑定</span>
             </template>
          </el-table-column>
          
          <el-table-column prop="email" label="邮箱" min-width="180">
             <template #default="{ row }">
               <div v-if="row.email" class="flex items-center gap-2 text-slate-300">
                 <el-icon class="text-indigo-400"><Message /></el-icon>
                 <span class="font-mono text-sm truncate">{{ row.email }}</span>
               </div>
               <span v-else class="text-slate-600 text-xs">未绑定</span>
             </template>
          </el-table-column>
          
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag 
                :type="row.status === 1 ? 'success' : 'danger'" 
                effect="dark"
                class="!border-none"
                :class="row.status === 1 ? '!bg-emerald-500/20 !text-emerald-400' : '!bg-rose-500/20 !text-rose-400'"
              >
                <div class="flex items-center gap-1.5">
                  <span class="w-1.5 h-1.5 rounded-full" :class="row.status === 1 ? 'bg-emerald-400' : 'bg-rose-400'"></span>
                  {{ row.status === 1 ? '正常' : '已禁用' }}
                </div>
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="注册时间" width="180">
            <template #default="{ row }">
              <span class="text-slate-400 text-sm font-mono">{{ row.createTime }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="flex gap-2">
                <el-button 
                  :type="row.status === 1 ? 'danger' : 'success'" 
                  link 
                  size="small"
                  class="!font-bold"
                  @click="handleStatusChange(row)"
                >
                  {{ row.status === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="p-4 flex justify-end border-t border-white/5 bg-white/2">
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

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="500px" class="dark-dialog">
      <div class="relative overflow-hidden rounded-xl bg-slate-900 p-6 border border-white/5">
        <!-- 装饰背景 -->
        <div class="absolute top-0 right-0 w-32 h-32 bg-indigo-500/10 blur-[50px] rounded-full"></div>
        
        <div class="flex items-center gap-6 mb-8 relative z-10">
          <div class="relative">
            <div class="absolute -inset-1 bg-gradient-to-r from-indigo-500 to-pink-500 rounded-full blur opacity-40"></div>
            <el-avatar :size="80" :src="getImageUrl(currentUser.avatar)" class="relative border-2 border-slate-800">
              {{ currentUser.nickname?.charAt(0) }}
            </el-avatar>
          </div>
          <div>
            <h3 class="text-2xl text-white font-bold">{{ currentUser.nickname || currentUser.username || '未知用户' }}</h3>
            <p class="text-indigo-400 font-mono text-sm">ID: {{ currentUser.id }}</p>
          </div>
        </div>
        
        <div class="grid grid-cols-2 gap-4 text-sm relative z-10">
          <div class="p-3 bg-white/5 rounded-lg border border-white/5">
            <div class="text-slate-500 mb-1">账号</div>
            <div class="text-white font-mono">{{ currentUser.username }}</div>
          </div>
          <div class="p-3 bg-white/5 rounded-lg border border-white/5">
            <div class="text-slate-500 mb-1">手机</div>
            <div class="text-white font-mono">{{ currentUser.phone || '未绑定' }}</div>
          </div>
          <div class="p-3 bg-white/5 rounded-lg border border-white/5">
            <div class="text-slate-500 mb-1">邮箱</div>
            <div class="text-white font-mono">{{ currentUser.email || '未绑定' }}</div>
          </div>
          <div class="p-3 bg-white/5 rounded-lg border border-white/5">
            <div class="text-slate-500 mb-1">注册时间</div>
            <div class="text-white font-mono text-xs">{{ currentUser.createTime }}</div>
          </div>
        </div>
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