<template>
  <view class="page">
    <u-navbar title="个人信息管理" :autoBack="true" bgColor="#f5f5f5" placeholder></u-navbar>
    <scroll-view scroll-y class="content">
      <view class="form-card">
        <view class="field">
          <text class="label">头像</text>
          <view class="avatar-uploader">
            <image :src="user.avatar" class="avatar-preview" mode="aspectFill" @click="changeAvatar" />
            <text class="material-symbols-outlined arrow">chevron_right</text>
          </view>
        </view>
        <view class="field">
          <text class="label">昵称</text>
          <input v-model="user.name" class="input" />
        </view>
        <view class="field">
          <text class="label">学号</text>
          <text class="value">{{ user.id }}</text>
        </view>
        <view class="field">
          <text class="label">专业</text>
          <text class="value">Computer Science</text>
        </view>
      </view>
      <u-button type="primary" shape="circle" :loading="loading" :disabled="loading" customStyle="margin-top: 24px; width: 100%; height: 48px; background: #2563eb; border: none; font-size: 15px; font-weight: 600; box-shadow: 0 8px 24px rgba(37, 99, 235, .24);" @click="save">保存</u-button>
    </scroll-view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { getUserProfile, updateProfile } from '@/api/user.js'
import { uploadFile } from '@/api/request.js'
import { onLoad } from '@dcloudio/uni-app'
import { getFullImageUrl } from '@/utils/avatar.js'

const user = reactive({
  name: '',
  id: '',
  avatar: '',
  phone: ''
})
const loading = ref(false)
const uploading = ref(false)

onLoad(async () => {
  await loadUserInfo()
})

async function loadUserInfo() {
  try {
    const res = await getUserProfile()
    user.name = res.data.username || ''
    user.id = res.data.studentId || ''
    user.avatar = getFullImageUrl(res.data.avatar)
    user.phone = res.data.phone || ''
  } catch (err) {
    console.error('加载用户信息失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

async function changeAvatar() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempFilePath = res.tempFilePaths[0]
      
      // 显示loading
      uni.showLoading({ title: '上传中...', mask: true })
      uploading.value = true
      
      try {
        // 上传图片到服务器
        const uploadRes = await uploadFile(tempFilePath)
        user.avatar = uploadRes.data.url
        uni.hideLoading()
        uni.showToast({ title: '上传成功', icon: 'success' })
      } catch (err) {
        console.error('上传头像失败:', err)
        uni.hideLoading()
        uni.showToast({ title: '上传失败', icon: 'none' })
      } finally {
        uploading.value = false
      }
    }
  })
}

async function save() {
  if (!user.name || !user.name.trim()) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }
  
  loading.value = true
  uni.showLoading({ title: '保存中...', mask: true })
  
  try {
    await updateProfile({
      username: user.name,
      avatar: user.avatar,
      phone: user.phone
    })
    
    // 更新本地缓存
    const userInfo = uni.getStorageSync('userInfo') || {}
    userInfo.name = user.name
    userInfo.avatar = user.avatar
    uni.setStorageSync('userInfo', userInfo)
    
    uni.hideLoading()
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 800)
  } catch (err) {
    console.error('保存失败:', err)
    uni.hideLoading()
    uni.showToast({ 
      title: err.message || '保存失败', 
      icon: 'none' 
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.page { background: #f5f5f5; min-height: 100vh; }
.content { padding: 12px 16px; height: calc(100vh - 44px); box-sizing: border-box; }
.form-card { background: #fff; border-radius: 12px; padding: 0 16px; }
.field { display: flex; justify-content: space-between; align-items: center; padding: 16px 0; border-bottom: 1px solid #f3f4f6; }
.field:last-child { border-bottom: none; }
.label { font-size: 14px; color: #111827; }
.value { font-size: 14px; color: #6b7280; }
.input { font-size: 14px; color: #6b7280; text-align: right; }
.avatar-uploader { display: flex; align-items: center; gap: 8px; }
.avatar-preview { width: 48px; height: 48px; border-radius: 999px; }
.arrow { color: #9ca3af; }
.save-btn { margin-top: 24px; background: #2563eb; color: #fff; text-align: center; padding: 13px; border-radius: 999px; font-weight: 600; font-size: 15px; box-shadow: 0 8px 24px rgba(37, 99, 235, .24); }
</style>
