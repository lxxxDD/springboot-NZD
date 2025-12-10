<template>
  <view class="bottom-nav">
    <view class="item" :class="{active: active==='home'}" @click="go('/pages/home/home','home')">
      <text class="material-symbols-outlined">home</text>
      <text>首页</text>
    </view>
    <view class="item" :class="{active: active==='services'}" @click="go('/pages/services/food','services')">
      <text class="material-symbols-outlined">grid_view</text>
      <text>服务</text>
    </view>
    <view class="item" :class="{active: active==='market'}" @click="go('/pages/market/market','market')">
      <text class="material-symbols-outlined">storefront</text>
      <text>市集</text>
    </view>
    <view class="item" :class="{active: active==='messages'}" @click="go('/pages/messages/messages','messages')">
      <text class="material-symbols-outlined">chat</text>
      <text>消息</text>
    </view>
    <view class="item" :class="{active: active==='profile'}" @click="go('/pages/profile/profile','profile')">
      <text class="material-symbols-outlined">person</text>
      <text>我的</text>
    </view>
  </view>
</template>
<script setup>
import { defineProps } from 'vue'
const props = defineProps({ active: { type: String, default: 'home' } })
const go = (url,key)=>{
  if (props.active===key) return
  // 使用reLaunch避免叠栈
  uni.reLaunch({ url })
}
</script>
<style scoped lang="scss">
.bottom-nav { position: fixed; left:0; right:0; bottom:0; height: 60px; background:#fff; border-top:1px solid #e5e7eb; display:flex; justify-content:space-around; align-items:center; z-index: 999; box-shadow: 0 -4px 12px rgba(0,0,0,.05); }
.item { flex:1; display:flex; flex-direction:column; align-items:center; justify-content:center; color:#9ca3af; gap:2px; }
.item .material-symbols-outlined{ font-size:25px; line-height:1; }
.item.active{ color:#2563eb; font-weight:600; }
.item.active .material-symbols-outlined { font-variation-settings: 'FILL' 1; }
</style>

