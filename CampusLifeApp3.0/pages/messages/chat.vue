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
        @click="closeAllInput"
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

    <!-- 底部区域：包含输入框和表情面板 -->
    <view class="footer-area">
      <view class="input-area">
        <view class="input-toolbar">
          <view class="input-box">
            <input
                v-model="inputText"
                class="input-field"
                placeholder="想要不？聊聊看..."
                confirm-type="send"
                :focus="isFocus"
                @focus="onInputFocus"
                @confirm="sendMessage"
            />
            <view class="emoji-btn" @click.stop="toggleEmojiPanel">
              <!-- 图标动画：增加 active 类 -->
              <text 
                class="material-symbols-outlined emoji-icon" 
                :class="{ 'icon-active': showEmoji }"
              >sentiment_satisfied</text>
            </view>
          </view>

          <view class="icon-btn" @click="handleSendClick">
            <view v-if="inputText.trim()" class="send-btn animate-bounce-in">
              <u-icon name="arrow-up" size="18" color="#fff" bold></u-icon>
            </view>
            <u-icon v-else name="plus-circle" size="26" color="#64748B"></u-icon>
          </view>
        </view>
      </view>

      <!-- 表情面板 (移除 v-if，改为 class 控制高度动画) -->
      <view class="emoji-panel-wrapper" :class="{ 'panel-open': showEmoji }">
        <view class="emoji-panel-inner">
          <scroll-view scroll-y class="emoji-scroll">
            <view v-for="(group, gIndex) in emojiGroups" :key="gIndex" class="emoji-group">
              <view class="emoji-group-title">{{ group.title }}</view>
              <view class="emoji-grid">
                <view 
                  v-for="(emoji, index) in group.list" 
                  :key="index" 
                  class="emoji-item"
                  @click="addEmoji(emoji)"
                >
                  <text class="emoji-text">{{ emoji }}</text>
                </view>
              </view>
            </view>
            <!-- 底部留白 -->
            <view style="height: 30px;"></view>
          </scroll-view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'
import { getChatMessages, sendMessage as sendMessageApi } from '@/api/messages.js'
import { uploadFile } from '@/api/request.js'
import { baseURL } from '@/api/request.js'
import { websocket } from '@/utils/websocket.js'

// 状态管理
const showEmoji = ref(false)
const isFocus = ref(false)
const inputText = ref('')
const scrollIntoViewId = ref('')
const loading = ref(false)
const page = ref(1)
const hasMore = ref(true)

// === 定制：校园二手交易专用表情组 ===
const emojiGroups = [
  {
    title: '交易沟通',
    list: ['🤝','💰','🉑','🙅','🔪','👀','🤔','🆗','👋','🙏','📦','📍','🚇','⏳','💸','🚀']
  },
  {
    title: '物品状态',
    list: ['✨','🆕','🎁','🧾','🛡️','🔧','🧹','📉','🏷️','💯','✅','🔋','☠️']
  },
  {
    title: '校园闲置',
    list: ['📚','💻','📱','🎧','📷','⌚','🚲','🛵','🛹','🏀','🏸','🎸','👟','👗','👜','💄','🧴','🧸','🎮','🎫']
  }
]

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

const messageList = ref([])

onLoad((options) => {
  if (options.id || options.userId) {
    targetUser.value.id = options.id || options.userId
    targetUser.value.userId = options.userId || options.id || ''
    targetUser.value.name = options.name || '用户'
    targetUser.value.avatar = formatAvatarUrl(options.avatar ? decodeURIComponent(options.avatar) : '')
  }
  
  loadMessages()
  
  if (options.autoSend === 'true') {
    setTimeout(() => {
      inputText.value = '你好，这件东西还在吗？'
      sendMessage()
    }, 500)
  }
  
  uni.$on('websocket-message', (msg) => {
    if (msg.senderId == targetUser.value.userId) {
      messageList.value.push({
        id: msg.id,
        content: msg.content,
        messageType: msg.messageType || 'text',
        isMe: false, 
        senderAvatar: msg.senderAvatar,
        createTime: msg.createTime
      })
      scrollToBottom()
    }
  })
})

onUnload(() => {
  uni.$off('websocket-message')
})

// === 表情与键盘逻辑 ===

// 切换表情面板显示
function toggleEmojiPanel() {
  if (showEmoji.value) {
    // 关闭表情，打开键盘
    showEmoji.value = false
    // 稍微延迟，等待高度动画开始收缩后再聚焦，避免闪烁
    setTimeout(() => {
      isFocus.value = true
    }, 50)
  } else {
    // 打开表情，关闭键盘
    isFocus.value = false
    uni.hideKeyboard()
    
    // 这里的延时是为了让键盘先收起一部分，避免面板直接把输入框顶出屏幕外（视平台而定）
    // 或者让动画看起来是衔接键盘的
    setTimeout(() => {
      showEmoji.value = true
      scrollToBottom()
    }, 50)
  }
}

// 输入框获得焦点（键盘弹出）
function onInputFocus() {
  // 如果表情面板打开，先关掉它
  if (showEmoji.value) {
    showEmoji.value = false
  }
  isFocus.value = true
  scrollToBottom()
}

// 点击内容区域，收起所有（键盘和表情）
function closeAllInput() {
  uni.hideKeyboard()
  isFocus.value = false
  showEmoji.value = false
}

// 添加表情到输入框
function addEmoji(emoji) {
  inputText.value += emoji
}

// === 发送逻辑 ===

async function loadMessages() {
  if (loading.value || !hasMore.value) return
  loading.value = true
  
  try {
    const res = await getChatMessages(targetUser.value.id, { page: page.value, size: 20 })
    const data = res.data
    const list = (data.list || []).map(item => ({
      id: item.id,
      content: item.content,
      messageType: item.messageType || 'text',
      isMe: item.isMine,
      senderAvatar: item.senderAvatar,
      createTime: item.createTime
    }))
    
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
    
    // 发送后保持状态：如果是表情面板，保持打开；如果是键盘模式，保持聚焦
    if (!showEmoji.value) {
        nextTick(() => {
             isFocus.value = true
        })
    }
  }
  
  const tempId = Date.now()
  messageList.value.push({
    id: tempId,
    content: msgContent,
    messageType: type,
    isMe: true
  })
  scrollToBottom()
  
  try {
    await sendMessageApi({
      receiverId: parseInt(targetUser.value.userId),
      content: msgContent,
      type: type
    })
  } catch (err) {
    console.error('发送消息失败:', err)
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

.nav-info {
  display: flex;
  flex-direction: column;

  .nav-name { font-size: 15px; font-weight: 700; color: $text-main; line-height: 1.2; }
}

/* 聊天内容区 */
.chat-content {
  flex: 1;
  background: $bg-page;
  box-sizing: border-box;
  overflow: hidden; 
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

/* 底部区域（包含输入栏和面板） */
.footer-area {
  background: #fff;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.03);
  position: relative;
  z-index: 10;
}

/* 只有输入栏 */
.input-area {
  padding: 10px 16px;
  /* 移除底部 padding，由面板或 wrapper 负责撑开 */
  background: #fff; 
  position: relative; 
  z-index: 2;
}

.input-toolbar {
  display: flex;
  align-items: center;
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
}

.emoji-btn {
  margin-left: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:active {
      opacity: 0.7;
  }
}

/* 图标动画效果 */
.emoji-icon {
  font-size: 24px; 
  color: #64748B;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); /* 弹性过渡 */
  
  &.icon-active {
    color: $primary;
    transform: scale(1.15); /* 稍微放大 */
  }
}

.send-btn {
  width: 36px; height: 36px;
  background: $primary;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
  
  &.animate-bounce-in {
    animation: bounceIn 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  }
}

@keyframes bounceIn {
  0% { transform: scale(0); opacity: 0; }
  60% { transform: scale(1.1); opacity: 1; }
  100% { transform: scale(1); }
}

/* 表情面板容器动画 */
.emoji-panel-wrapper {
  height: 0;
  overflow: hidden;
  background: #F8FAFC;
  transition: height 0.3s cubic-bezier(0.25, 1, 0.5, 1); /* 流畅的展开曲线 */
  will-change: height;
  
  &.panel-open {
    height: 280px; /* 目标高度 */
    border-top: 1px solid #E2E8F0;
  }
}

.emoji-panel-inner {
  height: 280px; /* 内部内容保持固定高度，避免内容挤压 */
  padding-bottom: env(safe-area-inset-bottom);
}

.emoji-scroll {
  height: 100%;
}

.emoji-group {
  margin-bottom: 8px;
}

.emoji-group-title {
  font-size: 12px;
  color: #94A3B8;
  padding: 12px 16px 4px;
  font-weight: 500;
  background: #F8FAFC;
}

.emoji-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 5px;
}

.emoji-item {
  width: 12.5%;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  
  &:active {
    background-color: rgba(0,0,0,0.05);
    border-radius: 8px;
  }
}
</style>