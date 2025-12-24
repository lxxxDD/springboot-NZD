<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar title="收银台" :autoBack="false" bgColor="transparent" titleStyle="color: #1E293B; font-weight: 700;" placeholder>
      <template #left>
        <view class="nav-back" @click="goBack">
          <text class="material-symbols-outlined" style="color: #1E293B; font-size: 20px;">arrow_back_ios</text>
        </view>
      </template>
    </u-navbar>

    <scroll-view scroll-y class="content">
      <view class="header-card animate-enter" style="--delay: 0s">
        <view class="countdown-box">
          <text class="label">支付剩余时间</text>
          <u-count-down :time="15 * 60 * 1000" format="mm:ss" @finish="onTimeout"></u-count-down>
        </view>

        <view class="amount-box">
          <text class="currency">¥</text>
          <text class="amount">{{ orderAmount.toFixed(2) }}</text>
        </view>

        <view class="order-brief">
          <text>{{ itemCount }} 件商品</text>
          <view class="divider"></view>
          <text>订单号 {{ orderId }}</text>
        </view>

        <view class="ticket-edge"></view>
      </view>

      <view class="section-title animate-enter" style="--delay: 0.1s">选择支付方式</view>

      <view class="methods-list animate-enter" style="--delay: 0.2s">
        <view
            class="method-item"
            :class="{ active: selectedMethod === 'wallet' }"
            @click="selectedMethod = 'wallet'"
        >
          <view class="left">
            <view class="icon-box wallet">
              <text class="material-symbols-outlined" style="color: #fff; font-size: 24px;">account_balance_wallet</text>
            </view>
            <view class="info">
              <text class="name">校园钱包</text>
              <text class="desc">余额: ¥{{ userBalance.toFixed(2) }}</text>
            </view>
          </view>
          <view class="radio-circle" :class="{ checked: selectedMethod === 'wallet' }">
            <view class="inner-dot" v-if="selectedMethod === 'wallet'"></view>
          </view>
        </view>

        <view
            class="method-item"
            :class="{ active: selectedMethod === 'wechat' }"
            @click="selectedMethod = 'wechat'"
        >
          <view class="left">
            <view class="icon-box wechat">
              <u-icon name="weixin-fill" color="#fff" size="24"></u-icon>
            </view>
            <view class="info">
              <text class="name">微信支付</text>
              <text class="desc">推荐使用</text>
            </view>
          </view>
          <view class="radio-circle" :class="{ checked: selectedMethod === 'wechat' }">
            <view class="inner-dot" v-if="selectedMethod === 'wechat'"></view>
          </view>
        </view>

        <view
            class="method-item"
            :class="{ active: selectedMethod === 'alipay' }"
            @click="selectedMethod = 'alipay'"
        >
          <view class="left">
            <view class="icon-box alipay">
              <u-icon name="zhifubao" color="#fff" size="24"></u-icon>
            </view>
            <view class="info">
              <text class="name">支付宝</text>
              <text class="desc">数亿用户的选择</text>
            </view>
          </view>
          <view class="radio-circle" :class="{ checked: selectedMethod === 'alipay' }">
            <view class="inner-dot" v-if="selectedMethod === 'alipay'"></view>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="bottom-bar animate-slide-up">
      <u-button
          type="primary"
          shape="circle"
          customStyle="height: 50px; width: 100%; background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%); border: none; font-size: 16px; font-weight: 700; box-shadow: 0 8px 20px rgba(79, 70, 229, 0.3);"
          :loading="loading"
          @click="handlePay"
      >
        <view class="btn-content">
          <text class="material-symbols-outlined" style="color: #fff; font-size: 20px; margin-right: 6px; opacity: 0.9;">lock</text>
          <text>安全支付 ¥{{ orderAmount.toFixed(2) }}</text>
        </view>
      </u-button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { createOrder, payOrder } from '@/api/orders.js'
import { createFoodOrder, payFoodOrder } from '@/api/canteen.js'
import { getBalance } from '@/api/user.js'
import { clearCart, getCart, getCartCanteen } from '@/store/cart.js'

// 响应式数据
const orderId = ref('')
const orderAmount = ref(25.50)
const itemCount = ref(3)
const userBalance = ref(0)
const selectedMethod = ref('wallet')
const loading = ref(false)
const orderType = ref('food') // 订单类型：food 或 market
const itemId = ref('') // 商品ID
const itemName = ref('') // 商品名称
const fromCart = ref(false) // 是否从购物车来的
const seller = ref('') // 卖家信息
const createdOrderId = ref(null) // 已创建的订单ID

onLoad(async (options) => {
  if (options.amount) orderAmount.value = parseFloat(options.amount)
  if (options.items) itemCount.value = parseInt(options.items)
  if (options.count) itemCount.value = parseInt(options.count)
  if (options.type) orderType.value = options.type
  if (options.itemId) itemId.value = options.itemId
  if (options.itemName) itemName.value = decodeURIComponent(options.itemName)
  if (options.seller) seller.value = decodeURIComponent(options.seller)
  if (options.from === 'cart') fromCart.value = true
  
  // 获取用户余额
  try {
    const res = await getBalance()
    userBalance.value = res.data.balance || 0
  } catch (err) {
    console.error('获取余额失败:', err)
    const userInfo = uni.getStorageSync('userInfo') || {}
    userBalance.value = userInfo.balance || 0
  }
  
  // 生成临时订单号
  orderId.value = 'ORD' + Date.now().toString().slice(-10)
})

function goBack() {
  uni.navigateBack()
}

function onTimeout() {
  uni.showToast({ title: '支付超时，请重新下单', icon: 'none' })
}

async function handlePay() {
  if (!selectedMethod.value) {
    uni.showToast({ title: '请选择支付方式', icon: 'none' })
    return
  }

  // 检查余额
  if (selectedMethod.value === 'wallet' && userBalance.value < orderAmount.value) {
    uni.showToast({ title: '余额不足，请充值', icon: 'none' })
    return
  }

  loading.value = true
  uni.showLoading({ title: '创建订单中...' })

  try {
    // 食堂订单特殊处理
    if (orderType.value === 'food' && fromCart.value) {
      // 获取购物车和食堂信息
      const cartItems = getCart()
      const canteen = getCartCanteen()
      
      if (!canteen || !canteen.id) {
        uni.showToast({ title: '请先选择食堂', icon: 'none' })
        loading.value = false
        uni.hideLoading()
        return
      }
      
      // 构建食堂订单数据
      const foodOrderData = {
        canteenId: canteen.id,
        items: cartItems.map(item => ({
          foodItemId: item.id,
          quantity: item.qty || 1
        })),
        remark: ''
      }
      
      // 1. 创建食堂订单
      const orderRes = await createFoodOrder(foodOrderData)
      if (orderRes.code !== 200) {
        throw new Error(orderRes.message || '创建订单失败')
      }
      
      createdOrderId.value = orderRes.data.id
      
      uni.showLoading({ title: '支付中...' })
      
      // 2. 支付食堂订单
      const paymentMethod = selectedMethod.value === 'wallet' ? 'balance' : selectedMethod.value
      await payFoodOrder(createdOrderId.value, paymentMethod)
      
      // 清空购物车
      clearCart()
      
      uni.hideLoading()
      uni.showToast({
        title: '支付成功',
        icon: 'success',
        duration: 2000
      })
      
      // 跳转到食堂订单详情
      setTimeout(() => {
        uni.redirectTo({
          url: `/pages/orders/food-detail?id=${createdOrderId.value}`
        })
      }, 2000)
      
    } else {
      // 其他类型订单（二手市场等）
      const orderRes = await createOrder({
        itemId: itemId.value,
        type: orderType.value,
        amount: orderAmount.value
      })
      
      createdOrderId.value = orderRes.data.id
      
      uni.showLoading({ title: '支付中...' })
      
      await payOrder(createdOrderId.value, selectedMethod.value)
      
      if (fromCart.value) {
        clearCart()
      }

      uni.hideLoading()
      uni.showToast({
        title: '支付成功',
        icon: 'success',
        duration: 2000
      })

      setTimeout(() => {
        const detailUrl = orderType.value === 'market'
          ? `/pages/orders/market-detail?id=${createdOrderId.value}`
          : `/pages/orders/food-detail?id=${createdOrderId.value}`;

        uni.redirectTo({
          url: detailUrl
        })
      }, 2000)
    }
  } catch (err) {
    console.error('支付失败:', err)
    uni.hideLoading()
    uni.showToast({ title: err.message || '支付失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
/* 变量 */
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

/* Material Symbols 字体设置 */
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;
  display: inline-block;
  line-height: 1;
  text-transform: none;
  letter-spacing: normal;
  word-wrap: normal;
  white-space: nowrap;
  direction: ltr;
}

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: -120px; right: -80px;
  width: 350px; height: 350px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.nav-back {
  padding: 4px 8px;
}

.content {
  padding: 16px 20px;
  height: calc(100vh - 44px - 90px);
}

/* 头部卡片 */
.header-card {
  background: #fff;
  border-radius: 24px;
  padding: 32px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  box-shadow: 0 8px 24px rgba(148, 163, 184, 0.08);
  margin-bottom: 32px;
  overflow: hidden;
}

.ticket-edge {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 12px;
  background-image: radial-gradient(circle, $bg-page 50%, transparent 50%);
  background-size: 24px 24px;
  background-position: -12px 6px;
  background-repeat: repeat-x;
}

.countdown-box {
  display: flex; align-items: center; gap: 8px;
  margin-bottom: 16px;

  .label { font-size: 12px; color: $text-sub; }
  ::v-deep .u-count-down__text { color: $primary !important; font-weight: 600; font-size: 14px; }
}

.amount-box {
  display: flex; align-items: baseline;
  color: $text-main;
  margin-bottom: 16px;

  .currency { font-size: 24px; font-weight: 700; margin-right: 4px; }
  .amount { font-size: 48px; font-weight: 800; letter-spacing: -1px; line-height: 1; }
}

.order-brief {
  display: flex; align-items: center; gap: 12px;
  font-size: 13px; color: $text-sub;

  .divider { width: 1px; height: 12px; background: #E2E8F0; }
}

/* 支付方式 */
.section-title {
  font-size: 15px; font-weight: 700; color: $text-main;
  margin-bottom: 16px; padding-left: 4px;
}

.methods-list {
  display: flex; flex-direction: column; gap: 12px;
}

.method-item {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  display: flex; align-items: center; justify-content: space-between;
  border: 2px solid transparent;
  transition: all 0.2s ease;

  &.active {
    border-color: $primary;
    background: #EEF2FF;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.1);
  }

  &:active { transform: scale(0.98); }
}

.left {
  display: flex; align-items: center; gap: 16px;
}

.icon-box {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;

  &.wallet { background: linear-gradient(135deg, #6366F1, #4F46E5); }
  &.wechat { background: #07C160; }
  &.alipay { background: #1677FF; }
}

.info {
  display: flex; flex-direction: column; gap: 2px;

  .name { font-size: 15px; font-weight: 600; color: $text-main; }
  .desc { font-size: 12px; color: $text-sub; }
}

.radio-circle {
  width: 20px; height: 20px;
  border-radius: 50%;
  border: 2px solid #CBD5E1;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;

  &.checked {
    border-color: $primary;
    background: $primary;
  }

  .inner-dot {
    width: 8px; height: 8px;
    background: #fff;
    border-radius: 50%;
  }
}

/* 底部栏 */
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff;
  padding: 16px 24px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  box-shadow: 0 -4px 20px rgba(0,0,0,0.05);
  z-index: 100;
}

.btn-content {
  display: flex; align-items: center; justify-content: center; width: 100%;
}

/* 动画 */
.animate-enter { opacity: 0; transform: translateY(15px); animation: fadeUp 0.5s forwards; animation-delay: var(--delay); }
.animate-slide-up { transform: translateY(100%); animation: slideUp 0.5s 0.2s forwards; }

@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideUp { to { transform: translateY(0); } }
</style>