<template>
  <view class="page">
    <!-- 1. 顶部区域 (固定背景色 + 搜索框) -->
    <view class="header-container">
      <u-navbar
        title=""
        :fixed="false"
        :autoBack="false"
        bgColor="transparent"
        leftIcon=""
        :border="false"
      ></u-navbar>
      
      <view class="header-content">
        <view class="header-top">
          <view class="user-info">
            <text class="date-text">{{ todayDate }}</text>
            <view class="name-row">
              <text class="greet-text">Hi, {{ user.name }}</text>
              <view class="status-dot"></view>
            </view>
          </view>
          <view class="weather-glass">
            <text class="material-symbols-outlined w-icon">{{ weather.icon }}</text>
            <text class="w-val">{{ weather.temp }}°</text>
          </view>
        </view>

        <!-- 模拟搜索框 -->
        <view class="search-bar">
          <text class="material-symbols-outlined search-icon">search</text>
          <text class="search-hold">搜索服务、通知或活动...</text>
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="content-scroll">
      <view class="main-wrapper">
        
        <!-- 2. 轮播图 Banner (视觉重心) -->
        <swiper class="banner-swiper" circular autoplay interval="5000" indicator-dots indicator-active-color="#fff" indicator-color="rgba(255,255,255,0.5)">
          <swiper-item v-for="(item, index) in banners" :key="index">
            <image :src="item" mode="aspectFill" class="banner-img"></image>
          </swiper-item>
        </swiper>

        <!-- 3. 金刚区 (微渐变图标) -->
        <view class="grid-card">
          <view class="grid-row">
            <view class="grid-item" @click="goFood">
              <view class="icon-box gradient-orange">
                <text class="material-symbols-outlined">restaurant</text>
              </view>
              <text class="grid-text">食堂</text>
            </view>
            <view class="grid-item" @click="goRepair">
              <view class="icon-box gradient-blue">
                <text class="material-symbols-outlined">build</text>
              </view>
              <text class="grid-text">报修</text>
            </view>
            <view class="grid-item" @click="goAssistant">
              <view class="icon-box gradient-indigo">
                <text class="material-symbols-outlined">smart_toy</text>
              </view>
              <text class="grid-text">助手</text>
            </view>
            <view class="grid-item" @click="goActivities">
              <view class="icon-box gradient-purple">
                <text class="material-symbols-outlined">celebration</text>
              </view>
              <text class="grid-text">活动</text>
            </view>
          </view>
        </view>

        <!-- 4. 待办事项 (票据风格) -->
        <view v-if="activeRepairs.length" class="ticket-card" @click="onOrderClick(activeRepairs[0])">
          <view class="ticket-left">
            <view class="ticket-line"></view> <!-- 装饰线条 -->
            <view class="ticket-content">
              <text class="ticket-label">进行中</text>
              <text class="ticket-title">维修单 {{ activeRepairs[0].id }}</text>
              <text class="ticket-sub">{{ activeRepairs[0].location }} · {{ issueText[activeRepairs[0].issue]||activeRepairs[0].issue }}</text>
            </view>
          </view>
          <view class="ticket-right">
            <text class="status-pill">{{ statusText[activeRepairs[0].status]||activeRepairs[0].status }}</text>
            <text class="material-symbols-outlined arrow-icon">chevron_right</text>
          </view>
        </view>

        <!-- 5. 热门活动 (大卡片) -->
        <view class="section-box">
          <view class="sec-header">
            <text class="sec-title">精彩活动</text>
            <text class="sec-link" @click="goActivities">全部</text>
          </view>
          <scroll-view scroll-x class="card-scroll" :show-scrollbar="false">
            <view class="card-scroll-inner">
              <view class="poster-card" v-for="item in activities" :key="item.id" @click="openActivity(item)">
                <image :src="item.image" mode="aspectFill" class="poster-image"></image>
                <view class="poster-mask">
                  <text class="poster-date">{{ item.date }}</text>
                  <text class="poster-name">{{ item.title }}</text>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>

        <!-- 6. 校园动态 (列表) -->
        <view class="section-box">
          <text class="sec-title mb-12">最新资讯</text>
          <view class="news-list">
            <view class="news-item" v-for="item in newsList" :key="item.id" @click="openNews(item)">
              <view class="news-info">
                <text class="news-h">{{ item.title }}</text>
                <text class="news-t">{{ item.time }}</text>
              </view>
              <image v-if="item.image" :src="item.image" mode="aspectFill" class="news-thumb"></image>
            </view>
          </view>
        </view>

        <view class="safe-area-bottom"></view>
      </view>
    </scroll-view>

    <BottomNav active="home" />
  </view>
</template>

<script setup>
import BottomNav from '@/components/BottomNav.vue'
import { getActivities } from '@/api/activities.js'
import { getRepairs } from '@/api/repairs.js'
import { getNewsList } from '@/api/news.js'
import { getWeatherNow } from '@/api/weather.js'
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'

// --- 逻辑 ---
const issueText = { Electric: '电路', Water: '水管', Wifi: '网络', Furniture: '家具', AC: '空调', Other: '其他' }
const statusText = { Received: '已接收', 'In Progress': '处理中', Completed: '已完成' }
const user = ref({ name: '同学' })
const weather = ref({ city: '加载中', temp: '--', text: '--', humidity: '--', icon: 'cloud' })
const repairList = ref([])
const activityList = ref([])
const newsList = ref([])
const banners = ref([
  'https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=800&q=80', // 校园风景
  'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=800&q=80', // 图书馆
])

const activeRepairs = computed(() => repairList.value.filter(r => r.status !== 'Completed'))
const activities = computed(() => activityList.value)

// 日期显示
const todayDate = computed(() => {
  const d = new Date()
  const m = d.getMonth() + 1
  const day = d.getDate()
  const week = ['日','一','二','三','四','五','六'][d.getDay()]
  return `${m}月${day}日 星期${week}`
})

function goFood(){ uni.reLaunch({ url:'/pages/services/food' }) }
function goRepair(){ uni.reLaunch({ url:'/pages/services/repair' }) }
function goAssistant(){ uni.navigateTo({ url:'/pages/assistant/assistant' }) }
function goActivities(){ uni.navigateTo({ url:'/pages/activity/list' }) }
function goOrders(){ uni.navigateTo({ url:'/pages/orders/orders' }) }
function onOrderClick(item) { goOrders() }
function openNews(n){ uni.navigateTo({ url:'/pages/news/detail?id='+n.id }) }
function openActivity(a){ uni.navigateTo({ url:'/pages/activity/detail?id='+a.id }) }

onShow(() => {
  const cached = uni.getStorageSync('userInfo')
  if (cached && cached.name) user.value = { name: cached.name }
  loadWeather()
  loadRepairs()
  loadActivities()
  loadNews()
})

async function loadWeather() { weather.value = { city: '南宁', temp: '26', text: '多云', icon: 'partly_cloudy_day' } }
async function loadRepairs() { repairList.value = [{id:'R20231001', issue:'Electric', location:'D栋 405', status:'In Progress'}] }
async function loadActivities() {
    activityList.value = [
        {id:1, title:'校园十佳歌手大赛总决赛', date:'10.24', image:'https://images.unsplash.com/photo-1514525253440-b393452e3726?w=400&q=80'},
        {id:2, title:'秋季篮球联赛', date:'10.26', image:'https://images.unsplash.com/photo-1504450758481-7338eba7524a?w=400&q=80'},
        {id:3, title:'读书分享会', date:'11.01', image:'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&q=80'}
    ]
}
async function loadNews() {
   newsList.value = [
       {id:1, title:'2024年秋季学期期末考试安排通知', time:'10分钟前', image: null},
       {id:2, title:'我校在创新创业大赛斩获金奖', time:'1小时前', image: 'https://images.unsplash.com/photo-1531545514256-b1400bc00f31?w=200&q=80'},
       {id:3, title:'图书馆闭馆时间调整通知', time:'昨天', image: null}
   ]
}
</script>

<style scoped lang="scss">
$page-bg: #F7F8FA;
$primary: #3b82f6;
$text-main: #1F2937;
$text-sub: #6B7280;

.page { background-color: $page-bg; min-height: 100vh; display: flex; flex-direction: column; }

/* 1. 头部 */
.header-container {
  background: #fff;
  padding-bottom: 12px;
}
.header-content { padding: 0 20px; }
.header-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }

.user-info { display: flex; flex-direction: column; }
.date-text { font-size: 12px; color: $text-sub; margin-bottom: 4px; font-weight: 500; }
.name-row { display: flex; align-items: center; gap: 6px; }
.greet-text { font-size: 22px; font-weight: 800; color: $text-main; letter-spacing: -0.5px; }
.status-dot { width: 8px; height: 8px; background: #10B981; border-radius: 50%; border: 2px solid #fff; box-shadow: 0 2px 4px rgba(16,185,129,0.2); }

.weather-glass {
  background: linear-gradient(135deg, #E0F2FE, #F0F9FF);
  padding: 6px 12px; border-radius: 20px;
  display: flex; align-items: center; gap: 6px;
  border: 1px solid #BAE6FD;
}
.w-icon { font-size: 20px; color: #0284C7; }
.w-val { font-size: 14px; font-weight: 700; color: #0369A1; }

.search-bar {
  background: #F3F4F6; height: 40px; border-radius: 12px;
  display: flex; align-items: center; padding: 0 12px; gap: 8px;
  transition: all 0.3s;
}
.search-bar:active { background: #E5E7EB; }
.search-icon { font-size: 20px; color: #9CA3AF; }
.search-hold { font-size: 13px; color: #9CA3AF; }

/* 内容区 */
.content-scroll { flex: 1; height: 0; }
.main-wrapper { padding: 16px; display: flex; flex-direction: column; gap: 20px; }

/* 2. Banner */
.banner-swiper {
  width: 100%; height: 150px; border-radius: 16px; overflow: hidden;
  box-shadow: 0 8px 16px rgba(0,0,0,0.06);
  transform: translateZ(0); /* 修复圆角在某些设备失效 */
}
.banner-img { width: 100%; height: 100%; object-fit: cover; }

/* 3. 金刚区 */
.grid-card {
  background: #fff; border-radius: 16px; padding: 16px 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}
.grid-row { display: grid; grid-template-columns: repeat(4, 1fr); }
.grid-item { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.grid-item:active { opacity: 0.7; }

.icon-box {
  width: 48px; height: 48px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.icon-box .material-symbols-outlined { font-size: 24px; }

/* 渐变色 */
.gradient-orange { background: linear-gradient(135deg, #FF9F43, #FF6B6B); }
.gradient-blue   { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.gradient-indigo { background: linear-gradient(135deg, #667eea, #764ba2); }
.gradient-purple { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }

.grid-text { font-size: 12px; color: $text-main; font-weight: 500; }

/* 4. 票据卡片 */
.ticket-card {
  background: #fff; border-radius: 12px;
  display: flex; justify-content: space-between; align-items: stretch;
  overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  position: relative;
}
.ticket-left { display: flex; align-items: center; padding: 16px; gap: 12px; flex: 1; }
.ticket-line { width: 4px; height: 32px; background: #F59E0B; border-radius: 4px; }
.ticket-content { display: flex; flex-direction: column; gap: 4px; }
.ticket-label { font-size: 10px; color: #F59E0B; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
.ticket-title { font-size: 15px; font-weight: 700; color: $text-main; }
.ticket-sub { font-size: 12px; color: $text-sub; }
.ticket-right {
  background: #FFFBEB; width: 80px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 4px; border-left: 1px dashed #FCD34D;
}
.status-pill { font-size: 11px; font-weight: 600; color: #D97706; }
.arrow-icon { font-size: 18px; color: #D97706; opacity: 0.6; }

/* 5. 活动 */
.section-box { display: flex; flex-direction: column; }
.sec-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; padding: 0 4px; }
.sec-title { font-size: 17px; font-weight: 700; color: $text-main; }
.mb-12 { margin-bottom: 12px; }
.sec-link { font-size: 13px; color: $primary; font-weight: 600; }

.card-scroll { width: 100%; white-space: nowrap; }
.card-scroll-inner { display: flex; gap: 12px; padding-bottom: 10px; } /* 阴影空间 */
.poster-card {
  position: relative; width: 140px; height: 180px;
  border-radius: 12px; overflow: hidden; display: inline-block;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.poster-image { width: 100%; height: 100%; object-fit: cover; background: #eee; }
.poster-mask {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  padding: 12px; display: flex; flex-direction: column; justify-content: flex-end;
  height: 50%;
}
.poster-date { font-size: 10px; color: #FCD34D; font-weight: 700; margin-bottom: 2px; }
.poster-name { font-size: 13px; color: #fff; font-weight: 600; white-space: normal; line-height: 1.3; }

/* 6. 新闻列表 */
.news-list { background: #fff; border-radius: 16px; padding: 4px 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.02); }
.news-item {
  display: flex; justify-content: space-between; padding: 12px 0;
  border-bottom: 1px solid #F3F4F6;
}
.news-item:last-child { border-bottom: none; }
.news-info { flex: 1; padding-right: 12px; display: flex; flex-direction: column; justify-content: space-between; }
.news-h { font-size: 14px; font-weight: 500; color: $text-main; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.news-t { font-size: 11px; color: #9CA3AF; margin-top: 6px; }
.news-thumb { width: 80px; height: 60px; border-radius: 8px; background: #F3F4F6; object-fit: cover; flex-shrink: 0; }

.safe-area-bottom { height: 40px; }
</style>