<template>
  <view class="page">
    <view class="nav-bar">
      <u-icon name="arrow-left" size="20" @click="goBack"></u-icon>
    </view>

    <view class="header-section">
      <text class="title">创建账号</text>
      <text class="subtitle">填写信息开启你的校园之旅</text>
    </view>

    <view class="form-container">
      <view class="grid-row">
        <u-input v-model="form.username" placeholder="姓名" border="none" customStyle="background:#f1f5f9; padding: 16px; border-radius: 16px;"></u-input>
        <u-input v-model="form.studentId" placeholder="学号" border="none" customStyle="background:#f1f5f9; padding: 16px; border-radius: 16px;"></u-input>
      </view>
      <u-input v-model="form.email" placeholder="校园邮箱" border="none" customStyle="background:#f1f5f9; padding: 16px; border-radius: 16px;"></u-input>
      <u-input v-model="form.password" type="password" placeholder="设置密码" border="none" customStyle="background:#f1f5f9; padding: 16px; border-radius: 16px;"></u-input>
      <u-input v-model="form.confirmPassword" type="password" placeholder="确认密码" border="none" customStyle="background:#f1f5f9; padding: 16px; border-radius: 16px;"></u-input>

      <u-button
        type="primary"
        text="立即注册"
        size="large"
        :loading="loading"
        @click="register"
        customStyle="border-radius: 16px; margin-top: 16px; height: 52px; background: #2563eb; box-shadow: 0 8px 24px -8px #2563eb;"
      ></u-button>
    </view>

    <view class="login-link">
      <text class="tip">已有账号?</text>
      <text class="link" @click="goBack">去登录</text>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { setUser } from '@/store/user.js'
import { register as registerApi } from '@/api/user.js'

const form = reactive({
  username: '',
  studentId: '',
  email: '',
  password: '',
  confirmPassword: ''
})
const loading = ref(false)

async function register() {
  // 表单验证
  if (!form.username.trim()) {
    return uni.showToast({ title: '请输入姓名', icon: 'none' })
  }
  if (!form.studentId.trim()) {
    return uni.showToast({ title: '请输入学号', icon: 'none' })
  }
  if (!form.email.trim()) {
    return uni.showToast({ title: '请输入邮箱', icon: 'none' })
  }
  if (!form.password.trim()) {
    return uni.showToast({ title: '请输入密码', icon: 'none' })
  }
  if (form.password !== form.confirmPassword) {
    return uni.showToast({ title: '两次密码不一致', icon: 'none' })
  }
  
  loading.value = true
  
  try {
    const res = await registerApi({
      studentId: form.studentId,
      username: form.username,
      password: form.password,
      email: form.email
    })
    
    // 保存 token 和用户信息
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('userInfo', res.data.userInfo)
    setUser(res.data.userInfo)
    uni.setStorageSync('isLoggedIn', true)
    
    uni.showToast({ title: '注册成功', icon: 'success' })
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/home/home' })
    }, 500)
  } catch (err) {
    console.error('注册失败:', err)
  } finally {
    loading.value = false
  }
}

function goBack() {
  uni.navigateBack();
}
</script>

<style scoped lang="scss">
.page { 
  display: flex; flex-direction: column; 
  min-height: 100vh; background: #fff; padding: 24px;
}

.nav-bar {
  position: absolute; top: calc(12px + var(--status-bar-height)); left: 16px;
  padding: 8px; background: #f1f5f9; border-radius: 50%;
}

.header-section {
  margin-top: 80px;
  .title { font-size: 24px; font-weight: 800; color: #1e293b; display: block; }
  .subtitle { font-size: 14px; color: #64748b; margin-top: 4px; margin-bottom: 32px; display: block; }
}

.form-container {
  width: 100%;
  display: flex; flex-direction: column; gap: 16px;
  .grid-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
}

.login-link {
  margin-top: 32px; text-align: center; font-size: 14px; color: #64748b;
  .link { color: #2563eb; font-weight: 700; margin-left: 4px; }
}
</style>
