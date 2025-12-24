<template>
  <view class="page">
    <view class="logo-container">
      <view class="logo-bg">
        <u-icon name="school" color="#fff" size="40"></u-icon>
      </view>
    </view>

    <text class="title">CampusPal</text>
    <text class="subtitle">你的智能校园生活助手</text>

    <view class="form-container">
      <u-input
        v-model="form.studentId"
        placeholder="请输入学号 / 邮箱"
        border="none"
        customStyle="background:#f1f5f9; padding: 16px; border-radius: 16px;"
      ></u-input>
      <u-input
        v-model="form.password"
        type="password"
        placeholder="请输入密码"
        border="none"
        customStyle="background:#f1f5f9; padding: 16px; border-radius: 16px; margin-top: 16px;"
      ></u-input>

      <view class="forgot-password" @click="handleForgotPassword">
        <text>忘记密码?</text>
      </view>

      <u-button
        type="primary"
        text="登录"
        size="large"
        @click="login"
        customStyle="border-radius: 16px; margin-top: 16px; height: 52px; background: #2563eb; box-shadow: 0 8px 24px -8px #2563eb;"
      ></u-button>
    </view>

    <view class="register-link">
      <text class="tip">还没有账号?</text>
      <text class="link" @click="goToRegister">立即注册</text>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { setUser } from '@/store/user.js'
import { login as loginApi, updateProfile } from '@/api/user.js'
import { redirectAfterLogin } from '@/utils/routeGuard.js'

const form = reactive({
  studentId: '',
  password: ''
})
const loading = ref(false)

async function login() {
  // 表单验证
  if (!form.studentId.trim()) {
    return uni.showToast({ title: '请输入学号/邮箱', icon: 'none' })
  }
  if (!form.password.trim()) {
    return uni.showToast({ title: '请输入密码', icon: 'none' })
  }
  
  loading.value = true
  
  try {
    const res = await loginApi({
      studentId: form.studentId,
      password: form.password
    })
    
    // 先清除旧数据
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('isLoggedIn')
    
    // 保存新的 token 和用户信息
    console.log('=== 登录成功，保存用户信息 ===', res.data.userInfo)
    
    let userInfo = res.data.userInfo
    
    // 如果没有头像，随机分配一个本地头像
    if (!userInfo.avatar) {
      const randomNum = Math.floor(Math.random() * 4) + 1
      const randomAvatar = `/static/avatar/${randomNum}.png`
      userInfo.avatar = randomAvatar
      
      // 异步更新到后端，不阻塞登录流程
      updateProfile({ avatar: randomAvatar }).catch(e => {
        console.error('自动设置默认头像失败', e)
      })
    }
    
    uni.setStorageSync('token', res.data.token)
    uni.setStorageSync('userInfo', userInfo)
    setUser(userInfo)
    uni.setStorageSync('isLoggedIn', true)
    
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      // 登录后跳转到之前保存的页面，如果没有则跳转到首页
      redirectAfterLogin()
    }, 500)
  } catch (err) {
    console.error('登录失败:', err)
  } finally {
    loading.value = false
  }
}

function goToRegister() {
  uni.navigateTo({ url: '/pages/register/register' });
}

function handleForgotPassword() {
  uni.showModal({
    title: '忘记密码',
    content: '请联系学校管理员重置密码，或前往学校行政服务中心办理。',
    showCancel: false,
    confirmText: '我知道了'
  })
}
</script>

<style scoped lang="scss">
.page { 
  display: flex; flex-direction: column; align-items: center; justify-content: center; 
  min-height: 100vh; background: #fff; padding: 24px;
}

.logo-container {
  margin-bottom: 24px;
  .logo-bg {
    width: 80px; height: 80px; background-color: #2563eb; border-radius: 24px;
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 12px 32px -8px rgba(37, 99, 235, 0.5);
  }
}

.title { font-size: 28px; font-weight: 800; color: #1e293b; margin-bottom: 8px; }
.subtitle { font-size: 14px; color: #64748b; margin-bottom: 40px; }

.form-container {
  width: 100%; max-width: 384px;
  .forgot-password { text-align: right; margin-top: 12px; font-size: 12px; color: #2563eb; font-weight: 600; }
}

.register-link {
  margin-top: 48px; font-size: 14px; color: #64748b;
  .link { color: #2563eb; font-weight: 700; margin-left: 4px; }
}
</style>
