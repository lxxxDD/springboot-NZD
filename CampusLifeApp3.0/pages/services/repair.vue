<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <!-- <TopNav :badge="unreadCount>0" :avatar="user.avatar" @notif="goNotifs" /> -->
 <u-navbar
        title=""
        :fixed="false"
        :autoBack="false"
        bgColor="transparent"
        leftIcon=""
        :border="false"
      ></u-navbar>
    <scroll-view scroll-y class="content">
      <view class="tabs-wrapper animate-enter" style="--delay: 0s">
        <view class="tabs-inner">
          <view class="tab-bg" style="transform: translateX(100%)"></view>
          <view class="tab-item" @click="goFood">
            <text>食堂点餐</text>
          </view>
          <view class="tab-item active">
            <text>校园报修</text>
          </view>
        </view>
      </view>

      <view v-if="actives.length" class="repair-section animate-enter" style="--delay: 0.1s">
        <view class="section-header">
          <text class="title">进度跟踪</text>
          <text class="count">{{ actives.length }} 个进行中</text>
        </view>

        <view class="list">
          <view
              v-for="(r, index) in actives"
              :key="r.id"
              class="repair-card animate-slide-in"
              :style="{ animationDelay: (index * 0.1 + 0.2) + 's' }"
              @click="track(r)"
          >
            <view class="card-main">
              <view class="icon-box" :class="getIconType(r.issue)">
                <text class="material-symbols-outlined" :style="{ fontSize: '24px', color: getIconColor(r.issue) }">{{ getIconName(r.issue) }}</text>
              </view>

              <view class="info">
                <view class="header-row">
                  <text class="issue-name">{{ issueText[r.issue] || r.issue }}</text>
                  <view class="status-badge" :class="getStatusClass(r.status)">
                    {{ statusText[r.status] || r.status }}
                  </view>
                </view>
                <text class="location-text">
                  <u-icon name="map" size="12" color="#94A3B8" style="margin-right: 2px; display:inline-block;"></u-icon>
                  {{ r.location }}
                </text>
                <text class="id-text">工单号: {{ r.id }}</text>
              </view>
            </view>

            <view class="card-footer">
              <view class="time-tag">
                <u-icon name="clock" size="12" color="#94A3B8"></u-icon>
                <text>{{ r.date || '刚刚' }}</text>
              </view>
              <button class="track-btn" @click.stop="showTrack(r)">
                跟踪进度 <u-icon name="arrow-right" size="10" color="#2563EB"></u-icon>
              </button>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="empty-state animate-enter" style="--delay: 0.1s">
        <view class="empty-icon">
          <u-icon name="checkmark-circle" size="48" color="#CBD5E1"></u-icon>
        </view>
        <text>当前没有进行中的报修</text>
      </view>

      <!-- 已完成的报修 -->
      <view v-if="completed.length" class="repair-section animate-enter" style="--delay: 0.2s; margin-top: 24px;">
        <view class="section-header">
          <text class="title">已完成</text>
          <text class="count">{{ completed.length }} 个</text>
        </view>

        <view class="list">
          <view
              v-for="(r, index) in completed"
              :key="r.id"
              class="repair-card completed-card animate-slide-in"
              :style="{ animationDelay: (index * 0.1 + 0.2) + 's' }"
              @click="track(r)"
          >
            <view class="card-main">
              <view class="icon-box completed-icon">
                <text class="material-symbols-outlined" style="font-size: 24px; color: #10B981;">check_circle</text>
              </view>

              <view class="info">
                <view class="header-row">
                  <text class="issue-name">{{ issueText[r.issue] || r.issue }}</text>
                  <view class="status-badge completed">已完成</view>
                </view>
                <text class="location-text">
                  <u-icon name="map" size="12" color="#94A3B8" style="margin-right: 2px; display:inline-block;"></u-icon>
                  {{ r.location }}
                </text>
                <text class="id-text">工单号: {{ r.id }}</text>
              </view>
            </view>

            <view class="card-footer">
              <view class="time-tag">
                <u-icon name="clock" size="12" color="#10B981"></u-icon>
                <text>{{ formatCompletedTime(r.completedAt) }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="tip-card animate-enter" style="--delay: 0.3s">
        <view class="tip-icon">
          <u-icon name="info-circle-fill" color="#F97316" size="20"></u-icon>
        </view>
        <view class="tip-content">
          <text class="tip-title">温馨提示</text>
          <text class="tip-desc">填写准确的位置信息（如：宿舍号、楼层），并上传现场照片，有助于维修师傅更快定位问题。</text>
        </view>
      </view>

      <view style="height: 100px"></view>
    </scroll-view>

    <view class="fab-wrapper animate-bounce-in">
      <button class="fab-create" @click="create">
        <u-icon name="plus" color="#fff" size="18" bold></u-icon>
        <text>发起新报修</text>
      </button>
    </view>

    <BottomNav active="services" />

    <u-popup :show="showProgress" @close="showProgress = false" mode="bottom" :round="24" z-index="10076">
      <view class="popup-container">
        <RepairProgress v-if="selectedRepair" :repair="selectedRepair" />
      </view>
    </u-popup>
  </view>
</template>

<script setup>
import TopNav from '@/components/TopNav.vue'
import BottomNav from '@/components/BottomNav.vue'
import RepairProgress from '@/components/RepairProgress.vue'
const issueText = { Electric: '电路', Water: '水管', Wifi: '网络', Furniture: '家具', AC: '空调', Other: '其他' }
const statusText = { 
  Received: '已接收', 
  received: '已接收',
  assigned: '已指派',
  'In Progress': '处理中', 
  in_progress: '处理中',
  Completed: '已完成',
  completed: '已完成'
}
import { getRepairs } from '@/api/repairs.js'
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const unreadCount = ref(0)
const repairList = ref([])
const loading = ref(false)

onShow(() => {
  loadRepairs()
})

async function loadRepairs() {
  loading.value = true
  const userInfo = uni.getStorageSync('userInfo')
  console.log('=== 当前登录用户 ===', userInfo)
  console.log('=== 用户ID ===', userInfo?.id, userInfo?.userId)
  try {
    const res = await getRepairs()
    console.log('=== 报修列表API响应 ===', JSON.stringify(res, null, 2))
    // 根据 API 文档，返回 ResultListRepairVO
    // RepairVO 字段: id, repairNo, location, issueType, description, images, status, 
    //               technicianName, rating, feedback, createTime, completedAt
    const list = Array.isArray(res.data) ? res.data : (res.data?.list || [])
    console.log('解析后的列表:', list)
    repairList.value = list.map(item => ({
      id: item.repairNo || item.id,
      repairId: item.id,
      issue: item.issueType,
      location: item.location,
      description: item.description,
      images: item.images || [],
      status: mapStatus(item.status),
      technicianName: item.technicianName,
      rating: item.rating,
      feedback: item.feedback,
      date: formatTime(item.createTime),
      completedAt: item.completedAt
    }))
  } catch (err) {
    console.error('加载报修列表失败:', err)
  } finally {
    loading.value = false
  }
}

function mapStatus(status) {
  // 将后端状态映射为前端显示状态
  const map = {
    'pending': 'Pending',
    'received': 'Received',
    'in_progress': 'In Progress',
    'completed': 'Completed'
  }
  return map[status] || status
}

function formatTime(dateStr) {
  if (!dateStr) return '刚刚'
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  
  return `${date.getMonth() + 1}/${date.getDate()}`
}

function formatCompletedTime(dateStr) {
  if (!dateStr) return '已完成'
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚完成'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前完成'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前完成'
  
  return `${date.getMonth() + 1}/${date.getDate()} 完成`
}

const actives = computed(() => repairList.value.filter(r => r.status !== 'Completed'))
const completed = computed(() => repairList.value.filter(r => r.status === 'Completed'))

const showProgress = ref(false)
const selectedRepair = ref(null)

// 辅助函数：根据问题类型返回图标
function getIconType(issue) {
  if (issue.includes('Electric') || issue.includes('Light')) return 'electric';
  if (issue.includes('Water') || issue.includes('Pipe')) return 'water';
  if (issue.includes('Net') || issue.includes('Wifi')) return 'net';
  return 'general';
}

function getIconName(issue) {
  const type = getIconType(issue);
  // 使用 Material Symbols 图标名（和报修发布页面一致）
  if (type === 'electric') return 'bolt';
  if (type === 'water') return 'water_drop';
  if (type === 'net') return 'wifi';
  return 'build';
}

function getIconColor(issue) {
  const type = getIconType(issue);
  const map = { electric: '#F59E0B', water: '#3B82F6', net: '#8B5CF6', general: '#64748B' };
  return map[type];
}

function getStatusClass(status) {
  if (status === 'In Progress') return 'processing';
  if (status === 'Received') return 'received';
  return 'default';
}

function goFood(){ uni.reLaunch({ url:'/pages/services/food' }) }
function create(){ uni.navigateTo({ url:'/pages/repair/create' }) }
function track(r){
  uni.navigateTo({ url: '/pages/repair/detail?id=' + r.repairId })
}
function showTrack(r){
  selectedRepair.value = r
  showProgress.value = true
}
function goNotifs(){ uni.navigateTo({ url:'/pages/notifications/notifications' }) }
</script>

<style scoped lang="scss">
/* 变量 */
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;
$card-radius: 20px;

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: -100px;
  right: -50px; /* 报修页面放在右边，和Food页面错开 */
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.06) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.content {
  height: calc(100vh - 56px - 70px);
  padding: 12px 20px;
  position: relative;
  z-index: 1;
}

/* Tabs */
.tabs-wrapper {
  margin-bottom: 20px;
}

.tabs-inner {
  position: relative;
  background: #F1F5F9;
  border-radius: 100px;
  padding: 4px;
  display: flex;
  height: 44px;
}

.tab-bg {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: #fff;
  border-radius: 100px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  font-size: 14px;
  font-weight: 600;
  color: $text-sub;

  &.active { color: #1E293B; font-weight: 700; }
}

/* Repair Section */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 12px;
  padding: 0 4px;
}

.title {
  font-size: 16px;
  font-weight: 700;
  color: $text-main;
}

.count {
  font-size: 12px;
  color: $text-sub;
  background: #F1F5F9;
  padding: 2px 8px;
  border-radius: 100px;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* Repair Card */
.repair-card {
  background: #fff;
  border-radius: $card-radius;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.6);
  transition: transform 0.1s;

  &:active { transform: scale(0.99); }
}

.card-main {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.icon-box {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.electric { background: #FEF3C7; border: 1px solid #FEF9C3; }
  &.water { background: #EFF6FF; border: 1px solid #E0E7FF; }
  &.net { background: #F3E8FF; border: 1px solid #EDE9FE; }
  &.general { background: #F1F5F9; border: 1px solid #E2E8F0; }
}

.info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.issue-name {
  font-size: 15px;
  font-weight: 700;
  color: $text-main;
}

.status-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 6px;
  font-weight: 600;

  &.processing { background: #FEF3C7; color: #B45309; }
  &.received { background: #EFF6FF; color: #2563EB; }
  &.completed { background: #D1FAE5; color: #059669; }
  &.default { background: #F1F5F9; color: #64748B; }
}

.completed-icon {
  background: #D1FAE5 !important;
}

.location-text {
  font-size: 13px;
  color: $text-sub;
  display: flex;
  align-items: center;
  margin-bottom: 2px;
}

.id-text {
  font-size: 11px;
  color: #94A3B8;
  font-family: monospace;
}

.card-footer {
  border-top: 1px solid #F1F5F9;
  padding-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94A3B8;
}

.track-btn {
  margin: 0;
  padding: 6px 12px;
  background: #EFF6FF;
  color: #2563EB;
  font-size: 12px;
  font-weight: 600;
  border-radius: 100px;
  border: none;
  display: flex;
  align-items: center;
  gap: 4px;
  line-height: 1.5;

  &::after { border: none; }
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 40px 0;
  color: $text-sub;
  font-size: 13px;

  .empty-icon { margin-bottom: 12px; }
}

/* Tip Card */
.tip-card {
  background: linear-gradient(135deg, #FFF7ED 0%, #FFFFFF 100%);
  border: 1px solid #FFEDD5;
  border-radius: 16px;
  padding: 16px;
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.tip-icon {
  padding-top: 2px;
}

.tip-content {
  flex: 1;
}

.tip-title {
  font-size: 14px;
  font-weight: 700;
  color: #9A3412;
  margin-bottom: 4px;
  display: block;
}

.tip-desc {
  font-size: 12px;
  color: #C2410C;
  line-height: 1.4;
}

/* FAB Button */
.fab-wrapper {
  position: fixed;
  bottom: 90px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  z-index: 100;
  pointer-events: none; /* wrapper 不阻挡点击，button 阻挡 */
}

.fab-create {
  pointer-events: auto;
  background: linear-gradient(135deg, $primary 0%, #4F46E5 100%);
  color: #fff;
  border-radius: 100px;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 8px 20px rgba(79, 70, 229, 0.3);
  border: none;
  font-size: 15px;
  font-weight: 600;
  transition: transform 0.1s;

  &:active { transform: scale(0.95); }
  &::after { border: none; }
}

.popup-container {
  padding: 10px 0;
}

/* Animations */
.animate-enter {
  opacity: 0;
  transform: translateY(10px);
  animation: fadeUp 0.5s ease-out forwards;
  animation-delay: var(--delay);
}

.animate-slide-in {
  opacity: 0;
  transform: translateX(-10px);
  animation: slideIn 0.4s ease-out forwards;
}

.animate-bounce-in {
  animation: bounceIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideIn { to { opacity: 1; transform: translateX(0); } }
@keyframes bounceIn { from { transform: scale(0.8); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>