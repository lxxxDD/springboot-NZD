<template>
  <view class="page">
    <view class="navbar">
      <view class="nav-back" @click="goBack">
        <u-icon name="arrow-left" color="#1E293B" size="20"></u-icon>
      </view>
      <text class="nav-title">用户列表</text>
      <view style="width: 36px"></view>
    </view>

    <scroll-view scroll-y class="content">
      <view class="search-box">
        <u-icon name="search" color="#94A3B8" size="18"></u-icon>
        <input 
          class="search-input" 
          placeholder="搜索用户..." 
          v-model="searchText"
          @input="handleSearch"
        />
      </view>

      <view class="user-list">
        <view 
          class="user-item animate-enter" 
          v-for="(user, index) in filteredUsers" 
          :key="user.id"
          :style="{ '--delay': (index * 0.05) + 's' }"
          @click="goUserDetail(user.id)"
        >
          <view class="user-left">
            <view class="avatar-wrapper">
              <image :src="user.avatar" class="avatar" mode="aspectFill"></image>
              <view class="online-badge" v-if="user.online"></view>
            </view>
            <view class="user-info">
              <view class="name-row">
                <text class="username">{{ user.name }}</text>
                <view class="verified-badge" v-if="user.verified">
                  <u-icon name="checkmark" color="#fff" size="8"></u-icon>
                </view>
              </view>
              <text class="user-bio">{{ user.bio }}</text>
              <view class="user-stats">
                <text class="stat-text">{{ user.followers }} 粉丝</text>
                <text class="stat-divider">·</text>
                <text class="stat-text">{{ user.posts }} 发布</text>
              </view>
            </view>
          </view>
          <u-icon name="arrow-right" color="#CBD5E1" size="16"></u-icon>
        </view>
      </view>

      <view class="empty-state" v-if="filteredUsers.length === 0">
        <u-icon name="search" color="#CBD5E1" size="48"></u-icon>
        <text class="empty-text">未找到相关用户</text>
      </view>

      <view style="height: 20px"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'

const searchText = ref('')

const users = ref([
  {
    id: '20231001',
    name: 'Sarah Chen',
    avatar: 'https://picsum.photos/200/200?random=1',
    bio: '热爱编程和设计 | 喜欢分享生活',
    followers: 328,
    posts: 42,
    verified: true,
    online: true
  },
  {
    id: '20231002',
    name: 'Alex Wang',
    avatar: 'https://picsum.photos/200/200?random=2',
    bio: '篮球爱好者 | 计算机科学专业',
    followers: 156,
    posts: 28,
    verified: false,
    online: false
  },
  {
    id: '20231003',
    name: 'Emily Zhang',
    avatar: 'https://picsum.photos/200/200?random=3',
    bio: '摄影师 | 旅行达人 | 美食探索者',
    followers: 892,
    posts: 156,
    verified: true,
    online: true
  },
  {
    id: '20231004',
    name: 'Michael Liu',
    avatar: 'https://picsum.photos/200/200?random=4',
    bio: '音乐制作人 | 吉他手',
    followers: 445,
    posts: 67,
    verified: true,
    online: false
  },
  {
    id: '20231005',
    name: 'Jessica Lin',
    avatar: 'https://picsum.photos/200/200?random=5',
    bio: '艺术设计 | UI/UX Designer',
    followers: 623,
    posts: 89,
    verified: false,
    online: true
  }
])

const filteredUsers = computed(() => {
  if (!searchText.value) return users.value
  const keyword = searchText.value.toLowerCase()
  return users.value.filter(user => 
    user.name.toLowerCase().includes(keyword) || 
    user.bio.toLowerCase().includes(keyword)
  )
})

function handleSearch() {
  // 搜索逻辑
}

function goBack() {
  uni.navigateBack()
}

function goUserDetail(userId) {
  uni.navigateTo({ url: `/pages/profile/user-detail?userId=${userId}` })
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
}

.navbar {
  height: 56px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #E2E8F0;
}

.nav-back {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  &:active { background: #F1F5F9; }
}

.nav-title {
  font-size: 16px;
  font-weight: 700;
  color: $text-main;
}

.content {
  height: calc(100vh - 56px);
  padding: 20px;
}

.search-box {
  background: #fff;
  border-radius: 16px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(148, 163, 184, 0.05);
}

.search-input {
  flex: 1;
  font-size: 14px;
  color: $text-main;
  
  &::placeholder {
    color: #CBD5E1;
  }
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-item {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(148, 163, 184, 0.05);
  transition: transform 0.2s;

  &:active { transform: scale(0.98); }
}

.user-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.avatar-wrapper {
  position: relative;
  width: 56px;
  height: 56px;
  flex-shrink: 0;

  .avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 2px solid #F1F5F9;
  }

  .online-badge {
    position: absolute;
    bottom: 2px;
    right: 2px;
    width: 12px;
    height: 12px;
    background: #10B981;
    border: 2px solid #fff;
    border-radius: 50%;
  }
}

.user-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.username {
  font-size: 15px;
  font-weight: 700;
  color: $text-main;
}

.verified-badge {
  width: 16px;
  height: 16px;
  background: $primary;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-bio {
  font-size: 13px;
  color: $text-sub;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-stats {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stat-text {
  font-size: 12px;
  color: #94A3B8;
}

.stat-divider {
  color: #CBD5E1;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-text {
  display: block;
  margin-top: 16px;
  font-size: 14px;
  color: #CBD5E1;
}

.animate-enter {
  opacity: 0;
  transform: translateY(10px);
  animation: fadeUp 0.4s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--delay);
}

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}
</style>

