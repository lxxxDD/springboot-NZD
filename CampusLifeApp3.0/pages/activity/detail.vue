<template>
  <view class="page">
    <u-navbar title=" " :autoBack="true" bgColor="transparent" leftIconColor="#fff"></u-navbar>

    <scroll-view scroll-y class="content">
      <view class="cover-wrapper">
        <image :src="item.image" class="cover-img" mode="aspectFill" />
        <view class="cover-gradient"></view>
      </view>

      <view class="card-container animate-slide-up">
        <view class="handle-bar"></view>

        <text class="title">{{ item.title }}</text>

        <view class="meta-grid">
          <view class="meta-item">
            <view class="icon-box blue">
              <u-icon name="clock" color="#3B82F6" size="20"></u-icon>
            </view>
            <view class="meta-text">
              <text class="label">活动时间</text>
              <text class="val">{{ item.formattedStartTime }}</text>
              <text class="val" style="font-size: 11px; color: #64748B;">至 {{ item.formattedEndTime }}</text>
            </view>
          </view>

          <view class="meta-item">
            <view class="icon-box orange">
              <u-icon name="map-fill" color="#F97316" size="20"></u-icon>
            </view>
            <view class="meta-text">
              <text class="label">地点</text>
              <text class="val">{{ item.location }}</text>
            </view>
          </view>
        </view>
        
        <view class="time-info-box">
          <view class="time-row">
            <text class="label">报名开始：</text>
            <text class="val">{{ item.formattedRegStartTime }}</text>
          </view>
          <view class="time-row">
            <text class="label">报名截止：</text>
            <text class="val">{{ item.formattedRegEndTime }}</text>
          </view>
        </view>

        <u-divider text="活动详情" textSize="12" textColor="#94A3B8"></u-divider>

        <view class="desc-box">
          <text class="desc-text">{{ item.description || '暂无活动详情介绍' }}</text>
        </view>
        
        <view class="participants-info" v-if="item.maxParticipants">
          <text class="participants-text">已报名 {{ item.currentParticipants }} / {{ item.maxParticipants }} 人</text>
        </view>

        <view style="height: 100px"></view>
      </view>
    </scroll-view>

    <view class="bottom-bar animate-fade-in">
      <view class="action-inner">
        <view class="countdown-box" v-if="countdownTime > 0">
          <text class="countdown-label">{{ countdownLabel }}</text>
          <u-count-down :time="countdownTime" format="DD天HH:mm:ss" @finish="refreshCountdown"></u-count-down>
        </view>
        <view class="status-text" v-else>
          <text>{{ countdownLabel }}</text>
        </view>
        
        <u-button
            :text="getButtonText"
            type="primary"
            shape="circle"
            :disabled="isButtonDisabled"
            :customStyle="getButtonStyle"
            @click="join"
        ></u-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getActivityDetail, registerActivity, cancelActivity } from '@/api/activities.js'

const item = ref({ 
  id: 0, 
  title: '', 
  image: '', 
  location: '',
  description: '',
  isRegistered: false,
  currentParticipants: 0,
  maxParticipants: 0,
  startTime: null,
  endTime: null,
  registrationStartTime: null,
  registrationEndTime: null,
  formattedStartTime: '',
  formattedEndTime: '',
  formattedRegStartTime: '',
  formattedRegEndTime: ''
})
const loading = ref(true)
const countdownTime = ref(0)
const countdownLabel = ref('')
const timeOffset = ref(0)

onLoad(async (options) => {
  const id = options.id
  await loadActivityDetail(id)
})

async function loadActivityDetail(id) {
  loading.value = true
  try {
    const res = await getActivityDetail(id)
    const data = res.data
    item.value = {
      id: data.id,
      title: data.title,
      description: data.description || '',
      image: data.coverImage || 'https://via.placeholder.com/400x300',
      location: data.location,
      startTime: data.startTime,
      endTime: data.endTime,
      registrationStartTime: data.registrationStartTime,
      registrationEndTime: data.registrationEndTime,
      formattedStartTime: formatTime(data.startTime),
      formattedEndTime: formatTime(data.endTime),
      formattedRegStartTime: formatTime(data.registrationStartTime),
      formattedRegEndTime: formatTime(data.registrationEndTime),
      currentParticipants: data.currentParticipants || 0,
      maxParticipants: data.maxParticipants,
      organizer: data.organizerName,
      status: data.status,
      isRegistered: data.isRegistered
    }
    // 同步服务器时间差
    if (data.serverTime) {
      const serverTime = new Date(data.serverTime).getTime()
      const localTime = new Date().getTime()
      timeOffset.value = serverTime - localTime
    }
    updateCountdown()
  } catch (err) {
    console.error('加载活动详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function getNow() {
  return new Date().getTime() + timeOffset.value
}

function formatTime(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${month}月${day}日 ${hour}:${minute}`
}

function updateCountdown() {
  const now = getNow()
  const regStart = new Date(item.value.registrationStartTime).getTime()
  const regEnd = new Date(item.value.registrationEndTime).getTime()
  const actStart = new Date(item.value.startTime).getTime()
  const actEnd = new Date(item.value.endTime).getTime()

  if (now < regStart) {
    countdownLabel.value = '距离报名开始'
    countdownTime.value = regStart - now
  } else if (now < regEnd) {
    countdownLabel.value = '报名截止倒计时'
    countdownTime.value = regEnd - now
  } else if (now < actStart) {
    countdownLabel.value = '距离活动开始'
    countdownTime.value = actStart - now
  } else if (now < actEnd) {
    countdownLabel.value = '活动结束倒计时'
    countdownTime.value = actEnd - now
  } else {
    countdownLabel.value = '活动已结束'
    countdownTime.value = 0
  }
}

function refreshCountdown() {
  updateCountdown()
}

const getButtonText = computed(() => {
  if (item.value.isRegistered) return '取消报名'
  
  const now = getNow()
  const regStart = new Date(item.value.registrationStartTime).getTime()
  const regEnd = new Date(item.value.registrationEndTime).getTime()
  
  if (now < regStart) return '报名未开始'
  if (now > regEnd) return '报名已截止'
  if (item.value.maxParticipants && item.value.currentParticipants >= item.value.maxParticipants) return '名额已满'
  
  return '立即报名'
})

const isButtonDisabled = computed(() => {
  if (item.value.isRegistered) return false // Allow cancel
  
  const now = getNow()
  const regStart = new Date(item.value.registrationStartTime).getTime()
  const regEnd = new Date(item.value.registrationEndTime).getTime()
  
  return now < regStart || now > regEnd || (item.value.maxParticipants && item.value.currentParticipants >= item.value.maxParticipants)
})

const getButtonStyle = computed(() => {
  if (item.value.isRegistered) {
    return 'flex: 1; height: 48px; background: #EF4444; border: none; font-weight: 700;'
  }
  if (isButtonDisabled.value) {
    return 'flex: 1; height: 48px; background: #94A3B8; border: none; font-weight: 700;'
  }
  return 'flex: 1; height: 48px; background: #0F172A; border: none; font-weight: 700;'
})

async function join() {
  if (item.value.isRegistered) {
    // 已报名，取消报名
    try {
      await cancelActivity(item.value.id)
      item.value.isRegistered = false
      item.value.currentParticipants--
      uni.showToast({ title: '已取消报名', icon: 'success' })
    } catch (err) {
      console.error('取消报名失败:', err)
    }
  } else {
    // 未报名，进行报名
    try {
      // 传入 showError: false 禁止全局错误提示
      await registerActivity(item.value.id, { showError: false })
      item.value.isRegistered = true
      item.value.currentParticipants++
      uni.showToast({ title: '报名成功', icon: 'success' })
    } catch (err) {
      // 优先判断错误码
      if (err.code === 2001 || (err.message && (err.message.includes('已报名') || err.message.includes('Already registered')))) {
        uni.showToast({ title: '您已报名参加', icon: 'none' })
        item.value.isRegistered = true
      } else {
        uni.showToast({ title: err.message || '报名失败', icon: 'none' })
      }
      console.error('报名失败:', err)
    }
  }
}
</script>

<style scoped lang="scss">
.page { background: #fff; min-height: 100vh; }

.cover-wrapper {
  position: relative;
  width: 100%;
  height: 320px;

  .cover-img { width: 100%; height: 100%; }
  .cover-gradient {
    position: absolute; bottom: 0; left: 0; right: 0;
    height: 150px;
    background: linear-gradient(to bottom, transparent, rgba(0,0,0,0.6));
  }
}

.card-container {
  position: relative;
  top: -40px;
  background: #fff;
  border-top-left-radius: 32px;
  border-top-right-radius: 32px;
  padding: 24px;
  min-height: 500px;
}

.handle-bar {
  width: 40px; height: 4px; background: #E2E8F0; border-radius: 10px;
  margin: 0 auto 20px;
}

.title {
  font-size: 24px; font-weight: 800; color: #0F172A;
  line-height: 1.3; margin-bottom: 24px; display: block;
}

.meta-grid {
  display: flex; flex-direction: column; gap: 16px; margin-bottom: 24px;
}

.meta-item {
  display: flex; align-items: flex-start; gap: 12px;
}

.icon-box {
  width: 36px; height: 36px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;

  &.blue { background: #EFF6FF; }
  &.orange { background: #FFF7ED; }
}

.meta-text {
  display: flex; flex-direction: column; gap: 4px;
  .label { font-size: 12px; color: #94A3B8; }
  .val { font-size: 14px; font-weight: 600; color: #1E293B; line-height: 1.4; }
}

.time-info-box {
  margin-bottom: 32px;
  padding-left: 48px; /* Align with text */
}
.time-row {
  display: flex; align-items: center; gap: 8px; margin-bottom: 8px;
  &:last-child { margin-bottom: 0; }
  .label { font-size: 13px; color: #64748B; }
  .val { font-size: 13px; color: #1E293B; font-weight: 500; }
}

.desc-box { margin-top: 20px; margin-bottom: 24px; }
.desc-text { font-size: 15px; color: #334155; line-height: 1.8; white-space: pre-wrap; text-align: justify; }

.participants-info {
  padding: 16px;
  background: #F8FAFC;
  border-radius: 16px;
  text-align: center;
  border: 1px solid #F1F5F9;
}
.participants-text {
  font-size: 14px;
  color: #64748B;
  font-weight: 500;
  
  text { color: #10B981; font-weight: 700; margin: 0 4px; }
}

.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff;
  padding: 12px 24px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  border-top: 1px solid #F1F5F9;
  z-index: 10;
}

.action-inner { display: flex; align-items: center; gap: 16px; }



.countdown-box {
  display: flex; flex-direction: column; align-items: flex-start;
  margin-right: 16px;
  min-width: 120px;
}
.countdown-label { font-size: 11px; color: #64748B; margin-bottom: 2px; }
.status-text { font-size: 14px; color: #64748B; font-weight: 600; margin-right: 16px; }

.animate-slide-up { animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
.animate-fade-in { animation: fadeIn 0.5s 0.3s backwards; }

@keyframes slideUp { from { transform: translateY(40px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
</style>