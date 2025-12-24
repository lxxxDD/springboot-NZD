<template>
  <view class="page">
    <u-navbar :autoBack="true" bgColor="#fff" leftIconColor="#1E293B" placeholder border>
      <template #center>
        <view class="nav-center">
          <view class="nav-avatar-box">
            <u-avatar :src="targetUser.avatar" size="32"></u-avatar>
          </view>
          <view class="nav-info">
            <text class="nav-name">{{ targetUser.name }}</text>
          </view>
        </view>
      </template>
      <template #right>
        <u-icon name="more-dot-fill" size="24" color="#1E293B"></u-icon>
      </template>
    </u-navbar>

    <scroll-view
        scroll-y
        class="chat-content"
        :scroll-into-view="scrollIntoViewId"
        :scroll-with-animation="true"
        @click="hideKeyboard"
    >
      <view class="content-inner">
        <view class="time-divider">
          <text>上午 10:23</text>
        </view>

        <view
            v-for="(msg, index) in messageList"
            :key="msg.id"
            :id="'msg-' + index"
            class="msg-row"
            :class="{ 'is-me': msg.isMe }"
        >
          <u-avatar
              class="msg-avatar"
              :src="msg.isMe ? user.avatar : targetUser.avatar"
              size="40"
          ></u-avatar>

          <view class="msg-bubble-wrapper">
            <view class="msg-bubble" :class="{ 'msg-image': msg.messageType === 'image' }">
              <image 
                v-if="msg.messageType === 'image'" 
                :src="msg.content" 
                mode="widthFix" 
                class="bubble-image"
                @click="previewImage(msg.content)"
              ></image>
              <text v-else class="bubble-text">{{ msg.content }}</text>
            </view>
            <text v-if="msg.isMe" class="msg-status">已读</text>
          </view>
        </view>

        <view style="height: 20px"></view>
      </view>
    </scroll-view>

    <view class="input-area animate-slide-up">
      <view class="input-toolbar">
        <view class="icon-btn" @click="showEmojiTip">
          <text style="font-size: 26px;">😊</text>
        </view>

        <view class="input-box">
          <input
              v-model="inputText"
              class="input-field"
              placeholder="发送消息..."
              confirm-type="send"
              :focus="isFocus"
              @confirm="sendMessage"
          />
        </view>

        <view class="icon-btn" @click="handleSendClick">
          <view v-if="inputText.trim()" class="send-btn">
            <u-icon name="arrow-up" size="18" color="#fff" bold></u-icon>
          </view>
          <u-icon v-else name="plus-circle" size="26" color="#64748B"></u-icon>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getChatMessages, sendMessage as sendMessageApi } from '@/api/messages.js'
import { uploadFile } from '@/api/request.js'
import { baseURL } from '@/api/request.js'

// 获取当前用户信息
const userInfo = uni.getStorageSync('userInfo') || {}
const user = ref({
  avatar: formatAvatarUrl(userInfo.avatar)
})

function formatAvatarUrl(url) {
  if (!url) return 'https://via.placeholder.com/100'
  if (url.startsWith('http')) return url
  return baseURL + url
}

const targetUser = ref({
  id: '',        // 会话ID
  userId: '',    // 对方用户ID
  name: 'Chat',
  avatar: ''
})

const inputText = ref('')
const scrollIntoViewId = ref('')
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

// 消息列表
const messageList = ref([])

import { websocket } from '@/utils/websocket.js'

// ... (existing code)

onLoad((options) => {
  // 支持两种方式：从消息列表进入(有id)或从商品详情进入(有userId)
  if (options.id || options.userId) {
    targetUser.value.id = options.id || options.userId
    targetUser.value.userId = options.userId || options.id || ''
    targetUser.value.name = options.name || '用户'
    targetUser.value.avatar = formatAvatarUrl(options.avatar ? decodeURIComponent(options.avatar) : '')
  }
  
  console.log('聊天页面参数:', options)
  console.log('目标用户:', targetUser.value)
  
  loadMessages()
  
  // 如果是首次联系，自动发送"你好"
  if (options.autoSend === 'true') {
    setTimeout(() => {
      inputText.value = '你好'
      sendMessage()
    }, 500)
  }
  
  // 监听消息
  uni.$on('websocket-message', (msg) => {
    console.log('收到新消息', msg)
    // 判断是否是当前会话的消息
    // msg.senderId 是发送者ID
    if (msg.senderId == targetUser.value.userId) {
      // 添加到列表
      messageList.value.push({
        id: msg.id,
        content: msg.content,
        messageType: msg.messageType || 'text',
        isMe: false, // 既然是收到的，肯定不是自己发的
        senderAvatar: msg.senderAvatar,
        createTime: msg.createTime
      })
      scrollToBottom()
    } else {
      // 如果不是当前会话，可以显示红点提示等（这里暂不处理）
      // 注意：chat.vue 是在聊天详情页，如果收到其他人的消息，通常只提示
      // uni.showToast({ title: '收到新消息', icon: 'none' })
    }
  })
})

import { onUnload } from '@dcloudio/uni-app'
onUnload(() => {
  // 移除监听
  uni.$off('websocket-message')
})

// 加载聊天记录
async function loadMessages() {
  if (loading.value || !hasMore.value) return
  loading.value = true
  
  try {
    const res = await getChatMessages(targetUser.value.id, { page: page.value, size: 20 })
    // 根据 API 文档，返回 PageVOMessageVO
    // MessageVO 字段: id, senderId, senderName, senderAvatar, content, messageType, isMine, createTime
    const data = res.data
    const list = (data.list || []).map(item => ({
      id: item.id,
      content: item.content,
      messageType: item.messageType || 'text',
      isMe: item.isMine,
      senderAvatar: item.senderAvatar,
      createTime: item.createTime
    }))
    
    // 历史消息在前面
    if (page.value === 1) {
      messageList.value = list.reverse()
    } else {
      messageList.value = [...list.reverse(), ...messageList.value]
    }
    
    hasMore.value = data.hasMore
    page.value++
    scrollToBottom()
  } catch (err) {
    console.error('加载聊天记录失败:', err)
  } finally {
    loading.value = false
  }
}

function previewImage(url) {
  uni.previewImage({
    urls: [url]
  })
}



const isFocus = ref(false)

function showEmojiTip() {
  uni.showToast({ title: '表情功能开发中', icon: 'none' })
}

function handleSendClick() {
  if (inputText.value.trim()) {
    sendMessage()
  } else {
    handleChooseImage()
  }
}

function handleChooseImage() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFilePaths = res.tempFilePaths
      uploadAndSendImage(tempFilePaths[0])
    }
  })
}

async function uploadAndSendImage(filePath) {
  uni.showLoading({ title: '发送中...' })
  try {
    const res = await uploadFile(filePath)
    let imgUrl = ''
    if (res.data && res.data.url) {
      imgUrl = res.data.url
    } else if (res.url) {
      imgUrl = res.url
    }
    
    if (imgUrl) {
      await sendMessage(imgUrl, 'image')
    }
  } catch (e) {
    console.error('图片发送失败', e)
    uni.showToast({ title: '图片发送失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

async function sendMessage(content = '', type = 'text') {
  let msgContent = content
  if (type === 'text') {
    if (!inputText.value.trim()) return
    msgContent = inputText.value
    inputText.value = ''
    
    // 重新获取焦点
    isFocus.value = false
    nextTick(() => {
      isFocus.value = true
    })
  }
  
  // 先在本地添加消息（乐观更新）
  const tempId = Date.now()
  messageList.value.push({
    id: tempId,
    content: msgContent,
    messageType: type,
    isMe: true
  })
  scrollToBottom()
  
  try {
    // 调用发送消息 API
    // 请求参数: { receiverId, content, type }
    await sendMessageApi({
      receiverId: parseInt(targetUser.value.userId),
      content: msgContent,
      type: type
    })
  } catch (err) {
    console.error('发送消息失败:', err)
    // 发送失败，移除本地消息
    const idx = messageList.value.findIndex(m => m.id === tempId)
    if (idx > -1) {
      messageList.value.splice(idx, 1)
    }
    uni.showToast({ title: '发送失败', icon: 'none' })
  }
}

function scrollToBottom() {
  nextTick(() => {
    scrollIntoViewId.value = 'msg-' + (messageList.value.length - 1)
  })
}

function hideKeyboard() {
  uni.hideKeyboard()
}
</script>

<style scoped lang="scss">
/* 变量 */
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page {
  background: $bg-page;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 导航栏样式 */
.nav-center {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-avatar-box {
  position: relative;
}

.status-dot {
  position: absolute;
  bottom: 0; right: 0;
  width: 10px; height: 10px;
  background: #10B981;
  border: 2px solid #fff;
  border-radius: 50%;
}

.nav-info {
  display: flex;
  flex-direction: column;

  .nav-name { font-size: 15px; font-weight: 700; color: $text-main; line-height: 1.2; }
  .nav-status { font-size: 10px; color: #10B981; font-weight: 500; }
}

/* 聊天内容区 */
.chat-content {
  flex: 1;
  background: $bg-page;
  box-sizing: border-box;
  overflow: hidden; /* 防止整体滚动 */
}

.content-inner {
  padding: 16px 16px 0;
  min-height: 100%;
}

.time-divider {
  text-align: center;
  margin-bottom: 24px;
  text {
    font-size: 11px;
    color: #94A3B8;
    background: rgba(0,0,0,0.03);
    padding: 4px 12px;
    border-radius: 100px;
  }
}

/* 消息行 */
.msg-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 12px;

  &.is-me {
    flex-direction: row-reverse;

    .msg-bubble {
      background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%);
      color: #fff;
      border-top-left-radius: 18px;
      border-top-right-radius: 4px;
      box-shadow: 0 4px 12px rgba(79, 70, 229, 0.25);
    }

    .msg-bubble-wrapper {
      align-items: flex-end;
    }
  }

  /* 对方的消息样式 */
  &:not(.is-me) {
    .msg-bubble {
      background: #fff;
      color: $text-main;
      border-top-left-radius: 4px;
      border-top-right-radius: 18px;
      box-shadow: 0 2px 8px rgba(148, 163, 184, 0.08);
    }
  }
}

.msg-avatar {
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.msg-bubble-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.msg-bubble {
  padding: 12px 16px;
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
  font-size: 15px;
  line-height: 1.5;
  position: relative;
  word-break: break-word;
  
  &.msg-image {
    padding: 4px;
    background: transparent !important;
    box-shadow: none !important;
  }
}

.bubble-image {
  max-width: 140px;
  border-radius: 8px;
  display: block;
}

.msg-status {
  font-size: 10px;
  color: #CBD5E1;
  margin-top: 4px;
  margin-right: 4px;
}

/* 底部输入栏 */
.input-area {
  background: #fff;
  padding: 10px 16px;
  padding-bottom: calc(10px + env(safe-area-inset-bottom));
  box-shadow: 0 -4px 20px rgba(0,0,0,0.03);
  position: relative;
  z-index: 10;
}

.input-toolbar {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  gap: 12px;
}

.icon-btn {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.input-box {
  flex: 1;
  background: #F1F5F9;
  border-radius: 24px;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  min-height: 44px;
  box-sizing: border-box;
}

.input-field {
  flex: 1;
  font-size: 15px;
  color: $text-main;
  /* 如果需要支持多行，可以将 input 换成 textarea 并控制高度 */
}

.emoji-btn {
  margin-left: 8px;
}

.send-btn {
  width: 36px; height: 36px;
  background: $primary;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
  transition: transform 0.1s;

  &:active { transform: scale(0.9); }
}

/* 动画 */
.animate-slide-up {
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
</style>