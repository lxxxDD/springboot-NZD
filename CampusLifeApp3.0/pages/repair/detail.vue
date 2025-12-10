<template>
  <view class="page">
    <u-navbar title="报修详情" :autoBack="true" bgColor="#fff" leftIconColor="#1E293B" placeholder></u-navbar>

    <scroll-view scroll-y class="content" v-if="repair">
      <!-- 状态卡片 -->
      <view class="status-card" :class="statusClass">
        <view class="status-icon">
          <text class="material-symbols-outlined">{{ statusIcon }}</text>
        </view>
        <view class="status-info">
          <text class="status-text">{{ statusText[repair.status] || repair.status }}</text>
          <text class="status-desc">{{ statusDesc }}</text>
        </view>
      </view>

      <!-- 基本信息 -->
      <view class="section-card">
        <view class="section-title">
          <text class="material-symbols-outlined">info</text>
          <text>基本信息</text>
        </view>
        
        <view class="info-row">
          <text class="label">工单号</text>
          <text class="value">{{ repair.id }}</text>
        </view>
        <view class="info-row">
          <text class="label">故障类型</text>
          <view class="type-tag" :class="getIconType(repair.issue)">
            <text class="material-symbols-outlined" style="font-size: 14px;">{{ getIconName(repair.issue) }}</text>
            <text>{{ issueText[repair.issue] || repair.issue }}</text>
          </view>
        </view>
        <view class="info-row">
          <text class="label">报修位置</text>
          <text class="value">{{ repair.location }}</text>
        </view>
        <view class="info-row">
          <text class="label">提交时间</text>
          <text class="value">{{ repair.date }}</text>
        </view>
      </view>

      <!-- 故障描述 -->
      <view class="section-card">
        <view class="section-title">
          <text class="material-symbols-outlined">description</text>
          <text>故障描述</text>
        </view>
        <text class="desc-text">{{ repair.description || '暂无描述' }}</text>
        
        <!-- 图片 -->
        <view v-if="repair.images && repair.images.length" class="image-list">
          <image 
            v-for="(img, idx) in repair.images" 
            :key="idx" 
            :src="img" 
            mode="aspectFill" 
            class="preview-img"
            @click="previewImage(idx)"
          ></image>
        </view>
      </view>

      <!-- 维修进度 -->
      <view class="section-card">
        <view class="section-title">
          <text class="material-symbols-outlined">timeline</text>
          <text>维修进度</text>
        </view>
        
        <view class="timeline">
          <view class="timeline-item" :class="{ active: stepIndex >= 0, done: stepIndex > 0 }">
            <view class="dot"></view>
            <view class="line"></view>
            <view class="timeline-content">
              <text class="time-title">已提交</text>
              <text class="time-desc">{{ repair.date }}</text>
            </view>
          </view>
          
          <view class="timeline-item" :class="{ active: stepIndex >= 1, done: stepIndex > 1 }">
            <view class="dot"></view>
            <view class="line"></view>
            <view class="timeline-content">
              <text class="time-title">已接单</text>
              <text class="time-desc" v-if="stepIndex >= 1">维修人员已确认</text>
            </view>
          </view>
          
          <view class="timeline-item" :class="{ active: stepIndex >= 2, done: stepIndex > 2 }">
            <view class="dot"></view>
            <view class="line"></view>
            <view class="timeline-content">
              <text class="time-title">维修中</text>
              <text class="time-desc" v-if="repair.technician">维修人员: {{ repair.technician }}</text>
            </view>
          </view>
          
          <view class="timeline-item" :class="{ active: stepIndex >= 3 }">
            <view class="dot"></view>
            <view class="timeline-content">
              <text class="time-title">已完成</text>
              <text class="time-desc" v-if="repair.completedAt">{{ repair.completedAt }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 评价（已完成时显示） -->
      <view v-if="repair.status === 'Completed'" class="section-card">
        <view class="section-title">
          <text class="material-symbols-outlined">star</text>
          <text>服务评价</text>
        </view>
        
        <view v-if="repair.rating" class="rating-done">
          <u-rate :value="repair.rating" :count="5" readonly activeColor="#F59E0B"></u-rate>
          <text class="feedback-text">{{ repair.feedback || '感谢您的评价' }}</text>
        </view>
        
        <view v-else class="rating-action">
          <text class="rating-hint">请对本次维修服务进行评价</text>
          <button class="rate-btn" @click="showRating = true">立即评价</button>
        </view>
      </view>

      <view style="height: 100px;"></view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar" v-if="repair && repair.status !== 'Completed'">
      <button class="btn-secondary" @click="callService">
        <text class="material-symbols-outlined">call</text>
        联系客服
      </button>
      <button class="btn-primary" @click="cancelRepair" v-if="repair.status === 'Pending'">
        取消报修
      </button>
    </view>

    <!-- 评价弹窗 -->
    <u-popup :show="showRating" @close="showRating = false" mode="bottom" :round="24">
      <view class="rating-popup">
        <text class="popup-title">评价维修服务</text>
        <u-rate v-model="ratingValue" :count="5" activeColor="#F59E0B" size="32"></u-rate>
        <u-textarea v-model="feedbackText" placeholder="请输入您的评价（选填）" :maxlength="200" count></u-textarea>
        <button class="submit-btn" @click="submitRating">提交评价</button>
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getRepairDetail } from '@/api/repairs.js'
const issueText = { Electric: '电路', Water: '水管', Wifi: '网络', Furniture: '家具', AC: '空调', Other: '其他' }
const statusText = { Received: '已接收', 'In Progress': '进行中', Completed: '已完成' }

const repair = ref(null)
const showRating = ref(false)
const ratingValue = ref(5)
const feedbackText = ref('')

// 根据状态计算进度步骤
const stepIndex = computed(() => {
  if (!repair.value) return 0
  const status = repair.value.status
  if (status === 'Pending') return 0
  if (status === 'Received') return 1
  if (status === 'In Progress') return 2
  if (status === 'Completed') return 3
  return 0
})

const statusClass = computed(() => {
  if (!repair.value) return ''
  const status = repair.value.status
  if (status === 'Completed') return 'completed'
  if (status === 'In Progress') return 'processing'
  if (status === 'Received') return 'received'
  return 'pending'
})

const statusIcon = computed(() => {
  if (!repair.value) return 'pending'
  const status = repair.value.status
  if (status === 'Completed') return 'check_circle'
  if (status === 'In Progress') return 'engineering'
  if (status === 'Received') return 'assignment_turned_in'
  return 'schedule'
})

const statusDesc = computed(() => {
  if (!repair.value) return ''
  const status = repair.value.status
  if (status === 'Completed') return '维修已完成，感谢您的耐心等待'
  if (status === 'In Progress') return '维修人员正在处理中'
  if (status === 'Received') return '已有维修人员接单'
  return '等待维修人员接单'
})

function getIconType(issue) {
  if (!issue) return 'general'
  if (issue.includes('Electric') || issue.includes('Light')) return 'electric'
  if (issue.includes('Water') || issue.includes('Pipe')) return 'water'
  if (issue.includes('Net') || issue.includes('Wifi')) return 'net'
  return 'general'
}

function getIconName(issue) {
  const type = getIconType(issue)
  if (type === 'electric') return 'bolt'
  if (type === 'water') return 'water_drop'
  if (type === 'net') return 'wifi'
  return 'build'
}

onLoad(async (options) => {
  const id = options.id
  if (id) {
    await loadRepairDetail(id)
  }
})

async function loadRepairDetail(id) {
  try {
    const res = await getRepairDetail(id)
    if (res.code === 200 && res.data) {
      const item = res.data
      repair.value = {
        id: item.repairNo || item.id,
        repairId: item.id,
        issue: item.issueType,
        location: item.location,
        description: item.description,
        images: item.images || [],
        status: mapStatus(item.status),
        technician: item.technicianName,
        rating: item.rating,
        feedback: item.feedback,
        date: formatTime(item.createTime),
        completedAt: item.completedAt ? formatTime(item.completedAt) : null
      }
    }
  } catch (e) {
    console.error('加载报修详情失败:', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

function mapStatus(status) {
  const map = {
    'pending': 'Pending',
    'received': 'Received',
    'in_progress': 'In Progress',
    'completed': 'Completed'
  }
  return map[status] || status
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

function previewImage(idx) {
  uni.previewImage({
    urls: repair.value.images,
    current: idx
  })
}

function callService() {
  uni.makePhoneCall({ phoneNumber: '400-123-4567' })
}

function cancelRepair() {
  uni.showModal({
    title: '确认取消',
    content: '确定要取消这个报修吗？',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({ title: '已取消', icon: 'success' })
        setTimeout(() => uni.navigateBack(), 1000)
      }
    }
  })
}

function submitRating() {
  uni.showToast({ title: '评价成功', icon: 'success' })
  showRating.value = false
  repair.value.rating = ratingValue.value
  repair.value.feedback = feedbackText.value
}
</script>

<style scoped lang="scss">
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page { background: $bg-page; min-height: 100vh; }
.content { padding: 16px 20px; }

/* 状态卡片 */
.status-card {
  display: flex; align-items: center; gap: 16px;
  padding: 20px; border-radius: 16px; margin-bottom: 16px;
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  
  &.completed { background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%); }
  &.processing { background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%); }
  &.received { background: linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%); }
}

.status-icon {
  width: 56px; height: 56px; border-radius: 50%;
  background: rgba(255,255,255,0.8);
  display: flex; align-items: center; justify-content: center;
  
  .material-symbols-outlined { font-size: 28px; color: $text-main; }
}

.status-info { flex: 1; }
.status-text { font-size: 18px; font-weight: 700; color: $text-main; display: block; }
.status-desc { font-size: 13px; color: $text-sub; margin-top: 4px; }

/* 区块卡片 */
.section-card {
  background: #fff; border-radius: 16px; padding: 20px;
  margin-bottom: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.section-title {
  display: flex; align-items: center; gap: 8px;
  font-size: 16px; font-weight: 600; color: $text-main;
  margin-bottom: 16px;
  
  .material-symbols-outlined { font-size: 20px; color: $primary; }
}

.info-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 0; border-bottom: 1px solid #F1F5F9;
  
  &:last-child { border-bottom: none; }
}

.label { font-size: 14px; color: $text-sub; }
.value { font-size: 14px; color: $text-main; font-weight: 500; }

.type-tag {
  display: flex; align-items: center; gap: 4px;
  padding: 4px 10px; border-radius: 100px; font-size: 13px;
  
  &.electric { background: #FEF3C7; color: #B45309; }
  &.water { background: #DBEAFE; color: #1D4ED8; }
  &.net { background: #EDE9FE; color: #7C3AED; }
  &.general { background: #F1F5F9; color: #475569; }
}

.desc-text { font-size: 14px; color: $text-main; line-height: 1.6; }

.image-list {
  display: flex; gap: 8px; margin-top: 12px; flex-wrap: wrap;
}

.preview-img {
  width: 80px; height: 80px; border-radius: 8px; object-fit: cover;
}

/* 时间线 */
.timeline { padding-left: 8px; }

.timeline-item {
  position: relative; padding-left: 24px; padding-bottom: 24px;
  
  &:last-child { padding-bottom: 0; }
  &:last-child .line { display: none; }
}

.dot {
  position: absolute; left: 0; top: 4px;
  width: 12px; height: 12px; border-radius: 50%;
  background: #E2E8F0; border: 2px solid #fff;
  box-shadow: 0 0 0 2px #E2E8F0;
}

.line {
  position: absolute; left: 5px; top: 20px; bottom: 0;
  width: 2px; background: #E2E8F0;
}

.timeline-item.active .dot { background: $primary; box-shadow: 0 0 0 2px $primary; }
.timeline-item.done .dot { background: #10B981; box-shadow: 0 0 0 2px #10B981; }
.timeline-item.done .line { background: #10B981; }

.timeline-content { padding-top: 0; }
.time-title { font-size: 14px; font-weight: 600; color: $text-main; display: block; }
.time-desc { font-size: 12px; color: $text-sub; margin-top: 2px; }

/* 评价 */
.rating-done { text-align: center; padding: 16px 0; }
.feedback-text { display: block; margin-top: 12px; color: $text-sub; font-size: 14px; }

.rating-action { text-align: center; padding: 16px 0; }
.rating-hint { font-size: 14px; color: $text-sub; display: block; margin-bottom: 12px; }
.rate-btn {
  background: $primary; color: #fff; border: none;
  padding: 10px 32px; border-radius: 100px; font-size: 14px;
}

/* 底部栏 */
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; padding: 12px 20px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  display: flex; gap: 12px;
  box-shadow: 0 -4px 12px rgba(0,0,0,0.05);
}

.btn-secondary, .btn-primary {
  flex: 1; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  font-size: 15px; font-weight: 600; border: none;
}

.btn-secondary {
  background: #F1F5F9; color: $text-main;
  .material-symbols-outlined { font-size: 18px; }
}

.btn-primary { background: $primary; color: #fff; }

/* 评价弹窗 */
.rating-popup {
  padding: 24px 20px 40px;
  display: flex; flex-direction: column; align-items: center; gap: 20px;
}

.popup-title { font-size: 18px; font-weight: 700; color: $text-main; }

.submit-btn {
  width: 100%; height: 48px; background: $primary; color: #fff;
  border: none; border-radius: 12px; font-size: 16px; font-weight: 600;
  margin-top: 12px;
}
</style>
