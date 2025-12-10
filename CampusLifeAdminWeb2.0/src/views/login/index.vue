<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    // 调用后端登录接口
    const response = await axios.post('/api/admin/auth/login', {
      username: form.value.username,
      password: form.value.password
    })
    
    if (response.data.code === 200) {
      const data = response.data.data
      // 保存token
      localStorage.setItem('token', data.token)
      // 保存用户信息（包含角色和权限）
      localStorage.setItem('userInfo', JSON.stringify(data))
      
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (e) {
    console.error('登录错误:', e)
    ElMessage.error(e.response?.data?.message || '登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <div class="neon-card w-full max-w-md p-10">
      <!-- Logo -->
      <div class="text-center mb-10">
        <div class="w-16 h-16 mx-auto rounded-2xl bg-gradient-to-tr from-indigo-600 to-pink-600 flex items-center justify-center text-white text-2xl font-bold shadow-[0_0_30px_rgba(236,72,153,0.4)]">
          CL
        </div>
        <h1 class="mt-4 text-2xl font-bold text-white">
          Campus<span class="text-indigo-400">Hub</span> Admin
        </h1>
        <p class="mt-2 text-slate-500 text-sm">校园生活服务管理平台</p>
      </div>

      <!-- Form -->
      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            placeholder="管理员账号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="登录密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            class="w-full btn-glow !rounded-xl !h-12"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="text-center text-slate-600 text-xs mt-6">
        © 2024 CampusHub. All rights reserved.
      </div>
    </div>
  </div>
</template>
