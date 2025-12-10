<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar
        title="校园活动"
        :autoBack="true"
        :bgColor="navBg"
        leftIconColor="#1E293B"
        titleStyle="color: #1E293B; font-weight: 700;"
        placeholder
        :customStyle="navCustomStyle"
    ></u-navbar>

    <view class="content-container">
      <view class="header-section animate-enter" style="--delay: 0s">
        <view class="title-box">
          <view>
            <text class="main-title">探索精彩活动</text>
            <text class="sub-title">发现兴趣，结识新朋友</text>
          </view>
          <view class="view-toggle" @click="isGridView = !isGridView">
            <u-icon :name="isGridView ? 'list' : 'grid-fill'" size="24" color="#1E293B"></u-icon>
          </view>
        </view>

        <scroll-view scroll-x class="category-scroll" :show-scrollbar="false">
          <view class="cat-list">
            <view
                v-for="(cat, index) in categories"
                :key="index"
                class="cat-item"
                :class="{ active: currentCat === index }"
                @click="currentCat = index"
            >
              {{ cat }}
            </view>
          </view>
        </scroll-view>
      </view>

      <view class="list-container">
        <view v-if="filteredActivities.length > 0" class="activity-list" :class="{ 'grid-mode': isGridView }">
          <view
              v-for="(item, index) in filteredActivities"
              :key="item.id"
              class="act-card animate-slide-up"
              :style="{ animationDelay: index * 0.1 + 's' }"
              @click="showActivity(item.id)"
          >
            <view class="cover-box">
              <image :src="item.image" mode="aspectFill" class="cover-img"></image>
              <view class="status-badge">
                <text class="status-dot"></text>报名中
              </view>
              <view class="participants-tag">
                <u-icon name="account-fill" size="12" color="#fff"></u-icon>
                <text class="count">{{ item.participants }} 人报名</text>
              </view>
            </view>

            <view class="card-body">
              <!-- Time Range (Above Title, Colored) -->
              <text class="time-text u-line-1">{{ item.timeRange }}</text>
              
              <!-- Title -->
              <text class="act-title u-line-1">{{ item.title }}</text>

              <!-- Bottom Info (Location/Organizer) -->
              <text class="bottom-info u-line-1">{{ item.location }}</text>
            </view>
              



            </view>
          </view>


        <view v-else class="empty-state animate-enter">
          <u-empty mode="search" text="该分类下暂无活动" iconSize="80"></u-empty>
        </view>

        <view style="height: 40px"></view>
      </view>
    </view>


  </view>
</template>

<script setup>

import { ref, computed, watch } from 'vue'
import { onLoad, onShow, onPageScroll, onReachBottom } from '@dcloudio/uni-app'
import { getActivities, registerActivity } from '@/api/activities.js'

const categories = ['全部', '学术讲座', '文艺汇演', '体育竞技', '志愿服务']
const statusMap = { '全部': '', '学术讲座': 'lecture', '文艺汇演': 'performance', '体育竞技': 'sports', '志愿服务': 'volunteer' }
const currentCat = ref(0)
const isGridView = ref(true)
const loading = ref(false)
const activityList = ref([])
const page = ref(1)
const hasMore = ref(true)
const navBg = ref('transparent')
const navCustomStyle = ref({})

onPageScroll((e) => {
  if (e.scrollTop > 50) {
    if (navBg.value !== 'rgba(255, 255, 255, 0.7)') {
      navBg.value = 'rgba(255, 255, 255, 0.7)'
      navCustomStyle.value = {
        backdropFilter: 'blur(10px)',
        boxShadow: '0 2px 10px rgba(0,0,0,0.05)'
      }
    }
  } else {
    if (navBg.value !== 'transparent') {
      navBg.value = 'transparent'
      navCustomStyle.value = {}
    }
  }
})

onReachBottom(() => {
  if (hasMore.value && !loading.value) {
    loadActivities()
  }
})

onShow(() => {
  reload()
})

// 监听分类变化
watch(currentCat, () => {
  reload()
})

function reload() {
  page.value = 1
  activityList.value = []
  hasMore.value = true
  loadActivities()
}

async function loadActivities() {
  if (loading.value) return
  loading.value = true
  
  try {
    const params = {
      page: page.value,
      size: 10
    }
    
    // 分类筛选
    if (currentCat.value !== 0) {
      params.status = statusMap[categories[currentCat.value]] || ''
    }
    
    const res = await getActivities(params)
    // 根据 API 文档，返回 PageVOActivityVO
    // ActivityVO 字段: id, title, description, coverImage, location, startTime, endTime, 
    //                  maxParticipants, currentParticipants, organizerName, status, isRegistered, createTime
    const data = res.data
    const list = (data.list || []).map(item => ({
      id: item.id,
      title: item.title,
      description: item.description,
      image: item.coverImage || 'https://via.placeholder.com/400x200',
      location: item.location,
      date: formatDate(item.startTime),
      startTime: item.startTime,
      endTime: item.endTime,
      registrationStartTime: item.registrationStartTime,
      registrationEndTime: item.registrationEndTime,
      formattedStartTime: formatTime(item.startTime),
      formattedEndTime: formatTime(item.endTime),
      timeRange: formatTimeRange(item.startTime, item.endTime),
      participants: item.currentParticipants || 0,
      maxParticipants: item.maxParticipants,
      organizer: item.organizerName,
      status: item.status,
      isRegistered: item.isRegistered
    }))
    
    activityList.value = [...activityList.value, ...list]
    hasMore.value = data.hasMore
    page.value++
  } catch (err) {
    console.error('加载活动列表失败:', err)
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${month}月${day}日 ${hour}:${minute}`
}

function formatTimeRange(startStr, endStr) {
  if (!startStr) return ''
  
  const format = (dateStr) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${month}/${day} ${hour}:${minute}`
  }
  
  let result = format(startStr)
  if (endStr) {
    result += ` - ${format(endStr)}`
  }
  return result
}

// 根据选中分类筛选活动（前端过滤，如果后端已过滤则直接返回）
const filteredActivities = computed(() => {
  return activityList.value
})

function showActivity(id) {
  uni.navigateTo({ url: `/pages/activity/detail?id=${id}` });
}

async function joinActivity(id) {
  try {
    // 传入 showError: false 禁止全局错误提示
    await registerActivity(id, { showError: false })
    uni.showToast({ title: '报名成功', icon: 'success' })
    updateLocalStatus(id)
  } catch (err) {
    const msg = err.message || ''
    // 如果是重复报名，视为成功或给予温和提示
    if (msg.includes('已报名') || msg.includes('Already registered')) {
      uni.showToast({ title: '您已报名参加', icon: 'none' })
      updateLocalStatus(id)
    } else {
      // 其他错误手动显示
      uni.showToast({ title: msg || '报名失败', icon: 'none' })
    }
    console.error('报名失败:', err)
  }
}

function updateLocalStatus(id) {
  const item = activityList.value.find(a => a.id === id)
  if (item) {
    item.isRegistered = true
    // 只有之前未报名才增加计数，避免重复增加
    // 但这里简单处理，假设刷新列表会纠正
    if (!item.isRegistered) item.participants++ 
  }
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
  min-height: 100vh;
  position: relative;
}

/* 优化：背景高度增加，避免滚动到底部露白 */
.bg-decoration {
  position: fixed;
  top: -120px; right: -80px;
  width: 350px; height: 350px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.view-toggle {
  width: 40px; height: 40px;
  background: #fff;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: transform 0.2s;
  
  &:active { transform: scale(0.9); }
}

.content-container {
  /* 移除 padding-top，由 u-navbar placeholder 自动处理 */
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 头部区域 */
.header-section {
  padding: 12px 20px;
  z-index: 1;
}

.header-section {
  padding: 12px 20px;
  z-index: 1;
}

.title-box { 
  margin-bottom: 20px; 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
}
.main-title { font-size: 24px; font-weight: 800; color: $text-main; display: block; margin-bottom: 4px; }
.sub-title { font-size: 13px; color: $text-sub; }

/* 分类胶囊 */
.category-scroll { width: 100%; white-space: nowrap; }
.cat-list { display: flex; gap: 12px; padding-right: 20px; }
.cat-item {
  padding: 6px 16px;
  background: #fff;
  border-radius: 100px;
  font-size: 13px;
  color: $text-sub;
  font-weight: 600;
  border: 1px solid transparent;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0,0,0,0.03);

  &.active {
    background: $text-main;
    color: #fff;
    box-shadow: 0 4px 12px rgba(30, 41, 59, 0.2);
    transform: translateY(-1px);
  }
}

/* 列表区域 */
.list-container {
  flex: 1;
  padding: 0 20px;
  box-sizing: border-box;
  /* 移除高度限制，使用页面滚动 */
  min-height: 500px;
}

.activity-list { 
  display: flex; 
  flex-direction: column; 
  gap: 20px; 
  padding-top: 10px; 
  
  &.grid-mode {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: 12px;
    
    .act-card {
      width: 48%;
      display: flex;
      flex-direction: column;
    }
    
    .cover-box {
      height: 120px;
    }
    
    .card-body {
      padding: 12px;
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    
    .act-title {
      font-size: 14px;
      margin-bottom: 8px;
      height: 40px; /* 固定高度确保对齐 */
      line-height: 1.4;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      overflow: hidden;
    }
    
    .info-row {
      flex-direction: column;
      align-items: flex-start;
      gap: 4px;
      padding: 6px;
      margin-bottom: 8px;
    }
    
    .info-divider { display: none; }
    
    .card-footer {
      margin-top: auto;
    }
  }
}

/* 活动卡片 */
.act-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 20px -5px rgba(148, 163, 184, 0.1);
  border: 1px solid rgba(255,255,255,0.5);
  transition: transform 0.1s;

  &:active { transform: scale(0.98); }
}

.cover-box {
  position: relative;
  width: 100%;
  height: 160px;

  .cover-img { width: 100%; height: 100%; }

  .status-badge {
    position: absolute; top: 12px; left: 12px;
    background: rgba(255,255,255,0.95);
    backdrop-filter: blur(4px);
    padding: 4px 10px; border-radius: 100px;
    font-size: 11px; font-weight: 700; color: #10B981;
    display: flex; align-items: center; gap: 6px;

    .status-dot { width: 6px; height: 6px; background: #10B981; border-radius: 50%; }
  }

  .participants-tag {
    position: absolute; bottom: 12px; right: 12px;
    background: rgba(0,0,0,0.6);
    backdrop-filter: blur(4px);
    padding: 4px 10px; border-radius: 8px;
    display: flex; align-items: center; gap: 4px;

    .count { font-size: 11px; color: #fff; font-weight: 600; }
  }
}

.card-body { padding: 16px; }

.act-title {
  font-size: 15px; font-weight: 700; color: $text-main;
  margin-bottom: 8px; display: block;
  line-height: 1.4;
}

.time-text {
  font-size: 12px; color: #F59E0B; font-weight: 500;
  margin-bottom: 6px; display: block;
}

.bottom-info {
  font-size: 12px; color: #94A3B8;
  display: block;
}

/* Removed old info-row styles */

.info-divider {
  width: 1px; height: 12px; background: #CBD5E1; margin: 0 12px;
}

.card-footer {
  display: flex; justify-content: space-between; align-items: center;
}

.organizer {
  display: flex; align-items: center; gap: 8px;
  .org-name { font-size: 12px; color: $text-sub; font-weight: 500; }
}

.empty-state {
  margin-top: 60px;
}

/* FAB */


/* 动画 */
.animate-enter { opacity: 0; transform: translateY(10px); animation: fadeUp 0.5s forwards; }
.animate-slide-up { opacity: 0; transform: translateY(20px); animation: fadeUp 0.6s cubic-bezier(0.2, 0.8, 0.2, 1) forwards; }
.animate-bounce-in { animation: bounceIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); }

@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
@keyframes bounceIn { from { transform: scale(0); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>