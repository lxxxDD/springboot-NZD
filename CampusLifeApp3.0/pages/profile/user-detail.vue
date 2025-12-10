<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <!-- 自定义导航栏 -->
    <view class="custom-navbar">
      <view class="nav-back" @click="goBack">
        <u-icon name="arrow-left" color="#1E293B" size="20"></u-icon>
      </view>
      <view class="nav-actions">
        <view class="nav-btn" @click="shareUser">
          <u-icon name="share" color="#1E293B" size="18"></u-icon>
        </view>
        <view class="nav-btn" @click="showMore">
          <u-icon name="more-dot-fill" color="#1E293B" size="18"></u-icon>
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="content">
      <!-- 用户头部信息 -->
      <view class="user-header animate-enter" style="--delay: 0s">
        <view class="avatar-wrapper">
          <image :src="userInfo.avatar" class="avatar" mode="aspectFill"></image>
          <view class="online-badge" v-if="userInfo.online"></view>
        </view>
        
        <view class="user-info">
          <view class="name-row">
            <text class="username">{{ userInfo.name }}</text>
            <view class="verified-badge" v-if="userInfo.verified">
              <u-icon name="checkmark" color="#fff" size="10"></u-icon>
            </view>
          </view>
          <text class="user-id">ID: {{ userInfo.id }}</text>
          <view class="tags">
            <view class="tag blue">{{ userInfo.major }}</view>
            <view class="tag green">{{ userInfo.grade }}</view>
          </view>
        </view>

        <view class="action-buttons">
          <view class="btn-primary" @click="sendMessage">
            <u-icon name="chat" color="#fff" size="16"></u-icon>
            <text>发消息</text>
          </view>
          <view class="btn-secondary" @click="toggleFollow">
            <u-icon :name="isFollowing ? 'checkmark' : 'plus'" color="#6366F1" size="16"></u-icon>
            <text>{{ isFollowing ? '已关注' : '关注' }}</text>
          </view>
        </view>
      </view>

      <!-- 统计数据 -->
      <view class="stats-card animate-enter" style="--delay: 0.1s">
        <view class="stat-item" @click="showFollowers">
          <text class="stat-value">{{ userInfo.followers }}</text>
          <text class="stat-label">粉丝</text>
        </view>
        <view class="divider"></view>
        <view class="stat-item" @click="showFollowing">
          <text class="stat-value">{{ userInfo.following }}</text>
          <text class="stat-label">关注</text>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ userInfo.posts }}</text>
          <text class="stat-label">发布</text>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ userInfo.rating }}</text>
          <text class="stat-label">评分</text>
        </view>
      </view>

      <!-- 个人简介 -->
      <view class="bio-card animate-enter" style="--delay: 0.2s" v-if="userInfo.bio">
        <view class="card-header">
          <view class="icon-box">
            <u-icon name="account" color="#6366F1" size="18"></u-icon>
          </view>
          <text class="card-title">个人简介</text>
        </view>
        <text class="bio-text">{{ userInfo.bio }}</text>
      </view>

      <!-- Tab切换 -->
      <view class="tabs-wrapper animate-enter" style="--delay: 0.3s">
        <view class="tabs">
          <view 
            class="tab-item" 
            :class="{ active: activeTab === 'market' }"
            @click="activeTab = 'market'"
          >
            <u-icon name="shopping-bag" size="18" :color="activeTab === 'market' ? '#6366F1' : '#94A3B8'"></u-icon>
            <text>在售商品</text>
            <view class="tab-badge" v-if="marketItems.length > 0">{{ marketItems.length }}</view>
          </view>
          <view 
            class="tab-item" 
            :class="{ active: activeTab === 'activity' }"
            @click="activeTab = 'activity'"
          >
            <u-icon name="calendar" size="18" :color="activeTab === 'activity' ? '#6366F1' : '#94A3B8'"></u-icon>
            <text>发起活动</text>
            <view class="tab-badge" v-if="activities.length > 0">{{ activities.length }}</view>
          </view>
        </view>
      </view>

      <!-- 商品列表 -->
      <view class="items-grid" v-if="activeTab === 'market'">
        <view 
          class="market-item animate-enter" 
          v-for="(item, index) in marketItems" 
          :key="item.id"
          :style="{ '--delay': (0.4 + index * 0.05) + 's' }"
          @click="goMarketDetail(item.id)"
        >
          <image :src="item.image" class="item-image" mode="aspectFill"></image>
          <view class="item-info">
            <text class="item-title">{{ item.title }}</text>
            <view class="item-bottom">
              <text class="item-price">¥{{ item.price }}</text>
              <view class="item-status" :class="item.status">
                {{ item.status === 'selling' ? '在售' : '已售' }}
              </view>
            </view>
          </view>
        </view>
        <view class="empty-state" v-if="marketItems.length === 0">
          <u-icon name="shopping-bag" color="#CBD5E1" size="48"></u-icon>
          <text class="empty-text">暂无在售商品</text>
        </view>
      </view>

      <!-- 活动列表 -->
      <view class="items-list" v-if="activeTab === 'activity'">
        <view 
          class="activity-item animate-enter" 
          v-for="(item, index) in activities" 
          :key="item.id"
          :style="{ '--delay': (0.4 + index * 0.05) + 's' }"
          @click="goActivityDetail(item.id)"
        >
          <image :src="item.image" class="activity-image" mode="aspectFill"></image>
          <view class="activity-info">
            <text class="activity-title">{{ item.title }}</text>
            <view class="activity-meta">
              <view class="meta-item">
                <u-icon name="clock" color="#94A3B8" size="12"></u-icon>
                <text>{{ item.time }}</text>
              </view>
              <view class="meta-item">
                <u-icon name="map" color="#94A3B8" size="12"></u-icon>
                <text>{{ item.location }}</text>
              </view>
            </view>
            <view class="activity-bottom">
              <view class="participants">
                <u-icon name="account" color="#6366F1" size="14"></u-icon>
                <text>{{ item.participants }}/{{ item.maxParticipants }}</text>
              </view>
              <view class="activity-status" :class="item.status">
                {{ item.status === 'ongoing' ? '进行中' : '已结束' }}
              </view>
            </view>
          </view>
        </view>
        <view class="empty-state" v-if="activities.length === 0">
          <u-icon name="calendar" color="#CBD5E1" size="48"></u-icon>
          <text class="empty-text">暂无发起的活动</text>
        </view>
      </view>

      <view style="height: 40px"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const activeTab = ref('market')
const isFollowing = ref(false)

// 用户信息
const userInfo = reactive({
  id: '20231001',
  name: 'Sarah Chen',
  avatar: 'https://picsum.photos/200/200?random=1',
  major: 'Computer Science',
  grade: 'Junior',
  verified: true,
  online: true,
  followers: 328,
  following: 156,
  posts: 42,
  rating: 4.8,
  bio: '热爱编程和设计 | 喜欢分享生活 | 校园二手市场活跃卖家 🎨'
})

// 在售商品
const marketItems = ref([
  {
    id: 1,
    title: 'MacBook Pro 2021',
    price: 1299,
    image: 'https://picsum.photos/300/300?random=10',
    status: 'selling'
  },
  {
    id: 2,
    title: 'iPad Air 第五代',
    price: 499,
    image: 'https://picsum.photos/300/300?random=11',
    status: 'selling'
  },
  {
    id: 3,
    title: '数据结构教材',
    price: 25,
    image: 'https://picsum.photos/300/300?random=12',
    status: 'sold'
  }
])

// 发起的活动
const activities = ref([
  {
    id: 1,
    title: '周末篮球友谊赛',
    time: '12-25 14:00',
    location: '体育馆',
    participants: 8,
    maxParticipants: 10,
    image: 'https://picsum.photos/400/200?random=20',
    status: 'ongoing'
  },
  {
    id: 2,
    title: '编程学习小组',
    time: '12-20 19:00',
    location: '图书馆301',
    participants: 12,
    maxParticipants: 15,
    image: 'https://picsum.photos/400/200?random=21',
    status: 'ongoing'
  }
])

onLoad((options) => {
  if (options.userId) {
    // 根据userId加载用户信息
    loadUserInfo(options.userId)
  }
})

function loadUserInfo(userId) {
  // TODO: 从服务器加载用户信息
  console.log('Loading user:', userId)
}

function goBack() {
  uni.navigateBack()
}

function shareUser() {
  uni.showToast({ title: '分享功能开发中', icon: 'none' })
}

function showMore() {
  uni.showActionSheet({
    itemList: ['举报用户', '拉黑用户'],
    success: (res) => {
      if (res.tapIndex === 0) {
        uni.showToast({ title: '举报功能开发中', icon: 'none' })
      } else if (res.tapIndex === 1) {
        uni.showToast({ title: '拉黑功能开发中', icon: 'none' })
      }
    }
  })
}

function sendMessage() {
  uni.navigateTo({ 
    url: `/pages/messages/chat?userId=${userInfo.id}&name=${userInfo.name}` 
  })
}

function toggleFollow() {
  isFollowing.value = !isFollowing.value
  uni.showToast({ 
    title: isFollowing.value ? '已关注' : '已取消关注', 
    icon: 'none' 
  })
}

function showFollowers() {
  uni.showToast({ title: '粉丝列表开发中', icon: 'none' })
}

function showFollowing() {
  uni.showToast({ title: '关注列表开发中', icon: 'none' })
}

function goMarketDetail(id) {
  uni.navigateTo({ url: `/pages/market/detail?id=${id}` })
}

function goActivityDetail(id) {
  uni.navigateTo({ url: `/pages/activity/detail?id=${id}` })
}
</script>

<style scoped lang="scss">
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
}

.bg-decoration {
  position: fixed;
  top: -100px;
  right: -80px;
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

/* 自定义导航栏 */
.custom-navbar {
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: rgba(248, 250, 252, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  z-index: 100;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
}

.nav-back {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  &:active { transform: scale(0.95); }
}

.nav-actions {
  display: flex;
  gap: 8px;
}

.nav-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  &:active { transform: scale(0.95); }
}

.content {
  height: calc(100vh - 56px);
  padding: 20px;
  position: relative;
  z-index: 1;
}

/* 用户头部 */
.user-header {
  background: #fff;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 8px 24px rgba(148, 163, 184, 0.08);
}

.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;

  .avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 4px solid #F1F5F9;
  }

  .online-badge {
    position: absolute;
    bottom: 4px;
    right: 4px;
    width: 16px;
    height: 16px;
    background: #10B981;
    border: 3px solid #fff;
    border-radius: 50%;
  }
}

.user-info {
  text-align: center;
  margin-bottom: 20px;
}

.name-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 4px;
}

.username {
  font-size: 22px;
  font-weight: 800;
  color: $text-main;
}

.verified-badge {
  width: 20px;
  height: 20px;
  background: $primary;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-id {
  font-size: 12px;
  color: $text-sub;
  font-family: monospace;
  margin-bottom: 8px;
}

.tags {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 700;

  &.blue { background: #EFF6FF; color: #2563EB; }
  &.green { background: #ECFDF5; color: #10B981; }
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-primary, .btn-secondary {
  flex: 1;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;

  &:active { transform: scale(0.98); }
}

.btn-primary {
  background: $primary;
  color: #fff;
}

.btn-secondary {
  background: #F8FAFC;
  color: $primary;
  border: 1.5px solid #E2E8F0;
}

/* 统计卡片 */
.stats-card {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.05);
}

.stat-item {
  flex: 1;
  text-align: center;

  &:active { opacity: 0.7; }
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 800;
  color: $text-main;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 12px;
  color: $text-sub;
  font-weight: 500;
}

.divider {
  width: 1px;
  height: 32px;
  background: #E2E8F0;
}

/* 简介卡片 */
.bio-card {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.icon-box {
  width: 32px;
  height: 32px;
  background: #F0F4FF;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: $text-main;
}

.bio-text {
  font-size: 14px;
  color: $text-sub;
  line-height: 1.5;
}

/* Tab切换 */
.tabs-wrapper {
  margin-bottom: 20px;
}

.tabs {
  background: #fff;
  border-radius: 16px;
  padding: 6px;
  display: flex;
  gap: 6px;
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.05);
}

.tab-item {
  flex: 1;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #94A3B8;
  position: relative;
  transition: all 0.3s;

  &.active {
    background: #F0F4FF;
    color: $primary;
  }

  &:active { transform: scale(0.98); }
}

.tab-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  background: #EF4444;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 100px;
  font-weight: 700;
  min-width: 16px;
  text-align: center;
}

/* 商品网格 */
.items-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.market-item {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.05);
  transition: transform 0.2s;

  &:active { transform: scale(0.98); }
}

.item-image {
  width: 100%;
  height: 160px;
  background: #F1F5F9;
}

.item-info {
  padding: 12px;
}

.item-title {
  font-size: 14px;
  font-weight: 600;
  color: $text-main;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8px;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 16px;
  font-weight: 800;
  color: $primary;
}

.item-status {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 6px;
  font-weight: 700;

  &.selling {
    background: #ECFDF5;
    color: #10B981;
  }

  &.sold {
    background: #F1F5F9;
    color: #94A3B8;
  }
}

/* 活动列表 */
.items-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.05);
  transition: transform 0.2s;

  &:active { transform: scale(0.98); }
}

.activity-image {
  width: 100%;
  height: 140px;
  background: #F1F5F9;
}

.activity-info {
  padding: 16px;
}

.activity-title {
  font-size: 15px;
  font-weight: 700;
  color: $text-main;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: $text-sub;
}

.activity-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.participants {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  color: $primary;
}

.activity-status {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 6px;
  font-weight: 700;

  &.ongoing {
    background: #EFF6FF;
    color: #2563EB;
  }

  &.ended {
    background: #F1F5F9;
    color: #94A3B8;
  }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  grid-column: 1 / -1;
}

.empty-text {
  display: block;
  margin-top: 16px;
  font-size: 14px;
  color: #CBD5E1;
}

/* 动画 */
.animate-enter {
  opacity: 0;
  transform: translateY(15px);
  animation: fadeUp 0.5s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--delay);
}

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}
</style>

