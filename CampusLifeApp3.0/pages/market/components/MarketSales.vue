<template>
  <scroll-view scroll-y class="scroll-content">
    <view v-if="list.length > 0" class="list-container">
      <view
          v-for="(order, index) in list"
          :key="order.id"
          class="order-card animate-enter"
          :style="{ '--delay': index * 0.05 + 's' }"
          @click="goOrderDetail(order)"
      >
        <view class="card-header">
          <text class="time">{{ formatTime(order.timestamp) }}</text>
          <text class="status">交易成功</text>
        </view>
        
        <view class="card-body">
          <image :src="order.image" mode="aspectFill" class="product-img"></image>
          <view class="item-info-simple">
            <text class="item-name u-line-1">{{ order.item }}</text>
            <text class="buyer-info">买家: {{ order.buyer || '匿名用户' }}</text>
          </view>
          <view class="amount">
            <text class="currency">¥</text>
            <text class="val">{{ order.amount.toFixed(2) }}</text>
          </view>
        </view>
        
        <view class="card-footer">
          <view class="action-btn" @click.stop="contactUser(order.buyer)">
            <u-icon name="chat" size="14" color="#6366F1"></u-icon>
            <text>联系买家</text>
          </view>
        </view>
      </view>
      <view style="height: 40px"></view>
    </view>
    <view v-else class="empty-state">
      <u-empty mode="order" text="暂无销售记录" icon-size="100" marginTop="100"></u-empty>
    </view>
  </scroll-view>
</template>

<script setup>
const props = defineProps({
  list: {
    type: Array,
    default: () => []
  }
})

function formatTime(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getMonth()+1}-${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2,'0')}`
}

function goOrderDetail(order) {
  uni.navigateTo({ url: `/pages/orders/market-detail?id=${order.id}` })
}

function contactUser(name) {
  if (!name) return
  uni.navigateTo({ url: `/pages/messages/chat?name=${encodeURIComponent(name)}` })
}
</script>

<style scoped lang="scss">
$primary: #6366F1;
$text-main: #1E293B;
$text-sub: #64748B;

.scroll-content { height: 100%; }
.list-container { padding: 16px 20px; display: flex; flex-direction: column; gap: 16px; }

.order-card {
  background: #fff; border-radius: 20px; padding: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  border: 1px solid #F1F5F9;
  transition: all 0.2s;
  
  &:active { transform: scale(0.98); }
}

.card-header { 
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 16px; 
  padding-bottom: 12px;
  border-bottom: 1px dashed #F1F5F9;
  
  .time { font-size: 12px; color: $text-sub; } 
  .status { 
    font-size: 12px; color: #10B981; font-weight: 600; 
    background: #ECFDF5; padding: 4px 10px; border-radius: 100px;
  } 
}

.card-body { 
  display: flex; justify-content: space-between; align-items: center; 
  margin-bottom: 16px; 
}

.item-info-simple { 
  display: flex; flex-direction: column; gap: 6px; flex: 1; margin-left: 12px;
  .item-name { font-size: 16px; font-weight: 700; color: $text-main; } 
  .buyer-info { font-size: 13px; color: $text-sub; } 
}

.product-img {
  width: 60px; height: 60px; border-radius: 8px; background: #F1F5F9;
}

.amount { 
  color: $text-main; font-weight: 800; 
  .currency { font-size: 14px; margin-right: 2px; } 
  .val { font-size: 24px; } 
}

.card-footer { 
  display: flex; justify-content: flex-end; 
}

.action-btn {
  display: flex; align-items: center; gap: 6px; 
  font-size: 13px; font-weight: 600; color: $primary; 
  padding: 8px 16px; background: #EEF2FF; border-radius: 100px;
  &:active { opacity: 0.8; }
}

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; }
.animate-enter { opacity: 0; transform: translateY(20px); animation: fadeUp 0.5s forwards; animation-delay: var(--delay); }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
</style>
