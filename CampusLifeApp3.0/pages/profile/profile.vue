<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <!-- 未登录状态 -->
    <view v-if="!isLoggedIn" class="not-logged-in">
      <view class="empty-container">
        <view class="empty-icon">
          <u-icon name="account" size="80" color="#CBD5E1"></u-icon>
        </view>
        <text class="empty-title">您还未登录</text>
        <text class="empty-desc">登录后即可查看个人信息、管理订单等更多功能</text>
        <u-button
          type="primary"
          text="立即登录"
          @click="goLogin"
          customStyle="border-radius: 16px; margin-top: 24px; width: 200px; height: 48px; background: #2563eb;"
        ></u-button>
      </view>
      <BottomNav active="profile" />
    </view>

    <!-- 已登录状态 -->
    <scroll-view v-else scroll-y class="content">
      <u-navbar
        title=""
        :fixed="false"
        :autoBack="false"
        bgColor="transparent"
        leftIcon=""
        :border="false"
      ></u-navbar>
      <view class="profile-card animate-enter" style="--delay: 0s" @click="goInfo">
        <view class="profile-left">
          <view class="avatar-wrapper">
            <image :src="user.avatar" class="avatar" mode="aspectFill" />
            <view class="edit-badge">
              <u-icon name="edit-pen" size="10" color="#fff"></u-icon>
            </view>
          </view>
          <view class="info-box">
            <h2 class="username">{{ user.name }}</h2>
            <text class="userid">ID: {{ user.id }}</text>
            <view class="tag-row">
              <view class="tag blue">Comp. Sci.</view>
              <view class="tag green">Level 3</view>
            </view>
          </view>
        </view>
        <u-icon name="arrow-right" color="#CBD5E1" size="16"></u-icon>
      </view>

      <view class="wallet-card animate-enter" style="--delay: 0.1s">
        <view class="wallet-bg-glow"></view>
        <view class="wallet-header">
          <view class="label-row">
            <u-icon name="rmb-circle" color="rgba(255,255,255,0.7)" size="16"></u-icon>
            <text>校园钱包余额</text>
          </view>
        </view>

        <view class="wallet-main">
          <view class="balance-wrapper">
            <text class="currency">¥</text>
            <text class="balance">{{ user.balance.toFixed(2) }}</text>
          </view>
          <view class="topup-btn" @click="toTopUp">
            <text>充值</text>
            <u-icon name="arrow-right" size="10" color="#fff"></u-icon>
          </view>
        </view>
      </view>

      <view class="menu-group animate-enter" style="--delay: 0.2s">
        <!-- Market Management Link -->
        <view class="menu-item" @click="goMarketManage">
          <view class="item-left">
            <view class="icon-box blue">
              <u-icon name="grid-fill" color="#3B82F6" size="20"></u-icon>
            </view>
            <text class="item-title">闲置交易管理</text>
          </view>
          <u-icon name="arrow-right" color="#CBD5E1" size="14"></u-icon>
        </view>

        <view class="menu-item" @click="goOrders">
          <view class="item-left">
            <view class="icon-box orange">
              <u-icon name="shopping-cart" size="20" color="#F97316"></u-icon>
            </view>
            <text class="item-text">我的订单</text>
          </view>
          <u-icon name="arrow-right" color="#CBD5E1" size="14"></u-icon>
        </view>

        <view class="menu-item" @click="goNotifs">
          <view class="item-left">
            <view class="icon-box red">
              <u-icon name="bell" size="20" color="#EF4444"></u-icon>
            </view>
            <text class="item-text">消息通知</text>
          </view>
          <view class="item-right">
            <view v-if="unreadCount>0" class="badge-pill">{{ unreadCount }}</view>
            <u-icon name="arrow-right" color="#CBD5E1" size="14"></u-icon>
          </view>
        </view>

        <view class="menu-item" @click="goTx">
          <view class="item-left">
            <view class="icon-box green">
              <u-icon name="list-dot" size="20" color="#10B981"></u-icon>
            </view>
            <text class="item-text">交易记录</text>
          </view>
          <u-icon name="arrow-right" color="#CBD5E1" size="14"></u-icon>
        </view>
      </view>

      <view class="menu-group animate-enter" style="--delay: 0.3s">
        <view class="menu-item" @click="goSettings">
          <view class="item-left">
            <view class="icon-box purple">
              <u-icon name="setting" size="20" color="#8B5CF6"></u-icon>
            </view>
            <text class="item-text">应用设置</text>
          </view>
          <u-icon name="arrow-right" color="#CBD5E1" size="14"></u-icon>
        </view>

        <view class="menu-item" @click="logout">
          <view class="item-left">
            <view class="icon-box gray">
              <u-icon name="close-circle" size="20" color="#64748B"></u-icon>
            </view>
            <text class="item-text">退出登录</text>
          </view>
        </view>
      </view>

      <view class="version-text">CampusPal v3.0.1</view>
      <view style="height:80px" />
    </scroll-view>

    <BottomNav active="profile" />
  </view>
</template>

<script setup>
import BottomNav from '@/components/BottomNav.vue'
import { getUserProfile, getBalance, updateProfile } from '@/api/user.js'
import { getNotifications } from '@/api/notifications.js'
import { reactive, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { isLoggedIn as checkLogin } from '@/utils/routeGuard.js'
import { getFullImageUrl } from '@/utils/avatar.js'

const user = reactive({
  name: '',
  id: '',
  avatar: '',
  balance: 0,
  points: 0
})
const unreadCount = ref(0)
const statusBarHeight = ref(20)
const customBarHeight = ref(44)
const isLoggedIn = ref(false)

onLoad(() => {
  const sys = uni.getSystemInfoSync()
  statusBarHeight.value = sys.statusBarHeight || 20
  
  // #ifdef MP-WEIXIN
  const menuButton = uni.getMenuButtonBoundingClientRect()
  customBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height
  // #endif
})

// 每次页面显示时刷新用户数据
onShow(async () => {
  isLoggedIn.value = checkLogin()
  
  if (isLoggedIn.value) {
    await loadUserProfile()
    await loadUnreadCount()
  }
})

async function loadUserProfile() {
  try {
    const [profileRes, balanceRes] = await Promise.all([
      getUserProfile(),
      getBalance()
    ])
    
    // 头像处理：使用公共方法处理URL
    const avatarUrl = getFullImageUrl(profileRes.data.avatar)

    Object.assign(user, {
      name: profileRes.data.username,
      id: profileRes.data.id,           // 使用数据库用户ID
      studentId: profileRes.data.studentId,  // 学号单独存储
      avatar: avatarUrl,
      balance: balanceRes.data.balance || 0,
      points: profileRes.data.points || 0
    })
    
    // 同步到本地存储（保留原有的完整userInfo，只更新部分字段）
    const cachedUserInfo = uni.getStorageSync('userInfo') || {}
    uni.setStorageSync('userInfo', { ...cachedUserInfo, ...user })
  } catch (err) {
    console.error('加载用户信息失败:', err)
    // 使用本地缓存
    const cached = uni.getStorageSync('userInfo')
    if (cached) {
      Object.assign(user, cached)
    }
  }
}

async function loadUnreadCount() {
  try {
    const res = await getNotifications({ page: 1, size: 1 })
    unreadCount.value = res.data.unreadCount || 0
  } catch (err) {
    console.error('加载未读数失败:', err)
  }
}

function toTopUp(){ uni.navigateTo({ url:'/pages/topup/topup' }) }
function goOrders(){ uni.navigateTo({ url:'/pages/orders/orders' }) }
function goNotifs(){ uni.navigateTo({ url:'/pages/notifications/notifications' }) }
function goInfo(){ uni.navigateTo({ url:'/pages/profile/info' }) }
function goTx(){ uni.navigateTo({ url:'/pages/profile/transactions' }) }
function goSettings(){ uni.navigateTo({ url:'/pages/profile/settings' }) }

function goMarketManage(){ uni.navigateTo({ url:'/pages/market/manage' }) }

function goLogin() {
  uni.reLaunch({ url: '/pages/login/login' })
}

function logout(){
  uni.showModal({
    title: '提示',
    content: '确定退出登录吗?',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        uni.showToast({ title:'已退出', icon:'none' })
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/login/login' })
        }, 500)
      }
    }
  })
}
</script>

<style scoped lang="scss">
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;
$primary: #6366F1;

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: -80px;
  right: -60px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.content {
  height: calc(100vh - 70px);
  padding: 16px 20px;
  position: relative;
  z-index: 1;
}

/* Profile Card */
.profile-card {
  background: #fff;
  border-radius: 24px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  box-shadow: 0 8px 24px rgba(148, 163, 184, 0.06);
  transition: transform 0.1s;

  &:active { transform: scale(0.98); }
}

.profile-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
  width: 68px;
  height: 68px;

  .avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 3px solid #F1F5F9;
  }

  .edit-badge {
    position: absolute;
    bottom: 0;
    right: 0;
    background: $primary;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid #fff;
  }
}

.info-box {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-size: 20px;
  font-weight: 800;
  color: $text-main;
  line-height: 1.2;
}

.userid {
  font-size: 12px;
  color: $text-sub;
  font-family: monospace;
}

.tag-row {
  display: flex;
  gap: 6px;
  margin-top: 2px;
}

.tag {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 6px;
  font-weight: 700;

  &.blue { background: #EFF6FF; color: #2563EB; }
  &.green { background: #ECFDF5; color: #10B981; }
}

/* Wallet Card */
.wallet-card {
  background: #1E293B;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 24px;
  color: #fff;
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 30px -8px rgba(30, 41, 59, 0.4);
}

.wallet-bg-glow {
  position: absolute;
  top: -50px;
  right: -50px;
  width: 180px;
  height: 180px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.4) 0%, rgba(30, 41, 59, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
}

.wallet-header {
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.label-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255,255,255,0.7);
}

.wallet-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  position: relative;
  z-index: 1;
}

.balance-wrapper {
  .currency { font-size: 20px; font-weight: 600; margin-right: 2px; opacity: 0.9; }
  .balance { font-size: 32px; font-weight: 800; letter-spacing: -0.5px; }
}

.topup-btn {
  background: rgba(255,255,255,0.2);
  padding: 6px 12px;
  border-radius: 100px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(4px);
  transition: background 0.2s;

  &:active { background: rgba(255,255,255,0.3); }
}


/* Menu Group */
.menu-group {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.05);
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  position: relative;

  &:not(:last-child)::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 60px;
    right: 0;
    height: 1px;
    background: #F1F5F9;
  }

  &:active { background: #F8FAFC; }
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-box {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;

  &.orange { background: #FFF7ED; }
  &.red { background: #FEF2F2; }
  &.green { background: #ECFDF5; }
  &.purple { background: #F5F3FF; }
  &.gray { background: #F1F5F9; }
}

.item-text {
  font-size: 14px;
  font-weight: 600;
  color: $text-main;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge-pill {
  background: #EF4444;
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 100px;
  font-weight: 700;
}

.version-text {
  text-align: center;
  font-size: 11px;
  color: #CBD5E1;
  margin-top: 24px;
}

/* Animation */
.animate-enter {
  opacity: 0;
  transform: translateY(15px);
  animation: fadeUp 0.5s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--delay);
}

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}

/* 未登录状态 */
.not-logged-in {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 70px);
  padding: 40px 20px;
  position: relative;
  z-index: 1;
}

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.empty-icon {
  margin-bottom: 24px;
  opacity: 0.5;
}

.empty-title {
  font-size: 24px;
  font-weight: 700;
  color: $text-main;
  margin-bottom: 12px;
}

.empty-desc {
  font-size: 14px;
  color: $text-sub;
  line-height: 1.6;
  max-width: 280px;
}
</style>