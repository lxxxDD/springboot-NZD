<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar title="我的订单" :autoBack="true" bgColor="#F8FAFC" leftIconColor="#1e293b" titleStyle="color: #1e293b; font-weight: 600;" placeholder></u-navbar>

    <view class="search-box animate-enter" style="--delay: 0s">
      <u-search 
        v-model="keyword" 
        placeholder="搜索订单号或商品名称" 
        :showAction="false" 
        bgColor="#fff" 
        shape="round"
        :clearabled="true"
      ></u-search>
    </view>

    <view class="tabs-box animate-enter" style="--delay: 0.1s">
      <u-tabs 
        :list="tabList" 
        :current="currentTab" 
        @change="changeTab"
        lineColor="#6366F1"
        activeStyle="color: #6366F1; font-weight: 700;"
        inactiveStyle="color: #64748B;"
      ></u-tabs>
    </view>

    <scroll-view scroll-y class="content" @scrolltolower="loadMore">
      <view v-if="displayOrders.length > 0" class="order-list">
        <view
            v-for="(order, index) in displayOrders"
            :key="order.id"
            class="card animate-enter"
            :style="{ '--delay': (index % pageSize) * 0.1 + 's' }"
            @click="showDetail(order)"
        >
          <!-- ... card content ... -->
          <view class="card-header">
            <view class="header-main">
              <view class="icon-box" :class="order.type">
                <view v-if="order.type === 'food'" class="svg-icon">
                  <svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M16 5C11.5817 5 8 8.58172 8 13H24C24 8.58172 20.4183 5 16 5Z" fill="#FCD34D" stroke="#D97706" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M8 13H24V15C24 16.1046 23.1046 17 22 17H10C8.89543 17 8 16.1046 8 15V13Z" fill="#10B981" stroke="#059669" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M8 17H24V19C24 20.1046 23.1046 21 22 21H10C8.89543 21 8 20.1046 8 19V17Z" fill="#EF4444" stroke="#B91C1C" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M9 21H23C23.5523 21 24 21.4477 24 22V23C24 24.1046 23.1046 25 22 25H10C8.89543 25 8 24.1046 8 23V22C8 21.4477 8.44772 21 9 21Z" fill="#FCD34D" stroke="#D97706" stroke-width="2" stroke-linejoin="round"/>
                    <circle cx="13" cy="9" r="1" fill="#D97706"/>
                    <circle cx="16" cy="8" r="1" fill="#D97706"/>
                    <circle cx="19" cy="9" r="1" fill="#D97706"/>
                  </svg>
                </view>
                <view v-else class="svg-icon">
                  <svg width="30" height="30" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M16 4L4 10L16 16L28 10L16 4Z" fill="#818CF8" stroke="#4F46E5" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M4 10V22L16 28V16" fill="#C7D2FE" stroke="#4F46E5" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M28 10V22L16 28V16" fill="#A5B4FC" stroke="#4F46E5" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M16 4V16" stroke="#4F46E5" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M16 16L28 10" stroke="#4F46E5" stroke-width="2" stroke-linejoin="round"/>
                    <path d="M16 16L4 10" stroke="#4F46E5" stroke-width="2" stroke-linejoin="round"/>
                  </svg>
                </view>
              </view>
              <view class="header-info">
                <h4 class="item-title">{{ order.item }}</h4>
                <text class="time">{{ order.time }}</text>
              </view>
            </view>
            <view class="status-tag" :class="getStatusClass(order.status)">
              <view v-if="order.status !== 'Completed' && order.status !== 'Cancelled' && order.status !== 'Refunded'" class="pulse-dot"></view>
              {{ getStatusText(order.status) }}
            </view>
          </view>

          <view class="divider"></view>

          <view class="card-footer">
            <view class="id-group">
              <text class="label">订单号</text>
              <text class="mono">{{ order.id }}</text>
            </view>
            <view class="action-btn">
              <text>详情</text>
              <u-icon name="arrow-right" size="12" color="#6366F1"></u-icon>
            </view>
          </view>
        </view>
        
        <!-- 加载更多组件 -->
        <u-loadmore :status="status" :loading-text="'努力加载中...'" :nomore-text="'没有更多订单了'" line />
      </view>

      <view v-else class="empty-state">
        <image src="/static/empty-order.png" mode="widthFix" class="empty-img" />
        <text class="empty-text">暂无历史订单</text>
        <text class="empty-sub">快去探索校园美食吧</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { getOrders } from '@/api/orders.js';
import { getFoodOrderList } from '@/api/canteen.js';
import { ref, watch } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const keyword = ref('');

// Tab配置
const tabList = [{ name: '全部' }, { name: '餐饮' }, { name: '二手' }];
const currentTab = ref(0);
const typeMap = { 0: '', 1: 'food', 2: 'market' };

// 分页相关状态
const displayOrders = ref([]);
const status = ref('loadmore');
const page = ref(1);
const pageSize = 10;
const hasMore = ref(true);

// 页面显示时刷新订单数据
onShow(() => {
  reload();
});

function changeTab(item) {
  currentTab.value = item.index;
  reload();
}

// 监听关键词变化，重置分页
watch([keyword], () => {
  reload();
});

function reload() {
  page.value = 1;
  displayOrders.value = [];
  hasMore.value = true;
  status.value = 'loadmore';
  loadMore();
}

async function loadMore() {
  if (status.value === 'nomore' || status.value === 'loading') return;
  
  status.value = 'loading';
  
  try {
    let list = [];
    
    if (currentTab.value === 1) {
      // 只获取食堂订单
      const res = await getFoodOrderList({
        page: page.value,
        size: pageSize
      });
      
      if (res.code === 200) {
        list = res.data.list.map(order => ({
          id: order.orderNo,
          orderId: order.id,
          item: order.items && order.items.length > 0 ? order.items[0].foodName : '食堂订单',
          type: 'food',
          status: order.status === 'completed' ? 'Completed' : 'Processing',
          time: formatTime(order.createTime),
          amount: order.totalAmount
        }));
        
        if (!res.data.hasMore || displayOrders.value.length + list.length >= res.data.total) {
          status.value = 'nomore';
          hasMore.value = false;
        }
      }
      
    } else if (currentTab.value === 2) {
      // 只获取二手商品订单
      const res = await getOrders({
        page: page.value,
        size: pageSize,
        type: 'market'
      });
      
      if (res.code === 200 && res.data) {
        list = res.data.list.map(order => ({
          id: order.orderNo || order.id,
          orderId: order.id,
          item: order.itemName || '二手商品',
          type: 'market',
          status: order.status === 'completed' ? 'Completed' : 'Processing',
          time: formatTime(order.createTime),
          amount: order.totalAmount
        }));
        
        if (!res.data.hasMore || displayOrders.value.length + list.length >= res.data.total) {
          status.value = 'nomore';
          hasMore.value = false;
        }
      }
      
    } else {
      // 获取全部订单：合并两种类型
      const [foodRes, marketRes] = await Promise.all([
        getFoodOrderList({ page: page.value, size: pageSize }),
        getOrders({ page: page.value, size: pageSize, type: 'market' }).catch(() => ({ code: 500, data: { list: [] } }))
      ]);
      
      // 处理食堂订单
      if (foodRes.code === 200) {
        const foodList = foodRes.data.list.map(order => ({
          id: order.orderNo,
          orderId: order.id,
          item: order.items && order.items.length > 0 ? order.items[0].foodName : '食堂订单',
          type: 'food',
          status: order.status === 'completed' ? 'Completed' : 'Processing',
          time: formatTime(order.createTime),
          amount: order.totalAmount,
          timestamp: new Date(order.createTime).getTime()
        }));
        list = list.concat(foodList);
      }
      
      // 处理二手商品订单
      if (marketRes.code === 200 && marketRes.data) {
        const marketList = marketRes.data.list.map(order => ({
          id: order.orderNo || order.id,
          orderId: order.id,
          item: order.itemName || '二手商品',
          type: 'market',
          status: order.status === 'completed' ? 'Completed' : 'Processing',
          time: formatTime(order.createTime),
          amount: order.totalAmount,
          timestamp: new Date(order.createTime).getTime()
        }));
        list = list.concat(marketList);
      }
      
      // 按时间排序
      list.sort((a, b) => b.timestamp - a.timestamp);
      
      // 检查是否还有更多
      const foodHasMore = foodRes.data?.hasMore || false;
      const marketHasMore = marketRes.data?.hasMore || false;
      if (!foodHasMore && !marketHasMore) {
        status.value = 'nomore';
        hasMore.value = false;
      }
    }
    
    // 关键词过滤
    if (keyword.value) {
      const k = keyword.value.toLowerCase();
      list = list.filter(o => 
        (o.item && o.item.toLowerCase().includes(k)) || 
        (o.id && o.id.toLowerCase().includes(k))
      );
    }
    
    displayOrders.value = [...displayOrders.value, ...list];
    page.value++;
    
    if (list.length === 0) {
      status.value = 'nomore';
      hasMore.value = false;
    } else if (status.value !== 'nomore') {
      status.value = 'loadmore';
    }
  } catch (err) {
    console.error('加载订单列表失败:', err);
    status.value = 'loadmore';
    uni.showToast({ title: '加载订单失败', icon: 'none' });
  }
}

function formatTime(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now - date;
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前';
  
  return `${date.getMonth() + 1}/${date.getDate()}`;
}

function showDetail(order) {
  if (order.type === 'market') {
    uni.navigateTo({ url: `/pages/market/order-detail?id=${order.orderId}` });
  } else {
    uni.navigateTo({ url: `/pages/orders/detail?id=${order.orderId}` });
  }
}

// 获取状态样式类
function getStatusClass(status) {
  switch(status) {
    case 'Completed': return 'completed';
    case 'Cancelled': 
    case 'Refunded': return 'cancelled';
    default: return 'processing';
  }
}

// 获取状态文本
function getStatusText(status) {
  switch(status) {
    case 'Completed': return '已完成';
    case 'Pending': return '待支付';
    case 'Paid': return '待确认';
    case 'Shipping': return '待自提';
    case 'Cancelled': return '已取消';
    case 'Refunded': return '已退款';
    default: return '进行中';
  }
}
</script>

<style scoped lang="scss">
/* 变量定义 */
$primary: #6366F1;
$text-main: #1e293b;
$text-sub: #64748b;
$bg-page: #F8FAFC;
$card-radius: 20px;

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 背景装饰球 */
.bg-decoration {
  position: absolute;
  top: -60px;
  right: -60px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.content {
  padding: 0 20px 20px;
  height: calc(100vh - 88px - 60px); /* 减去导航栏和搜索框高度 */
  position: relative;
  z-index: 1;
}

.search-box {
  padding: 10px 20px;
  position: relative;
  z-index: 2;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 40px;
}

/* 卡片基础样式 */
.card {
  background: #fff;
  border-radius: $card-radius;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(148, 163, 184, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.6);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  position: relative;
  overflow: hidden;

  &:active {
    transform: scale(0.98);
    box-shadow: 0 2px 10px rgba(148, 163, 184, 0.05);
  }
}

/* 头部布局 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 14px;
}

/* 图标容器 */
.icon-box {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;

  /* 食物类型配色 */
  &.food {
    background: linear-gradient(135deg, #FEF3C7 0%, #FFFBEB 100%);
    border: 1px solid #FEF9C3;
  }

  /* 市场/快递类型配色 */
  &.market {
    background: linear-gradient(135deg, #E0E7FF 0%, #EEF2FF 100%);
    border: 1px solid #C7D2FE;
  }
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-title {
  font-weight: 700;
  font-size: 15px;
  color: $text-main;
  line-height: 1.3;
}

.time {
  font-size: 12px;
  color: $text-sub;
}

/* 状态标签 */
.status-tag {
  font-size: 11px;
  padding: 6px 10px;
  border-radius: 100px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;

  &.completed {
    background: #F1F5F9;
    color: #64748B;
  }

  &.processing {
    background: #EFF6FF;
    color: $primary;
  }

  &.cancelled {
    background: #FEF2F2;
    color: #EF4444;
  }
}

.pulse-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: $primary;
  animation: pulse 2s infinite;
}

/* 分割线 */
.divider {
  height: 1px;
  background: #F1F5F9;
  margin: 16px 0;
}

/* 底部布局 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.id-group {
  display: flex;
  align-items: center;
  gap: 8px;

  .label {
    font-size: 12px;
    color: #94A3B8;
  }

  .mono {
    font-family: monospace;
    font-size: 13px;
    color: $text-sub;
    font-weight: 500;
    letter-spacing: 0.5px;
  }
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: $primary;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px 4px 12px;
  border-radius: 100px;
  background: rgba(99, 102, 241, 0.05);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 120px;

  .empty-img {
    width: 200px;
    height: auto;
    margin-bottom: 20px;
    opacity: 0.8;
  }

  .empty-text {
    font-size: 16px;
    font-weight: 700;
    color: $text-main;
    margin-bottom: 8px;
  }

  .empty-sub {
    font-size: 13px;
    color: $text-sub;
  }
}

/* 动画 */
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(99, 102, 241, 0.4); }
  70% { box-shadow: 0 0 0 6px rgba(99, 102, 241, 0); }
  100% { box-shadow: 0 0 0 0 rgba(99, 102, 241, 0); }
}

.animate-enter {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.5s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--delay);
}

@keyframes fadeInUp {
  to { opacity: 1; transform: translateY(0); }
}
</style>