<template>
  <view class="sheet">
    <view class="handle-bar" />
    <view class="header">
      <text class="title">维修进度</text>
      <text class="sub">{{ repair.id }} • {{ repair.location }}</text>
    </view>
    <!-- 维修人员信息 -->
    <view v-if="repair.technicianName" class="technician-card">
      <view class="tech-avatar">
        <text class="material-symbols-outlined" style="font-size: 32px; color: #64748B; line-height: 56px; text-align: center; width: 100%; display: block;">person</text>
      </view>
      <view class="tech-info">
        <text class="tech-name">{{ repair.technicianName }}</text>
        <text class="tech-specialty">维修师傅</text>
      </view>
      <view v-if="repair.technicianPhone" class="tech-contact" @click="callTechnician">
        <text class="material-symbols-outlined">call</text>
      </view>
    </view>

    <view class="timeline">
      <view class="t-item" v-for="(s, i) in steps" :key="i">
        <view class="line" :class="{ done: i < currentStep }" />
        <view class="dot" :class="{ done: i <= currentStep }">
          <text v-if="i <= currentStep" class="material-symbols-outlined">check</text>
          <text v-else class="material-symbols-outlined">schedule</text>
        </view>
        <view class="t-body">
          <view class="t-title" :class="{ done: i <= currentStep }">{{ s.title }}</view>
          <view class="t-time">{{ s.time }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { defineProps, computed } from 'vue'

const props = defineProps({
  repair: {
    type: Object,
    default: () => ({ id: '', location: '', status: 'Received' })
  }
})

function zhTime(input) {
  if (!input) return ''
  const s = String(input).trim().toLowerCase()
  if (/[\u4e00-\u9fa5]/.test(s)) return String(input) // already Chinese
  if (/just\s*now/.test(s)) return '刚刚'
  let m = s.match(/(\d+)\s*minute/)
  if (m) return `${m[1]}分钟前`
  m = s.match(/(\d+)\s*hour/)
  if (m) return `${m[1]}小时前`
  m = s.match(/(\d+)\s*day/)
  if (m) return `${m[1]}天前`
  m = s.match(/(\d+)\s*week/)
  if (m) return `${m[1]}周前`
  m = s.match(/(\d+)\s*month/)
  if (m) return `${m[1]}个月前`
  m = s.match(/(\d+)\s*year/)
  if (m) return `${m[1]}年前`
  return String(input)
}

const steps = computed(() => [
  { title: '已接收请求', time: zhTime(props.repair.date || props.repair.createTime || '2 days ago') },
  { 
    title: props.repair.technicianName ? `已指派: ${props.repair.technicianName}` : '已指派技术员', 
    time: props.repair.status !== 'received' && props.repair.status !== 'Received' ? zhTime('1 hour ago') : '等待指派…' 
  },
  { title: '维修完成', time: props.repair.status === 'completed' || props.repair.status === 'Completed' ? zhTime('just now') : '处理中…' }
])

const currentStep = computed(() => {
  const s = (props.repair.status || '').toLowerCase()
  if (s === 'completed') return 2
  if (s === 'in_progress' || s === 'in progress') return 1
  return 0 // received
})
</script>

<style scoped lang="scss">
.sheet {
  background: #fff;
  border-top-left-radius: 24px;
  border-top-right-radius: 24px;
  padding: 16px 22px 44px;
  min-height: 55vh;
}
.handle-bar {
  width: 48px;
  height: 5px;
  background: #d1d5db;
  border-radius: 999px;
  margin: 0 auto 16px;
}
.header {
  margin-bottom: 24px;
}
.title {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  display: block;
}
.sub {
  font-size: 14px;
  color: #6b7280;
}
.timeline {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.t-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 8px 0;
  position: relative;
}
.dot {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  background: #e5e7eb;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}
.dot .material-symbols-outlined {
    font-size: 20px;
}
.dot.done {
  background: #22c55e;
  color: #fff;
}
.line {
  position: absolute;
  left: 15px;
  top: 40px;
  bottom: -16px;
  width: 2px;
  background: #e5e7eb;
  z-index: 1;
}
.line.done {
  background: #22c55e;
}
.t-item:last-child .line {
  display: none;
}
.t-body {
  display: flex;
  flex-direction: column;
  padding-top: 4px;
}
.t-title {
  font-size: 16px;
  color: #6b7280;
}
.t-title.done {
  color: #0f172a;
  font-weight: 700;
}
.t-time {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 2px;
}

/* 维修人员卡片 */
.technician-card {
  display: flex;
  align-items: center;
  background: #F8FAFC;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 20px;
  gap: 12px;
}
.tech-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #E2E8F0;
}
.tech-info {
  flex: 1;
}
.tech-name {
  font-size: 16px;
  font-weight: 700;
  color: #0F172A;
  display: block;
}
.tech-specialty {
  font-size: 13px;
  color: #64748B;
  display: block;
  margin-top: 2px;
}
.tech-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 13px;
  color: #F59E0B;
}
.tech-rating .star {
  font-size: 16px;
  color: #F59E0B;
}
.tech-contact {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #2563EB;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.tech-contact .material-symbols-outlined {
  font-size: 22px;
}
</style>

