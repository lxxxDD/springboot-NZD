<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminList, getAdminById, deleteAdmin, updateAdminStatus, resetAdminPassword, createAdmin, updateAdmin, getAllRoles } from '@/api'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const queryParams = ref({ page: 1, size: 10, username: '' })
const selectedRows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加管理员')
const formLoading = ref(false)
const formRef = ref(null)
const form = ref({ id: null, username: '', password: '', nickname: '', email: '', phone: '', roleId: null, status: 1 })
const roleList = ref([])
const currentAdminId = ref(null) // 当前登录的管理员ID
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

// 获取当前登录管理员ID
const getCurrentAdminId = () => {
  try {
    const adminInfo = JSON.parse(localStorage.getItem('adminInfo') || '{}')
    currentAdminId.value = adminInfo.id
  } catch { currentAdminId.value = null }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAdminList(queryParams.value)
    tableData.value = res.data?.records || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    console.error('获取管理员列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateAdminStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleResetPassword = async (row) => {
  try {
    const { value: newPassword } = await ElMessageBox.prompt('请输入新密码', '重置密码', {
      confirmButtonText: '确认重置',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入6位以上新密码',
      inputType: 'password',
      inputValidator: (val) => {
        if (!val || val.length < 6) return '密码长度不能少于6位'
        return true
      }
    })
    await resetAdminPassword(row.id, newPassword)
    ElMessage.success('密码已重置')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  if (row.id === currentAdminId.value) {
    ElMessage.error('不能删除当前登录的管理员')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该管理员?', '警告', { type: 'warning' })
    await deleteAdmin(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const fetchRoles = async () => {
  try {
    const res = await getAllRoles()
    roleList.value = res.data || []
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

const handleAdd = () => {
  form.value = { id: null, username: '', password: '', nickname: '', email: '', phone: '', roleId: null, status: 1 }
  dialogTitle.value = '添加管理员'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  form.value = { ...row, password: '' }
  dialogTitle.value = '编辑管理员'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    formLoading.value = true
    try {
      if (form.value.id) {
        await updateAdmin(form.value.id, form.value)
        ElMessage.success('更新成功')
      } else {
        await createAdmin(form.value)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchData()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '操作失败')
    } finally {
      formLoading.value = false
    }
  })
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
    ElMessage.warning('请先选择要删除的管理员')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 个管理员?`, '警告', { type: 'warning' })
    await Promise.all(selectedRows.value.map(row => deleteAdmin(row.id)))
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

onMounted(() => {
  getCurrentAdminId()
  fetchRoles()
  fetchData()
})
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white flex items-center gap-2">
        <el-icon class="text-indigo-400"><UserFilled /></el-icon>
        管理员账号
      </h2>
      <div class="flex gap-4">
        <el-input v-model="queryParams.username" placeholder="搜索用户名" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">批量删除</el-button>
        <el-button type="primary" class="btn-glow" @click="handleAdd">
          <el-icon class="mr-1"><Plus /></el-icon>添加管理员
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
          
          <el-table-column label="管理员" min-width="180">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                 <div class="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center border border-white/10 text-indigo-400 font-bold">
                    {{ row.username.charAt(0).toUpperCase() }}
                 </div>
                 <div>
                    <div class="text-white font-bold">{{ row.username }}</div>
                    <div class="text-xs text-slate-500">{{ row.nickname }}</div>
                 </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="角色" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.roleName" type="primary" effect="plain" class="!bg-indigo-500/10 !border-indigo-500/30 !text-indigo-400">{{ row.roleName }}</el-tag>
              <span v-else class="text-slate-500 text-xs">未分配</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="email" label="联系方式" min-width="180">
             <template #default="{ row }">
                <div class="text-sm text-slate-300">{{ row.email }}</div>
                <div class="text-xs text-slate-500 font-mono">{{ row.phone }}</div>
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
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="lastLoginTime" label="最后登录" width="180">
            <template #default="{ row }">
               <span class="font-mono text-xs text-slate-500">{{ row.lastLoginTime || '从未登录' }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button :type="row.status === 1 ? 'danger' : 'success'" link @click="handleStatusChange(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名"><el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="密码" v-if="!form.id"><el-input v-model="form.password" type="password" placeholder="请输入密码" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" placeholder="请输入昵称" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="请输入邮箱" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone" placeholder="请输入手机" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="选择角色" style="width: 100%">
            <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
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