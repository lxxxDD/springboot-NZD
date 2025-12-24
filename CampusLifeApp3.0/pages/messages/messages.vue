<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <view class="content-wrapper">
      <u-navbar
        title=""
        :fixed="false"
        :autoBack="false"
        bgColor="transparent"
        leftIcon=""
        :border="false"
      ></u-navbar>

      <view class="page-header animate-enter" style="--delay: 0s">
        <text class="header-title">消息中心</text>

        <view
            v-if="totalUnread > 0"
            class="mark-read-btn animate-fade-in"
            @click="confirmMarkAllRead"
        >
          <u-icon name="checkmark-circle" color="#64748B" size="14"></u-icon>
          <text>全部已读</text>
        </view>
      </view>

      <view class="search-section animate-enter" style="--delay: 0.1s">
        <u-search
            placeholder="搜索联系人 / 聊天记录"
            :showAction="false"
            bgColor="#fff"
            height="44"
            shape="round"
            searchIconColor="#94A3B8"
            placeholderColor="#94A3B8"
            :inputStyle="{fontSize: '14px', color: '#1E293B'}"
        ></u-search>
      </view>

      <scroll-view scroll-y class="msg-list">
        <view class="list-inner">
          <u-swipe-action>
            <view
                v-for="(item, index) in msgList"
                :key="item.id"
                class="msg-card-wrap animate-slide-in"
                :style="{ animationDelay: index * 0.05 + 0.2 + 's' }"
            >
              <u-swipe-action-item
                  :options="swipeOptions"
                  :name="item.id"
                  @click="onSwipeClick"
              >
                <view class="msg-item" @click="openChat(item)" hover-class="item-hover">
                  <view class="avatar-box">
                    <u-avatar :src="item.avatar" size="52" shape="circle"></u-avatar>
                    <view v-if="item.online" class="status-dot online"></view>
                  </view>

                  <view class="content-box">
                    <view class="row-top">
                      <text class="name">{{ item.name }}</text>
                      <text class="time">{{ item.time }}</text>
                    </view>
                    <view class="row-bottom">
                      <text class="msg-text u-line-1" :class="{ 'highlight': item.unread > 0 }">
                        {{ item.lastMsg }}
                      </text>
                      <view class="badge-wrap" v-if="item.unread > 0">
                        <view class="badge">
                          {{ item.unread > 99 ? '99+' : item.unread }}
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
              </u-swipe-action-item>
            </view>
          </u-swipe-action>
        </view>

        <view v-if="msgList.length === 0" class="empty-state animate-enter" style="--delay: 0.2s">
          <u-empty mode="message" text="暂无消息" iconSize="80"></u-empty>
        </view>

        <view style="height: 100px"></view>
      </scroll-view>
    </view>

    <BottomNav active="messages" />

    <u-modal
        :show="showConfirmModal"
        title="忽略未读"
        content="确定要将所有消息标记为已读吗？"
        showCancelButton
        confirmColor="#6366F1"
        @confirm="handleMarkAllRead"
        @cancel="showConfirmModal = false"
    ></u-modal>
  </view>
</template>

<script setup>
import BottomNav from '@/components/BottomNav.vue'
import { getConversations, deleteConversation, markAsRead } from '@/api/messages.js'
import { ref, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { isLoggedIn } from '@/utils/routeGuard.js'

const statusBarHeight = ref(20)
const headerPaddingRight = ref(0)
const loading = ref(false)

onLoad(() => {
  const sys = uni.getSystemInfoSync()
  statusBarHeight.value = sys.statusBarHeight || 20
  
  // #ifdef MP-WEIXIN
  const menuButton = uni.getMenuButtonBoundingClientRect()
  headerPaddingRight.value = sys.windowWidth - menuButton.left + 10
  // #endif
})

onShow(() => {
  // 未登录直接跳转登录页
  if (!isLoggedIn()) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }
  loadConversations()
  
  // 监听新消息
  uni.$on('websocket-message', handleNewMessage)
})

import { onHide, onUnload } from '@dcloudio/uni-app'

onHide(() => {
  uni.$off('websocket-message', handleNewMessage)
})

onUnload(() => {
  uni.$off('websocket-message', handleNewMessage)
})

function handleNewMessage(msg) {
  console.log('消息列表收到新消息', msg)
  // 查找会话
  const index = msgList.value.findIndex(item => item.userId == msg.senderId)
  
  if (index > -1) {
    // 更新现有会话
    const item = msgList.value[index]
    item.lastMsg = msg.messageType === 'image' ? '[图片]' : msg.content
    item.time = '刚刚'
    item.unread += 1
    
    // 移到顶部
    msgList.value.splice(index, 1)
    msgList.value.unshift(item)
  } else {
    // 新会话，重新加载列表
    loadConversations()
  }
}

const showConfirmModal = ref(false)
const msgList = ref([])

async function loadConversations() {
  loading.value = true
  try {
    const res = await getConversations()
    // 根据 API 文档，返回的是 ConversationVO 数组
    // 字段: id, userId, username, avatar, lastMessage, lastMessageTime, unreadCount
    const list = Array.isArray(res.data) ? res.data : (res.data.list || [])
    msgList.value = list.map(item => ({
      id: item.id,
      userId: item.userId,
      name: item.username,
      avatar: item.avatar || 'https://via.placeholder.com/100/6366f1/fff',
      lastMsg: formatLastMessage(item.lastMessage, item.lastMessageType),
      time: formatTime(item.lastMessageTime),
      unread: item.unreadCount || 0,
      online: false
    }))
  } catch (err) {
    console.error('加载会话列表失败:', err)
  } finally {
    loading.value = false
  }
}

function formatLastMessage(msg, msgType) {
  if (!msg) return ''
  if (msgType === 'image') return '[图片]'
  return msg
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return `${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  if (diff < 604800000) return ['周日','周一','周二','周三','周四','周五','周六'][date.getDay()]
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const totalUnread = computed(() => {
  return msgList.value.reduce((sum, item) => sum + item.unread, 0)
})

const swipeOptions = [
  { text: '置顶', style: { backgroundColor: '#3C9CFF' } },
  { text: '删除', style: { backgroundColor: '#f56c6c' } }
]

function confirmMarkAllRead() {
  showConfirmModal.value = true
}

async function handleMarkAllRead() {
  try {
    // 标记所有会话为已读
    for (const item of msgList.value) {
      if (item.unread > 0) {
        await markAsRead(item.id)
        item.unread = 0
      }
    }
    showConfirmModal.value = false
    uni.showToast({ title: '已全部标记为已读', icon: 'none' })
  } catch (err) {
    console.error('标记已读失败:', err)
  }
}

async function onSwipeClick(e) {
  const { name, index } = e
  if (index === 1) {
    try {
      await deleteConversation(name)
      const idx = msgList.value.findIndex(i => i.id === name)
      if (idx > -1) {
        msgList.value.splice(idx, 1)
        uni.showToast({ title: '已删除', icon: 'none' })
      }
    } catch (err) {
      console.error('删除会话失败:', err)
    }
  } else {
    uni.showToast({ title: '已置顶', icon: 'none' })
  }
}

async function openChat(item) {
  if (item.unread > 0) {
    try {
      await markAsRead(item.id)
      item.unread = 0
    } catch (err) {
      console.error('标记已读失败:', err)
    }
  }
  // 传递 userId 用于发送消息
  const url = `/pages/messages/chat?id=${item.id}&userId=${item.userId}&name=${item.name}&avatar=${encodeURIComponent(item.avatar)}`;
  uni.navigateTo({ url })
}
</script>

<style scoped lang="scss">
/* 变量 */
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;
$primary: #6366F1;

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.bg-decoration {
  position: absolute; top: -100px; right: -100px; width: 400px; height: 400px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.06) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
}

/* 核心内容容器 */
.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
  height: 0; /* 配合flex:1 */
}

/* 页面头部 (自定义导航下方) */
.page-header {
  padding: 4px 20px 16px; /* 顶部留白，因为 Navbar 已经占位了 */
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 28px;
  font-weight: 800;
  color: $text-main;
  letter-spacing: -0.5px;
}

/* 全部已读按钮 */
.mark-read-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 12px;
  background: rgba(255,255,255,0.8);
  border-radius: 100px;
  border: 1px solid #E2E8F0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);

  text { font-size: 12px; color: $text-sub; font-weight: 600; }
  &:active { opacity: 0.7; background: #F1F5F9; transform: scale(0.98); }
}

/* 搜索栏 */
.search-section {
  padding: 0 20px 16px;
}

/* 消息列表 */
.msg-list {
  flex: 1;
  height: 0;
}

.list-inner {
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 16px; /* 卡片间距 */
}

/* 卡片容器 */
.msg-card-wrap {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.06);
  overflow: hidden;
  transform: translateZ(0);
}

.msg-item {
  display: flex; align-items: center; gap: 16px;
  padding: 18px;
  background: #fff;
  position: relative; z-index: 10;
}
.item-hover { background-color: #F8FAFC; }

/* 头像 */
.avatar-box { position: relative; flex-shrink: 0; }
.status-dot {
  position: absolute; bottom: 2px; right: 2px; width: 12px; height: 12px;
  background: #10B981; border: 2px solid #fff; border-radius: 50%;
}

/* 内容 */
.content-box {
  flex: 1; display: flex; flex-direction: column; gap: 6px; overflow: hidden;
}
.row-top { display: flex; justify-content: space-between; align-items: center; }
.name { font-size: 16px; font-weight: 700; color: $text-main; }
.time { font-size: 11px; color: #94A3B8; font-weight: 500; }

.row-bottom { display: flex; justify-content: space-between; align-items: center; }
.msg-text {
  font-size: 14px; color: $text-sub; flex: 1; margin-right: 12px;
  &.highlight { color: $text-main; font-weight: 600; }
}

.badge-wrap { flex-shrink: 0; }
.badge {
  background: #EF4444; color: #fff; font-size: 10px; font-weight: 700;
  padding: 3px 7px; border-radius: 100px; min-width: 20px; text-align: center;
  box-shadow: 0 2px 6px rgba(239, 68, 68, 0.25);
}

.empty-state { margin-top: 80px; display: flex; justify-content: center; }

/* 动画 */
.animate-enter { opacity: 0; transform: translateY(15px); animation: fadeUp 0.5s ease-out forwards; animation-delay: var(--delay); }
.animate-slide-in { opacity: 0; transform: translateX(15px); animation: slideIn 0.4s ease-out forwards; }
.animate-fade-in { animation: fadeIn 0.4s ease-out; }

@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideIn { to { opacity: 1; transform: translateX(0); } }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
</style>