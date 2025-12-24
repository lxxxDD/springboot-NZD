<template>
  <view class="page">
    <u-navbar title="通知" :autoBack="true" bgColor="#f7f8fa" placeholder></u-navbar>
    <scroll-view scroll-y class="content">
      <view class="list">
        <view class="item" v-for="n in list" :key="n.id" @click="handleRead(n)">
          <view class="icon-wrapper">
            <u-icon name="bell-fill" color="#2563eb" size="24"></u-icon>
          </view>
          <view class="body">
            <text class="notif-title">{{ n.title }}</text>
            <text class="msg">{{ n.message }}</text>
            <text class="time">{{ n.time }}</text>
          </view>
          <view v-if="!n.read" class="dot" />
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getNotifications, markAsRead } from '@/api/notifications.js'

const list = ref([])
const loading = ref(false)

onShow(() => {
  loadNotifications()
})

async function loadNotifications() {
  loading.value = true
  try {
    const res = await getNotifications({ page: 1, size: 50 })
    // API 返回 ResultMapStringObject，data 可能是 { list, total, ... } 或直接是数组
    const data = res.data
    const items = Array.isArray(data) ? data : (data.list || data.notifications || [])
    list.value = items.map(item => ({
      id: item.id,
      title: item.title,
      message: item.content || item.message,
      time: formatTime(item.createTime || item.createdAt),
      read: item.isRead || item.read || false
    }))
  } catch (err) {
    console.error('加载通知失败:', err)
  } finally {
    loading.value = false
  }
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

async function handleRead(item) {
  if (!item.read) {
    try {
      await markAsRead(item.id)
      item.read = true
    } catch (err) {
      console.error('标记已读失败:', err)
    }
  }
}
</script>

<style scoped lang="scss">
.page { background: #f7f8fa; min-height: 100vh; display: flex; flex-direction: column; }
.content { flex: 1; padding: 12px 16px; }
.list { display: flex; flex-direction: column; gap: 12px; }
.item {
  background: #fff; border-radius: 16px; padding: 16px; display: flex; gap: 16px; align-items: flex-start; position: relative;
  .icon-wrapper { width: 40px; height: 40px; border-radius: 50%; background: #e0e7ff; display: flex; align-items: center; justify-content: center; }
  .body { flex: 1; }
  .notif-title { font-size: 14px; font-weight: 700; color: #1e293b; display: block; }
  .msg { font-size: 13px; color: #475569; display: block; margin: 2px 0; }
  .time { font-size: 12px; color: #94a3b8; }
  .dot { width: 8px; height: 8px; background: #ef4444; border-radius: 50%; position: absolute; right: 16px; top: 16px; }
}
</style>