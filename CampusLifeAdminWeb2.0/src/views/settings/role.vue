<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, deleteRole, updateRoleStatus, createRole, updateRole } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, name: '' })
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')
const formLoading = ref(false)
const formRef = ref(null)
const form = ref({ id: null, name: '', description: '', permissions: '', status: 1 })

// 可用权限列表（根据路由自动生成）
const availablePermissions = ref([
  // 控制台
  { label: '控制台', value: 'dashboard', group: '控制台' },
  
  // 用户管理
  { label: '用户列表', value: 'user:list', group: '用户管理' },
  
  // 服务管理
  { label: '报修管理', value: 'service:repair', group: '服务管理' },
  { label: '维修人员', value: 'service:technician', group: '服务管理' },
  { label: '食堂管理', value: 'service:canteen', group: '服务管理' },
  { label: '菜品管理', value: 'service:food', group: '服务管理' },
  { label: '订餐订单', value: 'service:food-order', group: '服务管理' },
  
  // 运营中心
  { label: '二手审核', value: 'operation:secondhand', group: '运营中心' },
  { label: '分类管理', value: 'operation:category', group: '运营中心' },
  { label: '活动管理', value: 'operation:activity', group: '运营中心' },
  { label: '内容发布', value: 'operation:content', group: '运营中心' },
  
  // 财务中心
  { label: '充值记录', value: 'finance:recharge', group: '财务中心' },
  { label: '交易流水', value: 'finance:transaction', group: '财务中心' },
  
  // 系统设置
  { label: '管理员账号', value: 'settings:admin', group: '系统设置' },
  { label: '角色权限', value: 'settings:role', group: '系统设置' },
  { label: '系统日志', value: 'settings:log', group: '系统设置' }
])
const selectedPermissions = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateRoleStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  // 检查角色是否被使用
  if (row.adminCount && row.adminCount > 0) {
    ElMessage.error(`该角色正在被 ${row.adminCount} 个管理员使用，无法删除`)
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该角色?', '警告', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.response?.data?.message || '删除失败，该角色可能正在被使用')
  }
}

const handleAdd = () => {
  form.value = { id: null, name: '', description: '', permissions: '', status: 1 }
  selectedPermissions.value = []
  dialogTitle.value = '添加角色'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  form.value = { ...row }
  try {
    selectedPermissions.value = row.permissions ? JSON.parse(row.permissions) : []
  } catch {
    selectedPermissions.value = []
  }
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  formLoading.value = true
  try {
    form.value.permissions = JSON.stringify(selectedPermissions.value)
    if (form.value.id) {
      await updateRole(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createRole(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    formLoading.value = false
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
    ElMessage.warning('请先选择要删除的角色')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个角色?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteRole(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

onMounted(() => fetchData())
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-pink-400"><Lock /></el-icon>
        角色权限
      </h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.name" placeholder="搜索角色名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">
          <el-icon class="mr-1"><Plus /></el-icon>添加角色
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
          
          <el-table-column prop="name" label="角色名称">
             <template #default="{ row }">
               <span class="font-bold text-white">{{ row.name }}</span>
             </template>
          </el-table-column>
          
          <el-table-column prop="description" label="描述" show-overflow-tooltip>
             <template #default="{ row }">
               <span class="text-slate-400">{{ row.description }}</span>
             </template>
          </el-table-column>
          
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
               <el-tag 
                :type="row.status === 1 ? 'success' : 'danger'" 
                effect="dark"
                class="!bg-transparent !border"
                :class="row.status === 1 ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-400' : 'bg-rose-500/10 border-rose-500/20 text-rose-400'"
              >
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              <span class="font-mono text-xs text-slate-500">{{ row.createTime }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button :type="row.status === 1 ? 'danger' : 'success'" link @click="handleStatusChange(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="flex justify-end p-4">
          <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="fetchData" />
        </div>
      </div>
    </BlurReveal>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" class="dark-dialog">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="角色名称"><el-input v-model="form.name" placeholder="请输入角色名称" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入角色描述" /></el-form-item>
        <el-form-item label="权限">
          <div class="max-h-60 overflow-y-auto custom-scrollbar border border-white/10 rounded-lg p-3 bg-slate-900/50">
            <el-checkbox-group v-model="selectedPermissions">
              <div v-for="(group, groupName) in availablePermissions.reduce((acc, cur) => { (acc[cur.group] = acc[cur.group] || []).push(cur); return acc; }, {})" :key="groupName" class="mb-4 last:mb-0">
                <div class="text-xs text-indigo-400 font-bold mb-2">{{ groupName }}</div>
                <div class="grid grid-cols-2 gap-2">
                   <el-checkbox v-for="perm in group" :key="perm.value" :value="perm.value" class="!mr-0">
                    <span class="text-slate-300">{{ perm.label }}</span>
                  </el-checkbox>
                </div>
              </div>
            </el-checkbox-group>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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