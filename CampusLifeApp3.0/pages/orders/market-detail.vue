<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar title="交易详情" :autoBack="true" bgColor="transparent" leftIconColor="#1E293B" titleStyle="color: #1E293B; font-weight: 700;" placeholder></u-navbar>

    <scroll-view v-if="order" scroll-y class="content">
      <view class="card status-card animate-enter" style="--delay: 0s">
        <view class="status-header">
          <view class="status-text">
            <h2 class="status-title">{{ statusTitle }}</h2>
            <p class="status-desc">{{ statusDesc }}</p>
          </view>
          <view class="status-icon-box" :class="{ completed: order.status === 'completed' }">
            <u-icon :name="order.status === 'completed' ? 'checkmark' : 'map-fill'"
                    color="#fff" size="28"></u-icon>
          </view>
        </view>

        <view class="progress-box">
          <view class="track-bg"></view>
          <view class="track-fill" :style="{ width: progressWidth }"></view>
          <view class="steps">
            <view v-for="(step, index) in steps" :key="index" class="step-node" :class="{ active: index <= currentStepIndex }">
              <view class="dot">
                <u-icon v-if="index <= currentStepIndex" name="checkmark" size="8" color="#fff"></u-icon>
              </view>
              <text class="label">{{ step.label }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="card code-card animate-enter" style="--delay: 0.1s" v-if="order.status !== 'completed' && order.status !== 'cancelled'">
        <view class="code-header">
          <text class="code-label">交易暗号</text>
          <u-icon name="eye" color="#94A3B8" size="16"></u-icon>
        </view>
        <view class="code-body">
          <text class="trade-code">{{ order.tradeCode }}</text>
          <text class="code-tip">面交时请出示此码或核对暗号</text>
        </view>
        <view class="tear-line"></view>
      </view>

      <view class="card seller-card animate-enter" style="--delay: 0.2s">
        <view class="seller-row">
          <u-avatar :src="counterparty.avatar || 'https://via.placeholder.com/100'" size="40" @click="goUserProfile"></u-avatar>
          <view class="seller-info" @click="goUserProfile">
            <text class="seller-name">{{ counterparty.name || '未知用户' }}</text>
            <text class="seller-tag">{{ counterparty.label }}</text>
          </view>
          <view class="profile-btn" @click="goUserProfile">
            <text>主页</text>
            <u-icon name="arrow-right" color="#6366F1" size="12"></u-icon>
          </view>
        </view>
      </view>

      <view class="card goods-card animate-enter" style="--delay: 0.3s">
        <text class="section-title">交易商品</text>
        <view class="goods-item">
          <u-image :src="order.itemImg" width="80px" height="80px" radius="8" mode="aspectFill" class="goods-img"></u-image>
          <view class="goods-info">
            <text class="goods-name u-line-2">{{ order.itemName }}</text>
            <view class="goods-meta">
              <text class="price"><text class="symbol">¥</text>{{ order.price }}</text>
              <text class="qty">x1</text>
            </view>
          </view>
        </view>

        <view class="divider"></view>

        <view class="bill-row total">
          <text class="label">实付款</text>
          <text class="val price"><text class="symbol">¥</text>{{ order.price }}</text>
        </view>
      </view>

      <view class="card info-card animate-enter" style="--delay: 0.4s">
        <view class="info-row">
          <text class="label">订单编号</text>
          <view class="val-box">
            <text class="val mono">{{ order.orderNo }}</text>
            <view class="copy-tag" @click.stop="copyId">复制</view>
          </view>
        </view>
        <view class="info-row">
          <text class="label">创建时间</text>
          <text class="val">{{ order.time }}</text>
        </view>
        <view class="info-row">
          <text class="label">交易方式</text>
          <text class="val">线下面交</text>
        </view>
      </view>

      <view style="height: 100px;"></view>
    </scroll-view>

    <view v-if="order" class="bottom-bar animate-slide-up">
      <view class="bar-inner">
        <view class="service-btn hover-scale" @click="contactCounterparty">
          <u-icon name="chat-fill" size="24" color="#6366F1"></u-icon>
          <text>联系</text>
        </view>

        <!-- 待支付: 可取消订单 -->
        <u-button
            v-if="isBuyer && order.status === 'pending'"
            type="error"
            plain
            shape="circle"
            customStyle="height: 44px; margin-left: 12px; border: 1px solid #FCA5A5; color: #EF4444; font-weight: 600; padding: 0 20px;"
            @click="handleCancelOrder"
        >
          取消订单
        </u-button>

        <!-- 已支付未发货: 买家可申请退款 -->
        <u-button
            v-if="isBuyer && order.status === 'paid'"
            type="warning"
            plain
            shape="circle"
            customStyle="height: 44px; margin-left: 12px; border: 1px solid #FCD34D; color: #D97706; font-weight: 600; padding: 0 20px;"
            @click="handleRefund"
        >
          申请退款
        </u-button>

        <!-- 卖家操作: 待确认 -> 确认交易 -->
        <u-button
            v-if="isSeller && order.status === 'paid'"
            type="primary"
            shape="circle"
            customStyle="flex: 1; height: 44px; margin-left: 12px; background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%); border: none; font-weight: 700;"
            @click="confirmShipment"
        >
          确认交易
        </u-button>

        <!-- 待自提: 买家可申请售后 -->
        <u-button
            v-if="isBuyer && order.status === 'shipping'"
            type="warning"
            plain
            shape="circle"
            customStyle="height: 44px; margin-left: 12px; border: 1px solid #FCD34D; color: #D97706; font-weight: 600; padding: 0 20px;"
            @click="handleAfterSale"
        >
          申请售后
        </u-button>

        <!-- 买家操作: 待自提 -> 确认收货 -->
        <u-button
            v-if="isBuyer && order.status === 'shipping'"
            type="primary"
            shape="circle"
            customStyle="flex: 1; height: 44px; margin-left: 12px; background: linear-gradient(135deg, #10B981 0%, #059669 100%); border: none; font-weight: 700; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);"
            @click="confirmReceipt"
        >
          确认收货
        </u-button>

        <!-- 已完成/已取消/其他状态 -->
        <u-button
            v-if="order.status === 'completed' || order.status === 'cancelled' || order.status === 'refunded'"
            type="info"
            plain
            shape="circle"
            customStyle="flex: 1; height: 44px; margin-left: 12px; border: 1px solid #CBD5E1; color: #64748B; font-weight: 700;"
            @click="goBack"
        >
          {{ order.status === 'completed' ? '交易已完成' : (order.status === 'refunded' ? '已退款' : '已取消') }}
        </u-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, updateOrderStatus, cancelOrder } from '@/api/orders.js'

const order = ref(null)
const userInfo = ref(uni.getStorageSync('userInfo') || {})

// 二手交易状态定义
const steps = [
  { key: 'paid', label: '已付款' },
  { key: 'shipping', label: '待自提' },
  { key: 'completed', label: '已完成' }
]

const isBuyer = computed(() => {
  return order.value && userInfo.value.id === order.value.buyerId
})

const isSeller = computed(() => {
  return order.value && userInfo.value.id === order.value.sellerId
})

const currentStepIndex = computed(() => {
  if (!order.value) return 0
  const idx = steps.findIndex(s => s.key === order.value.status)
  return idx >= 0 ? idx : (order.value.status === 'completed' ? 2 : 0)
})

const progressWidth = computed(() => {
  const percent = (currentStepIndex.value / (steps.length - 1)) * 100
  return `${percent}%`
})

const statusTitle = computed(() => {
  if (!order.value) return ''
  switch(order.value.status) {
    case 'pending': return '等待支付'
    case 'paid': return '等待卖家确认'
    case 'shipping': return '待自提'
    case 'completed': return '交易已完成'
    case 'cancelled': return '交易已取消'
    case 'refunded': return '已退款'
    default: return '未知状态'
  }
})

const statusDesc = computed(() => {
  if (!order.value) return ''
  switch(order.value.status) {
    case 'pending': return '请尽快完成支付'
    case 'paid': return '请等待卖家确认交易'
    case 'shipping': return '请联系卖家约定自提时间和地点'
    case 'completed': return '好物已归您所有'
    case 'cancelled': return '订单已取消'
    default: return ''
  }
})

onLoad(async (options) => {
  const id = options.id
  await loadOrderDetail(id)
})

async function loadOrderDetail(id) {
  try {
    const res = await getOrderDetail(id)
    if (res.code === 0 || res.success) {
      const data = res.data
      order.value = {
        ...data,
        id: data.id,
        status: data.status,
        tradeCode: data.id.toString().slice(-4), // 简单模拟暗号
        time: data.createTime,
        itemName: data.items && data.items.length > 0 ? data.items[0].itemName : '未知商品',
        price: data.totalAmount,
        itemImg: data.items && data.items.length > 0 ? data.items[0].itemImage : '',
        sellerName: data.sellerName || '未知卖家',
        sellerAvatar: data.sellerAvatar,
        buyerName: data.buyerName || '未知买家',
        buyerAvatar: data.buyerAvatar,
        buyerId: data.buyerId,
        sellerId: data.sellerId
      }
    }
  } catch (e) {
    console.error('加载订单详情失败', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

const counterparty = computed(() => {
  if (!order.value) return {}
  if (isSeller.value) {
    return {
      name: order.value.buyerName || '买家',
      avatar: order.value.buyerAvatar,
      id: order.value.buyerId,
      label: '买家'
    }
  } else {
    return {
      name: order.value.sellerName || '卖家',
      avatar: order.value.sellerAvatar,
      id: order.value.sellerId,
      label: '卖家'
    }
  }
})

function copyId() {
  uni.setClipboardData({ data: order.value.orderNo || order.value.id.toString() })
}

function contactCounterparty() {
  const target = counterparty.value
  uni.navigateTo({ url: `/pages/messages/chat?userId=${target.id}&name=${target.name}&avatar=${encodeURIComponent(target.avatar || '')}` })
}

function goUserProfile() {
  const target = counterparty.value
  uni.navigateTo({ url: `/pages/profile/user-detail?userId=${target.id}` })
}

// 卖家确认交易
function confirmShipment() {
  uni.showModal({
    title: '确认交易',
    content: '确认后订单将进入“待自提”状态，请联系买家约定自提时间和地点。',
    success: async (res) => {
      if (res.confirm) {
        try {
          await updateOrderStatus(order.value.id, 'shipping')
          order.value.status = 'shipping'
          uni.showToast({ title: '已确认交易', icon: 'success' })
        } catch (e) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}

// 买家确认收货
function confirmReceipt() {
  uni.showModal({
    title: '确认收货',
    content: '请确保您已收到商品并检查无误。确认收货后款项将打给卖家。',
    success: async (res) => {
      if (res.confirm) {
        try {
          await updateOrderStatus(order.value.id, 'completed')
          order.value.status = 'completed'
          uni.showToast({ title: '交易完成', icon: 'success' })
        } catch (e) {
          uni.showToast({ title: '操作失败', icon: 'none' })
        }
      }
    }
  })
}

function goBack() {
  uni.navigateBack()
}

// 取消订单（待支付状态）
function handleCancelOrder() {
  uni.showModal({
    title: '取消订单',
    content: '确定要取消此订单吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrder(order.value.id, '买家主动取消')
          order.value.status = 'cancelled'
          uni.showToast({ title: '订单已取消', icon: 'success' })
        } catch (e) {
          uni.showToast({ title: '取消失败', icon: 'none' })
        }
      }
    }
  })
}

// 申请退款（已支付未发货状态）
function handleRefund() {
  uni.showModal({
    title: '申请退款',
    content: '卖家尚未确认交易，您可以申请全额退款。确定要申请退款吗？',
    confirmText: '确认退款',
    success: async (res) => {
      if (res.confirm) {
        try {
          await cancelOrder(order.value.id, '买家申请退款')
          order.value.status = 'refunded'
          uni.showToast({ title: '退款成功，款项已原路退回', icon: 'success' })
        } catch (e) {
          uni.showToast({ title: '退款申请失败', icon: 'none' })
        }
      }
    }
  })
}

// 申请售后（待自提状态）
function handleAfterSale() {
  uni.showActionSheet({
    itemList: ['商品有问题，申请退款', '联系卖家协商', '联系平台客服'],
    success: async (res) => {
      if (res.tapIndex === 0) {
        uni.showModal({
          title: '申请售后退款',
          content: '请先与卖家沟通协商。如卖家同意，款项将全额退回。',
          confirmText: '申请退款',
          success: async (result) => {
            if (result.confirm) {
              try {
                await cancelOrder(order.value.id, '买家申请售后退款')
                order.value.status = 'refunded'
                uni.showToast({ title: '退款成功', icon: 'success' })
              } catch (e) {
                uni.showToast({ title: '请先联系卖家协商', icon: 'none' })
              }
            }
          }
        })
      } else if (res.tapIndex === 1) {
        contactCounterparty()
      } else if (res.tapIndex === 2) {
        uni.showToast({ title: '客服功能开发中', icon: 'none' })
      }
    }
  })
}
</script>

<style scoped lang="scss">
/* 变量 */
$primary: #6366F1;
$primary-dark: #4F46E5;
$success: #10B981;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page { background: $bg-page; min-height: 100vh; display: flex; flex-direction: column; }

.bg-decoration {
  position: absolute; top: -100px; left: -20%; width: 140%; height: 400px;
  background: radial-gradient(circle at 50% 20%, #E0E7FF 0%, rgba(248, 250, 252, 0) 70%);
  z-index: 0; pointer-events: none;
}

.content { flex: 1; padding: 16px 20px; position: relative; z-index: 1; }

/* 通用卡片 */
.card {
  background: #fff; border-radius: 20px; padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.06);
}

/* 1. 状态卡片 */
.status-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.status-title { font-size: 22px; font-weight: 800; color: $text-main; margin-bottom: 4px; }
.status-desc { font-size: 13px; color: $text-sub; }
.status-icon-box {
  width: 48px; height: 48px; border-radius: 16px;
  background: linear-gradient(135deg, $primary, $primary-dark);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 16px rgba(0,0,0,0.1);
  &.completed { background: #10B981; }
}

/* 进度条 */
.progress-box { position: relative; padding: 0 12px; margin-top: 10px; }
.track-bg { position: absolute; top: 7px; left: 0; right: 0; height: 4px; background: #F1F5F9; border-radius: 2px; }
.track-fill { position: absolute; top: 7px; left: 0; height: 4px; background: $primary; border-radius: 2px; transition: width 0.5s ease; }
.steps { display: flex; justify-content: space-between; position: relative; z-index: 1; }
.step-node {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  .dot {
    width: 18px; height: 18px; border-radius: 50%; background: #fff;
    border: 2px solid #CBD5E1; display: flex; align-items: center; justify-content: center;
    transition: all 0.3s;
  }
  .label { font-size: 11px; color: #94A3B8; font-weight: 500; }
  &.active {
    .dot { border-color: $primary; background: $primary; transform: scale(1.1); }
    .label { color: $text-main; font-weight: 700; }
  }
}

/* 2. 交易暗号卡片 */
.code-card { background: linear-gradient(135deg, #EEF2FF 0%, #F8FAFC 100%); border: 1px solid #E0E7FF; text-align: center; padding: 24px; position: relative; }
.code-header { display: flex; justify-content: center; align-items: center; gap: 6px; margin-bottom: 12px; }
.code-label { font-size: 13px; color: $text-sub; font-weight: 600; }
.trade-code { font-size: 36px; font-weight: 800; color: $primary; letter-spacing: 4px; font-family: monospace; display: block; margin-bottom: 8px; }
.code-tip { font-size: 11px; color: #94A3B8; }
.tear-line {
  position: absolute; bottom: 0; left: 0; right: 0; height: 4px;
  background-image: linear-gradient(to right, #CBD5E1 50%, transparent 50%);
  background-size: 12px 1px; background-repeat: repeat-x;
}

/* 3. 卖家卡片 */
.seller-row { display: flex; align-items: center; gap: 12px; }
.seller-info { flex: 1; display: flex; flex-direction: column; justify-content: center; }
.seller-name { font-size: 15px; font-weight: 700; color: $text-main; }
.seller-tag { font-size: 10px; color: #10B981; background: #ECFDF5; padding: 2px 6px; border-radius: 4px; width: fit-content; margin-top: 2px; }
.btn-group { display: flex; align-items: center; gap: 8px; }
.contact-btn { display: flex; align-items: center; gap: 4px; padding: 6px 12px; background: #EEF2FF; border-radius: 100px; text { font-size: 12px; color: $primary; font-weight: 600; } }
.profile-btn { display: flex; align-items: center; gap: 2px; padding: 6px 10px; background: #fff; border: 1px solid #E0E7FF; border-radius: 100px; text { font-size: 12px; color: $primary; font-weight: 600; } &:active { background: #F0F4FF; } }

/* 4. 商品明细 */
.section-title { font-size: 15px; font-weight: 700; color: $text-main; margin-bottom: 16px; display: block; }
.goods-item { display: flex; gap: 12px; margin-bottom: 16px; }
.goods-img { background: #F1F5F9; flex-shrink: 0; }
.goods-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.goods-name { font-size: 14px; font-weight: 600; color: $text-main; line-height: 1.4; }
.goods-meta { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 16px; font-weight: 700; color: $text-main; .symbol { font-size: 12px; } }
.qty { font-size: 12px; color: $text-sub; }

.divider { height: 1px; background: #F1F5F9; margin: 12px 0; }
.bill-row { display: flex; justify-content: space-between; align-items: center; .label { font-size: 14px; font-weight: 600; color: $text-main; } .val.price { font-size: 20px; font-weight: 800; color: $text-main; } }

/* 5. 订单信息 */
.info-row {
  display: flex; justify-content: space-between; align-items: center; padding: 12px 0;
  border-bottom: 1px solid #F1F5F9;
  &:last-child { border: none; padding-bottom: 0; }

  .label { font-size: 13px; color: $text-sub; }
  .val { font-size: 13px; color: $text-main; font-weight: 500; }
  .mono { font-family: monospace; letter-spacing: 0.5px; }
  .val-box { display: flex; align-items: center; gap: 8px; }
  .copy-tag { font-size: 10px; color: $text-sub; background: #F1F5F9; padding: 2px 6px; border-radius: 4px; }
}

/* 底部栏 */
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  padding: 12px 20px; padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background: #fff; box-shadow: 0 -4px 20px rgba(0,0,0,0.05);
  z-index: 100;
}
.bar-inner { display: flex; align-items: center; }
.service-btn {
  display: flex; flex-direction: column; align-items: center; gap: 2px; padding: 0 12px;
  text { font-size: 10px; color: $primary; font-weight: 600; }
}

/* 动画 */
.hover-scale:active { transform: scale(0.95); transition: 0.1s; }
.animate-enter { opacity: 0; transform: translateY(20px); animation: fadeInUp 0.5s forwards; animation-delay: var(--delay); }
.animate-slide-up { transform: translateY(100%); animation: slideUp 0.5s 0.2s forwards; }
@keyframes fadeInUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideUp { to { transform: translateY(0); } }
</style>