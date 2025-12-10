<template>
  <view class="page">
    <scroll-view scroll-y class="content">
      <u-navbar
        title=""
        :fixed="false"
        :autoBack="false"
        bgColor="transparent"
        leftIcon=""
        :border="false"
      ></u-navbar>
      <view class="welcome">
        <view class="header-text">
          <text class="greet">{{ greet }}, {{ user.name }}！👋</text>
          <text class="hint">祝你在校园度过高效的一天。</text>
        </view>

        <view class="weather">
          <view class="w-left">
            <view class="loc">
              <text class="material-symbols-outlined">location_on</text>
              <text class="loc-text">{{ weather.city }}</text>
            </view>
            <view class="w-main">
              <text class="temp">{{ weather.temp }}°</text>
              <view class="w-meta">
                <text>{{ weather.text }}</text>
                <text class="humidity">湿度 {{ weather.humidity }}%</text>
              </view>
            </view>
          </view>
          <view class="w-right">
            <text class="material-symbols-outlined big">{{ weather.icon }}</text>
          </view>
        </view>
      </view>

      <view class="section">
        <text class="title">快捷功能</text>
        <view class="quick">
          <view class="q-item" @click="goFood">
            <view class="q-icon orange"><text class="material-symbols-outlined">restaurant</text></view>
            <text class="q-text">食堂</text>
          </view>
          <view class="q-item" @click="goRepair">
            <view class="q-icon blue"><text class="material-symbols-outlined">build</text></view>
            <text class="q-text">报修</text>
          </view>
          <view class="q-item" @click="goAssistant">
            <view class="q-icon indigo"><text class="material-symbols-outlined">smart_toy</text></view>
            <text class="q-text">助手</text>
          </view>
          <view class="q-item" @click="goActivities">
            <view class="q-icon purple"><text class="material-symbols-outlined">celebration</text></view>
            <text class="q-text">活动</text>
          </view>
        </view>
      </view>

      <view class="section">
        <view class="row">
          <text class="title">订单状态</text>
          <text class="link" @click="goRepair">查看全部</text>
        </view>

        <view v-if="activeRepairs.length" class="order-swiper-box">
          <up-swiper
            :list="activeRepairs"
            :previousMargin="30"
            :nextMargin="30"
            :circular="false"
            :autoplay="false"
            :indicator="false"
            height="100"
            radius="16"
            bgColor="transparent"
            @click="onOrderSwiperClick"
          >
            <template #default="{ item }">
              <view class="order-card">
                <view class="icon"><text class="material-symbols-outlined">home_repair_service</text></view>
                <view class="info">
                  <view class="row-inner">
                    <text class="name">维修单 {{ item.id }}</text>
                    <text class="chip amber">{{ statusText[item.status]||item.status }}</text>
                  </view>
                  <text class="sub">{{ item.location }} · {{ issueText[item.issue]||item.issue }}</text>
                </view>
              </view>
            </template>
          </up-swiper>
        </view>
        <view v-else class="empty">当前暂无进行中的订单或报修</view>
      </view>

      <view class="section">
        <text class="title">校园新闻</text>
        
        <!-- 新闻轮播 - 前6条 (uview-plus 卡片式) -->
        <up-swiper
          :list="swiperList"
          keyName="image"
          previousMargin="30"
          nextMargin="30"
          :circular="true"
          :autoplay="true"
          :interval="4000"
          :indicator="true"
          indicatorMode="dot"
          height="180"
          radius="12"
          bgColor="transparent"
          @click="onSwiperClick"
        >
          <template #default="{ item }">
            <view class="swiper-card" @click="openNews(item)">
              <image :src="item.image" mode="aspectFill" class="swiper-img"></image>
              <view class="swiper-overlay">
                <text class="swiper-title">{{ item.title }}</text>
              </view>
            </view>
          </template>
        </up-swiper>
      </view>

      <view class="section">
        <view class="row">
          <text class="title">校园活动</text>
          <text class="link" @click="goActivities">查看全部</text>
        </view>
        <view class="grid">
          <view class="act" v-for="a in activities.slice(0,4)" :key="a.id" @click="openActivity(a)">
            <image :src="a.image" mode="aspectFill" class="cover"></image>
            <view class="a-body">
              <text class="a-title">{{ a.title }}</text>
              <view class="a-meta">
                <text class="material-symbols-outlined small-icon">calendar_today</text>
                <text class="a-date">{{ a.date }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view style="height: 80px"></view>
    </scroll-view>

    <BottomNav active="home" />
  </view>
</template>

<script setup>
import BottomNav from '@/components/BottomNav.vue'
const issueText = { Electric: '电路', Water: '水管', Wifi: '网络', Furniture: '家具', AC: '空调', Other: '其他' }
const statusText = { Received: '已接收', 'In Progress': '进行中', Completed: '已完成' }
import { getActivities } from '@/api/activities.js'
import { getRepairs } from '@/api/repairs.js'
import { getNewsList } from '@/api/news.js'
import { getWeatherNow } from '@/api/weather.js'
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'

const unreadCount = ref(0)
const repairList = ref([])
const activityList = ref([])
const newsList = ref([])

// 用户信息 - 从本地存储获取
const user = ref({ name: '用户' })

// 天气数据
const weather = ref({
  city: '加载中...',
  temp: '--',
  text: '--',
  humidity: '--',
  icon: 'cloud'
})
const statusBarHeight = ref(44)
const customBarHeight = ref(44)

// 进行中的报修（首页只显示未完成的）
const activeRepairs = computed(() => repairList.value.filter(r => r.status !== 'Completed'))

// 活动列表
const activities = computed(() => activityList.value)

// 新闻列表（优先使用 API 数据，fallback 到模拟数据）
const news = computed(() => newsList.value)

// 轮播新闻 - 前6条
const newsForSwiper = computed(() => news.value.slice(0, 6))

// u-swiper 需要的列表格式
const swiperList = computed(() => newsForSwiper.value)

onLoad(() => {
  const sys = uni.getSystemInfoSync()
  statusBarHeight.value = sys.statusBarHeight || 20
  
  // #ifdef MP-WEIXIN
  const menuButton = uni.getMenuButtonBoundingClientRect()
  customBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height
  // #endif
})

onShow(() => {
  // 从本地存储获取用户信息
  const cached = uni.getStorageSync('userInfo')
  console.log('首页获取 userInfo:', cached)
  if (cached && cached.name) {
    user.value = { name: cached.name }
    console.log('设置用户名:', cached.name)
  }
  
  loadWeather()
  loadRepairs()
  loadActivities()
  loadNews()
})

// 加载天气数据 - 默认北京，可改为学校所在城市
async function loadWeather() {
  try {
    const data = await getWeatherNow('nanning')
    weather.value = {
      city: data.city || '校园天气',
      temp: data.temp,
      text: data.text,
      humidity: data.humidity || '--',
      icon: getWeatherIcon(data.icon)
    }
  } catch (err) {
    console.error('获取天气失败:', err)
    weather.value.city = '天气获取失败'
  }
}

// 心知天气图标转 Material Icons
function getWeatherIcon(code) {
  const iconMap = {
    '0': 'sunny', '1': 'sunny', '2': 'sunny',  // 晴
    '3': 'nights_stay',  // 晴（夜间）
    '4': 'partly_cloudy_day', '5': 'partly_cloudy_day', '6': 'partly_cloudy_day',  // 多云
    '7': 'partly_cloudy_night', '8': 'partly_cloudy_night',  // 多云（夜间）
    '9': 'cloud', '10': 'thunderstorm',  // 阴、雷阵雨
    '11': 'thunderstorm', '12': 'thunderstorm',
    '13': 'rainy', '14': 'rainy', '15': 'rainy', '16': 'rainy', '17': 'rainy', '18': 'rainy',  // 雨
    '19': 'ac_unit', '20': 'ac_unit', '21': 'ac_unit', '22': 'ac_unit',  // 雪
    '30': 'foggy', '31': 'foggy'  // 雾霾
  }
  return iconMap[code] || 'cloud'
}

// 加载报修列表
async function loadRepairs() {
  try {
    const res = await getRepairs()
    console.log('报修API返回:', res.data)
    const list = Array.isArray(res.data) ? res.data : (res.data.list || [])
    console.log('报修列表:', list)
    repairList.value = list.map(item => ({
      id: item.repairNo || item.id,
      issue: item.issueType,
      location: item.location,
      status: mapRepairStatus(item.status),
      date: item.createTime
    }))
  } catch (err) {
    console.error('加载报修列表失败:', err)
  }
}

function mapRepairStatus(status) {
  const map = {
    'pending': 'Pending',
    'received': 'Received', 
    'in_progress': 'In Progress',
    'completed': 'Completed'
  }
  return map[status] || status
}

// 加载活动列表
async function loadActivities() {
  try {
    const res = await getActivities({ page: 1, size: 4 })
    const data = res.data
    const list = (data.list || []).map(item => ({
      id: item.id,
      title: item.title,
      image: item.coverImage || 'https://via.placeholder.com/200x110',
      date: formatDate(item.startTime),
      location: item.location
    }))
    activityList.value = list
  } catch (err) {
    console.error('加载活动列表失败:', err)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

// 加载新闻列表
async function loadNews() {
  try {
    const res = await getNewsList({ page: 1, size: 5 })
    const data = res.data
    const list = Array.isArray(data) ? data : (data.list || [])
    newsList.value = list.map(item => ({
      id: item.id,
      title: item.title,
      summary: item.summary || item.description || '',
      image: item.coverImage || item.image || 'https://via.placeholder.com/104',
      time: formatNewsTime(item.publishTime || item.createTime)
    }))
  } catch (err) {
    // API 未实现时静默失败，使用模拟数据
    console.log('新闻接口未实现，使用模拟数据')
  }
}

function formatNewsTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const greet = computed(()=>{
  const h = new Date().getHours()
  return h<12?'早上好':h<18?'下午好':'晚上好'
})

function goFood(){ uni.reLaunch({ url:'/pages/services/food' }) }
function goRepair(){ uni.reLaunch({ url:'/pages/services/repair' }) }
function goAssistant(){ uni.navigateTo({ url:'/pages/assistant/assistant' }) }
function goActivities(){ uni.navigateTo({ url:'/pages/activity/list' }) }
function goOrders(){ uni.navigateTo({ url:'/pages/orders/orders' }) }
function goNotifs(){ uni.navigateTo({ url:'/pages/notifications/notifications' }) }
function goProfile(){ uni.navigateTo({ url:'/pages/profile/profile' }) }
function openNews(n){ uni.navigateTo({ url:'/pages/news/detail?id='+n.id }) }
function onSwiperClick(index) {
  const item = swiperList.value[index]
  if (item) openNews(item)
}
function onOrderSwiperClick(index) {
  // 可以在这里处理点击订单卡片的逻辑，例如跳转到订单详情
  const item = activeRepairs.value[index]
  if (item) {
    // 假设跳转到报修详情，这里需要根据实际路由调整
    // uni.navigateTo({ url: '/pages/repair/detail?id=' + item.id })
    goOrders() // 暂时跳转到订单列表
  }
}
function openActivity(a){ uni.navigateTo({ url:'/pages/activity/detail?id='+a.id }) }
</script>

<style scoped lang="scss">
/* 核心变量：保留主色调 */
$primary-blue: #3b82f6;
$primary-dark: #2563eb;
$bg-color: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page { background: $bg-color; min-height: 100vh; }
.content {
  padding: 0 20px; /* 移除顶部固定padding，改为动态style控制 */
  height: calc(100vh - 60px); /* 减去底部导航高度 */
}

/* 欢迎区 */
.welcome { margin-bottom: 28px; }
.header-text { margin-bottom: 16px; }
.greet {
  font-size: 24px;
  font-weight: 800;
  color: $text-main;
  display: block;
  letter-spacing: -0.5px;
  margin-bottom: 4px;
}
.hint {
  color: $text-sub;
  font-size: 13px;
  display: block;
}

/* 天气卡片 */
.weather {
  background: linear-gradient(135deg, $primary-blue, $primary-dark); /* 保持主色调 */
  color: #fff;
  border-radius: 20px;
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 20px rgba(37, 99, 235, 0.25); /* 优化阴影质感 */
}
.w-left { display: flex; flex-direction: column; gap: 12px; }
.loc {
  display: flex; align-items: center; gap: 6px; opacity: 0.9;
  .material-symbols-outlined { font-size: 16px; }
}
.loc-text { font-size: 13px; font-weight: 500; }
.w-main { display: flex; align-items: center; gap: 16px; }
.temp {
  font-size: 40px;
  font-weight: 700;
  line-height: 1;
}
.w-meta { display: flex; flex-direction: column; font-size: 13px; opacity: 0.9; }
.humidity { font-size: 11px; opacity: 0.7; margin-top: 2px; }
.big { font-size: 56px; opacity: 0.9; font-variation-settings: 'FILL' 1; }

/* 模块通用 */
.section { margin-bottom: 36px; }
.title {
  font-size: 18px;
  font-weight: 700;
  color: $text-main;
  margin-bottom: 16px;
  display: block;
}
.row {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
  .title { margin-bottom: 0; }
}
.link { color: $primary-dark; font-size: 13px; font-weight: 600; }

/* 快捷功能 */
.quick {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px; /* 增大间距 */
}
.q-item {
  display: flex; flex-direction: column; align-items: center; gap: 10px;
  &:active { transform: scale(0.96); transition: transform 0.1s; } /* 添加点击反馈 */
}
.q-icon {
  width: 60px; height: 60px; border-radius: 20px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}
.q-icon .material-symbols-outlined { font-size: 28px; font-variation-settings: 'FILL' 1; }

/* 配色微调：保持色相，降低饱和度增加舒适感 */
.orange { background: #FFF7ED; color: #EA580C; }
.blue { background: #EFF6FF; color: #2563EB; }
.indigo { background: #EEF2FF; color: #4F46E5; }
.purple { background: #F5F3FF; color: #9333EA; }
.q-text { font-size: 12px; color: $text-sub; font-weight: 600; }

/* 订单卡片 - 轮播 */
.order-swiper-box { margin: 0 -20px; } /* 抵消父级 padding，实现全宽轮播 */

.order-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px 20px;
  display: flex;
  gap: 16px;
  align-items: center;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.06);
  height: 100%;
  margin: 0 10px; /* 卡片间距 */
  box-sizing: border-box;
}

.icon {
  width: 44px; height: 44px; border-radius: 12px;
  background: #EFF6FF; color: $primary-dark; /* 使用主色调 */
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.icon .material-symbols-outlined { font-size: 24px; font-variation-settings: 'FILL' 1; }
.info { flex: 1; overflow: hidden; }
.row-inner { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.name { font-size: 15px; font-weight: 600; color: $text-main; }
.chip {
  font-size: 11px; padding: 4px 10px; border-radius: 100px;
  font-weight: 600;
  flex-shrink: 0;
}
.amber { background: #FEF3C7; color: #B45309; }
.sub { font-size: 12px; color: $text-sub; display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.empty {
  background: #fff; border: 1px dashed #CBD5E1;
  color: #94A3B8; text-align: center; padding: 24px;
  border-radius: 16px; font-size: 13px;
}

/* 新闻轮播 - uview-plus 卡片式 */
.swiper-card {
  position: relative;
  width: calc(100% - 16px);
  height: 100%;
  margin: 0 8px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.swiper-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.swiper-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
}
.swiper-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 新闻列表 */
.news-list { display: flex; flex-direction: column; gap: 16px; }
.news {
  background: #fff; border-radius: 16px; overflow: hidden;
  display: flex; height: 104px;
  box-shadow: 0 2px 12px rgba(148, 163, 184, 0.05);
  border: none;
}
.news image { width: 104px; height: 104px; object-fit: cover; }
.n-body {
  flex: 1; padding: 14px 16px;
  display: flex; flex-direction: column; justify-content: space-between;
}
.n-title {
  font-size: 15px; font-weight: 600; color: $text-main; line-height: 1.4;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}
.n-footer { display: flex; justify-content: space-between; align-items: center; }
.n-sum {
  color: $text-sub; font-size: 12px; flex: 1;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-right: 8px;
}
.n-time { color: #94A3B8; font-size: 11px; flex-shrink: 0; }

/* 校园活动 */
.grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.act {
  background: #fff; border-radius: 16px; overflow: hidden;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.06);
  border: none;
}
.cover { width: 100%; height: 110px; object-fit: cover; }
.a-body { padding: 14px; }
.a-title {
  font-size: 14px; font-weight: 600; color: $text-main;
  margin-bottom: 8px; display: block;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.a-meta { display: flex; align-items: center; gap: 4px; color: $text-sub; }
.small-icon { font-size: 14px; color: #94A3B8; }
.a-date { font-size: 11px; }
</style>