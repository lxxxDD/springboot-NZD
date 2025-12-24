<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar title="订单详情" :autoBack="false" @leftClick="goBack" bgColor="transparent" leftIconColor="#1E293B" titleStyle="color: #1E293B; font-weight: 600;" placeholder></u-navbar>

    <scroll-view v-if="order" scroll-y class="content">
      <view class="card status-card animate-enter" style="--delay: 0s">
        <view class="status-header">
          <view class="status-text">
            <h2 class="status-title">{{ order.status === 'completed' ? '订单已完成' : '商家制作中' }}</h2>
            <p class="status-desc">{{ order.status === 'completed' ? '希望您用餐愉快' : '预计 10-15 分钟内出餐' }}</p>
          </view>
          <view class="status-icon-box" :class="{ completed: order.status === 'completed' }">
            <u-icon :name="order.status === 'completed' ? 'checkmark' : 'hourglass'"
                    color="#fff" size="32"></u-icon>
          </view>
        </view>

        <view class="progress-container">
          <view class="progress-track">
            <view class="progress-fill" :class="{ completed: order.status === 'completed' }"></view>
          </view>
          <view class="steps-row">
            <view v-for="(step, index) in steps" :key="index" class="step-item">
              <view class="step-circle" :class="{ active: step.active, current: isCurrentStep(index) }">
                <view class="inner-dot"></view>
              </view>
              <text class="step-text" :class="{ active: step.active }">{{ step.label }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="card pickup-card animate-enter" style="--delay: 0.1s">
        <view class="pickup-header">
          <text class="pickup-label">取餐号码</text>
          <view class="pickup-status">
            <text class="status-dot"></text> 请凭码取餐
          </view>
        </view>
        <view class="pickup-body">
          <text class="pickup-code">{{ order.pickupCode || 'A-102' }}</text>
          <view class="pickup-qr">
            <u-qrcode
                :val="order.id || '123456'"
                size="48"
                unit="px"
                background="transparent"
                foreground="#1E293B"
            ></u-qrcode>
          </view>
        </view>
      </view>

      <view class="card detail-card animate-enter" style="--delay: 0.2s">
        <view class="card-header">
          <text class="card-heading">商品明细</text>
          <text class="store-name">CampusPal 优选商家</text>
        </view>

        <view v-for="(item, index) in orderItems" :key="index" class="item-row">
          <image :src="item.foodImage || '/static/default-food.png'" class="item-img" mode="aspectFill"></image>
          <view class="item-info">
            <view class="info-main">
              <h5 class="item-name">{{ item.foodName }}</h5>
            </view>
            <view class="info-side">
              <span class="item-price">
                <text class="currency">¥</text>{{ item.price?.toFixed(2) || '0.00' }}
              </span>
              <span class="item-qty">x{{ item.quantity }}</span>
            </view>
          </view>
        </view>

        <view class="divider-dashed"></view>

        <view class="cost-summary">
          <view class="cost-row">
            <span>打包费</span>
            <span>¥0.00</span>
          </view>
          <view class="cost-row">
            <span>优惠券</span>
            <span class="discount">- ¥0.00</span>
          </view>
          <view class="cost-row total">
            <span class="total-label">实付金额</span>
            <span class="total-price">
              <text class="currency">¥</text>{{ order?.totalAmount?.toFixed(2) || '0.00' }}
            </span>
          </view>
        </view>
      </view>

      <view class="card info-card animate-enter" style="--delay: 0.3s">
        <view class="info-item">
          <span class="label">订单编号</span>
          <view class="value-box">
            <span class="mono">{{ order.orderNo }}</span>
            <text class="copy-btn" @click.stop>复制</text>
          </view>
        </view>
        <view class="info-item">
          <span class="label">下单时间</span>
          <span class="value">{{ order?.createTime || '刚刚' }}</span>
        </view>
        <view class="info-item">
          <span class="label">支付方式</span>
          <span class="value">校园钱包</span>
        </view>
      </view>

      <view style="height: 100px;"></view>
    </scroll-view>

    <view v-if="order" class="bottom-bar-wrapper animate-slide-up">
      <view class="bottom-bar">
        <view class="icon-btn" @click="showHelp">
          <u-icon name="kefu-ermai" size="24" color="#475569"></u-icon>
          <text>客服</text>
        </view>
        <button class="action-btn primary" @click="reorder">
          再来一单
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getFoodOrderDetail } from '@/api/canteen.js';
import { addItem } from '@/store/cart.js';

const order = ref(null);
const orderItems = ref([]);

onLoad(async (options) => {
  if (!options.id) {
    uni.showToast({ title: '订单ID不存在', icon: 'none' });
    return;
  }
  
  try {
    uni.showLoading({ title: '加载中...' });
    const res = await getFoodOrderDetail(options.id);
    uni.hideLoading();
    
    if (res.code === 200 && res.data) {
      order.value = res.data;
      orderItems.value = res.data.items || [];
    } else {
      uni.showToast({ title: res.message || '获取订单详情失败', icon: 'none' });
    }
  } catch (error) {
    uni.hideLoading();
    console.error('获取订单详情失败:', error);
    uni.showToast({ title: '获取订单详情失败', icon: 'none' });
  }
});

const steps = computed(() => {
  const status = order.value?.status;
  return [
    { label: '已下单', active: true },
    { label: '制作中', active: status === 'paid' || status === 'completed' },
    { label: '请取餐', active: status === 'completed' },
    { label: '已完成', active: status === 'completed' }
  ];
});

const isCurrentStep = (index) => {
  const activeSteps = steps.value.filter(s => s.active);
  return index === activeSteps.length - 1;
};

function goBack() {
  uni.reLaunch({ url: '/pages/services/food' });
}

function showHelp() {
  uni.showToast({ title: '客服正忙，请稍后再试', icon: 'none' });
}

function reorder() {
  if (orderItems.value && orderItems.value.length > 0) {
    // 将所有订单商品重新添加到购物车
    orderItems.value.forEach(item => {
      const cartItem = {
        id: item.foodItemId,
        name: item.foodName,
        price: item.price,
        img: item.foodImage,
        qty: item.quantity
      };
      addItem(cartItem);
    });
    uni.reLaunch({ url: '/pages/services/food?showCart=true' });
  }
}
</script>

<style scoped lang="scss">
$primary: #6366F1;
$primary-dark: #4F46E5;
$success: #10B981;
$text-main: #1E293B;
$text-sub: #64748B;
$bg-page: #F8FAFC;
$card-radius: 24px;

.page {
  background: $bg-page;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: -100px; left: -20%;
  width: 140%; height: 400px;
  background: radial-gradient(circle at 50% 20%, #E0E7FF 0%, rgba(248, 250, 252, 0) 70%);
  z-index: 0; pointer-events: none;
}

.content {
  flex: 1; padding: 12px 20px;
  position: relative; z-index: 1;
}

/* 通用卡片 - 保持原样式 */
.card {
  background: #FFFFFF;
  border-radius: $card-radius;
  padding: 24px;
  margin-bottom: 16px;
  box-shadow: 0 4px 20px rgba(148, 163, 184, 0.06);
}

/* --- 2. 取餐码卡片 (简约风格) --- */
.pickup-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.pickup-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  border-bottom: 1px dashed #F1F5F9;
  padding-bottom: 12px;
}

.pickup-label { font-size: 13px; color: $text-sub; font-weight: 500; }
.pickup-status { font-size: 11px; color: $success; display: flex; align-items: center; gap: 4px; background: #ECFDF5; padding: 2px 8px; border-radius: 100px; }
.status-dot { width: 6px; height: 6px; background: $success; border-radius: 50%; }

.pickup-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.pickup-code {
  font-size: 48px;
  font-weight: 800;
  color: $text-main;
  font-family: 'DIN Alternate', sans-serif;
  letter-spacing: 2px;
}

.pickup-qr {
  padding: 4px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #F1F5F9;
}

/* --- 1. 状态卡片 (原样式) --- */
.status-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.status-title { font-size: 22px; font-weight: 800; color: $text-main; margin-bottom: 4px; }
.status-desc { font-size: 13px; color: $text-sub; }
.status-icon-box {
  width: 52px; height: 52px; border-radius: 16px;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 16px rgba(79, 70, 229, 0.25);
  &.completed { background: #10B981; box-shadow: 0 8px 16px rgba(16, 185, 129, 0.25); }
}

/* 进度条 */
.progress-container { position: relative; padding: 0 10px; }
.progress-track { position: absolute; top: 9px; left: 10%; right: 10%; height: 2px; background: #F1F5F9; z-index: 0; }
.progress-fill { height: 100%; width: 60%; background: $primary; border-radius: 2px; &.completed { width: 100%; background: $success; } }
.steps-row { display: flex; justify-content: space-between; position: relative; z-index: 1; }
.step-item { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.step-circle {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid #cbd5e1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &.active {
    border-color: $primary;
    background: #fff;

    .inner-dot {
      width: 10px;
      height: 10px;
      background: $primary;
      border-radius: 50%;
    }
  }

  &.current {
    transform: scale(1.2);
    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15);
    border-color: $primary;
    background: $primary;

    .inner-dot {
      background: #fff;
    }
  }
}
.step-text { font-size: 11px; color: #94A3B8; font-weight: 500; &.active { color: $text-main; font-weight: 700; } }

/* --- 3. 商品明细 (原样式补充) --- */
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.card-heading { font-size: 16px; font-weight: 700; color: $text-main; }
.store-name { font-size: 12px; color: $text-sub; }
.item-row { display: flex; gap: 16px; }
.item-img { width: 72px; height: 72px; border-radius: 12px; background: #F1F5F9; }
.item-info { flex: 1; display: flex; justify-content: space-between; }
.info-main { display: flex; flex-direction: column; gap: 6px; }
.item-name { font-size: 15px; font-weight: 600; color: $text-main; }
.tag { font-size: 10px; background: #F1F5F9; color: $text-sub; padding: 2px 6px; border-radius: 4px; }
.info-side { text-align: right; }
.item-price { display: block; font-size: 16px; font-weight: 700; color: $text-main; .currency { font-size: 12px; } }
.item-qty { font-size: 12px; color: $text-sub; margin-top: 4px; display: block; }
.divider-dashed { margin: 20px 0; height: 1px; background-image: linear-gradient(to right, #E2E8F0 50%, transparent 50%); background-size: 8px 1px; background-repeat: repeat-x; }
.cost-summary { display: flex; flex-direction: column; gap: 12px; }
.cost-row { display: flex; justify-content: space-between; font-size: 13px; color: $text-sub; &.total { align-items: flex-end; margin-top: 4px; } }
.total-label { font-size: 14px; font-weight: 700; color: $text-main; }
.total-price { font-size: 20px; font-weight: 800; color: $text-main; line-height: 1; .currency { font-size: 14px; } }
.discount { color: #F59E0B; font-weight: 600; }

/* --- 4. 订单信息 (原样式) --- */
.info-card { padding: 8px 24px; }
.info-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 0; border-bottom: 1px solid #F1F5F9; &:last-child { border: none; } }
.info-item .label { font-size: 13px; color: $text-sub; }
.info-item .value-box { display: flex; align-items: center; gap: 8px; }
.info-item .value, .mono { font-size: 13px; color: $text-main; font-weight: 500; }
.mono { font-family: monospace; letter-spacing: 0.5px; }
.copy-btn { font-size: 10px; background: #F1F5F9; color: $text-sub; padding: 2px 6px; border-radius: 4px; }

/* --- 底部栏悬浮 (原样式) --- */
.bottom-bar-wrapper { position: fixed; bottom: 24px; left: 20px; right: 20px; z-index: 100; }
.bottom-bar { background: rgba(255, 255, 255, 0.9); backdrop-filter: blur(12px); border-radius: 100px; padding: 8px 8px 8px 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08); border: 1px solid rgba(255,255,255,0.5); }
.icon-btn { display: flex; flex-direction: column; align-items: center; gap: 2px; margin-right: 16px; text { font-size: 10px; color: #475569; font-weight: 600; } }
.action-btn { flex: 1; border: none; border-radius: 100px; height: 44px; display: flex; align-items: center; justify-content: center; font-size: 15px; font-weight: 600; &.primary { background: $text-main; color: #fff; box-shadow: 0 4px 12px rgba(30,41,59,0.2); &:active { transform: scale(0.98); } } }

/* 动画 */
.animate-enter { opacity: 0; transform: translateY(20px); animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards; animation-delay: var(--delay); }
.animate-slide-up { animation: slideUpBar 0.6s cubic-bezier(0.16, 1, 0.3, 1) 0.4s forwards; transform: translateY(100px); opacity: 0; }
@keyframes fadeInUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideUpBar { to { transform: translateY(0); opacity: 1; } }
</style>