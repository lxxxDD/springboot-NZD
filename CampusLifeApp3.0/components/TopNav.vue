<template>
  <view class="top-nav">
    <view class="left">
      <view class="logo">校</view>
      <text class="title">校园生活助手</text>
    </view>
    <view class="right">
      <view class="icon-btn" @click="$emit('notif')">
        <text class="material-symbols-outlined">notifications</text>
        <view v-if="badge" class="badge"></view>
      </view>
      <view class="avatar" @click="goProfile">
        <image :src="avatar" mode="aspectFill"></image>
      </view>
    </view>
  </view>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
const props = defineProps({
  badge: { type: Boolean, default: false },
  avatar: { type: String, default: '/static/avatar.png' }
})
const emit = defineEmits(['notif'])
const goProfile = () => {
  uni.navigateTo({ url: '/pages/profile/profile' })
}
</script>

<style scoped lang="scss">
.top-nav {
  position: sticky; top: 0; z-index: 50;
  display: flex; align-items: center; justify-content: space-between;
  height: 56px; padding: 0 16px; background: rgba(255,255,255,.95);
  backdrop-filter: blur(10px); box-shadow: 0 1px 6px rgba(0,0,0,.06);
}
.left { display:flex; align-items:center; gap:8px; }
.logo { width:32px; height:32px; border-radius:8px; background:#2563EB; color:#fff; display:flex; align-items:center; justify-content:center; font-weight:700; }
.title { font-size:16px; font-weight:700; color:#1f2937; }
.right { display:flex; align-items:center; gap:12px; }
.icon-btn { position:relative; width:36px; height:36px; border-radius:999px; display:flex; align-items:center; justify-content:center; background:#f3f4f6; color:#6b7280; .material-symbols-outlined { font-variation-settings: 'FILL' 1; } }
.badge { position:absolute; top:6px; right:6px; width:8px; height:8px; background:#ef4444; border:2px solid #fff; border-radius:999px; }
.avatar { width:36px; height:36px; border-radius:999px; overflow:hidden; border:1px solid #e5e7eb; }
.avatar image { width:100%; height:100%; }
</style>

