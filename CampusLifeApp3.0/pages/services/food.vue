<template>
  <view class="page">
    <view class="bg-decoration"></view>

   

    <scroll-view scroll-y class="content">
      <u-navbar
        title=""
        :fixed="false"
        :autoBack="false"
        bgColor="transparent"
        leftIcon=""
        :border="false"
      ></u-navbar>
      <view class="tabs-wrapper animate-enter" style="--delay: 0s">
        <view class="tabs-inner">
          <view class="tab-bg" :style="{ transform: `translateX(0%)` }"></view> <view class="tab-item active">
          <text>食堂点餐</text>
        </view>
          <view class="tab-item" @click="goRepair">
            <text>校园报修</text>
          </view>
        </view>
      </view>

      <view class="canteen-card animate-enter" style="--delay: 0.1s" v-if="currentCanteen">
        <view class="card-bg-pattern"></view>
        <view class="canteen-header">
          <view class="header-left">
            <u-icon name="map-fill" color="#fff" size="18"></u-icon>
            <text class="ttl">{{ currentCanteen.name }}</text>
          </view>
          <view class="status-badge" :class="{ closed: currentCanteen.status !== 'open' }">
            <view class="dot" :class="{ 'dot-closed': currentCanteen.status !== 'open' }"></view>
            {{ currentCanteen.status === 'open' ? '营业中' : '已打烊' }}
          </view>
        </view>

        <view class="stats-grid">
          <view class="stat-item">
            <text class="stat-val">{{ currentCanteen.queueCount || 0 }}</text>
            <text class="stat-label">当前排队</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-val">~{{ currentCanteen.estimatedWaitMinutes || 0 }}<text class="unit">分</text></text>
            <text class="stat-label">预计等待</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-val">{{ currentCanteen.availableSeats || 0 }}<text class="unit">/{{ currentCanteen.totalSeats || 0 }}</text></text>
            <text class="stat-label">剩余座位</text>
          </view>
        </view>
        
        <!-- 食堂切换 -->
        <view class="canteen-switch" v-if="canteenList.length > 1">
          <scroll-view scroll-x class="canteen-scroll" :show-scrollbar="false">
            <view class="canteen-list">
              <view 
                v-for="c in canteenList" 
                :key="c.id" 
                class="canteen-item"
                :class="{ active: currentCanteen && currentCanteen.id === c.id }"
                @click="switchCanteen(c)"
              >
                {{ c.name }}
              </view>
            </view>
          </scroll-view>
        </view>
      </view>

      <view class="search-wrapper animate-enter" style="--delay: 0.15s">
        <u-search 
          v-model="keyword" 
          placeholder="搜索美食..." 
          :showAction="false" 
          bgColor="#fff" 
          shape="round"
          :clearabled="true"
        ></u-search>
      </view>

      <view class="cats-wrapper animate-enter" style="--delay: 0.2s">
        <scroll-view scroll-x class="cats-scroll" :show-scrollbar="false">
          <view class="cats-flex">
            <view
                v-for="(c, index) in cats"
                :key="c"
                class="cat-chip"
                :class="{ active: filter === c }"
                @click="filter = c"
            >
              {{ c }}
            </view>
          </view>
        </scroll-view>
      </view>

      <view class="menu-grid animate-enter" style="--delay: 0.3s">
        <view v-for="item in filtered" :key="item.id" class="food-card" @click="showFoodDetail(item)">
          <view class="img-wrapper">
            <image :src="item.image || '/static/images/food/default.jpg'" mode="aspectFill" class="food-img"></image>
            <view class="price-tag">
              <text class="currency">¥</text>{{ (item.price || 0).toFixed(2) }}
            </view>
            <view class="tag-wrapper" v-if="item.tags">
              <text class="food-tag" v-for="tag in (item.tags || '').split(',').slice(0, 2)" :key="tag">{{ tag }}</text>
            </view>
          </view>

          <view class="card-body">
            <view class="info-main">
              <text class="food-name u-line-1">{{ item.name }}</text>
              <text class="food-cat">{{ item.category }}</text>
              <text class="food-stock soldout" v-if="item.stock === 0">已售罄</text>
              <text class="food-stock" v-else-if="item.stock > 0">库存: {{ item.stock }}</text>
              <text class="food-stock unlimited" v-else>库存充足</text>
            </view>
            <view class="add-btn" @click.stop="handleQuickAddToCart(item, $event)">
              <u-icon name="plus" color="#fff" size="14" bold></u-icon>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-if="!loading && filtered.length === 0">
        <u-icon name="search" size="48" color="#ccc"></u-icon>
        <text class="empty-text">暂无菜品</text>
      </view>
      
      <!-- 加载中 -->
      <view class="loading-state" v-if="loading">
        <u-loading-icon size="32" color="#6366F1"></u-loading-icon>
        <text class="loading-text">加载中...</text>
      </view>

      <view style="height: 100px"></view>
    </scroll-view>

    <view v-if="cartCount > 0" class="fab-wrapper animate-bounce-in">
      <view class="fab" @click="openCart">
        <view class="fab-icon">
          <u-icon name="shopping-cart-fill" color="#fff" size="24"></u-icon>
        </view>
        <view class="fab-badge">{{ cartCount }}</view>
      </view>
    </view>

    <!-- 抛物线动画小球 -->
    <view 
      v-for="ball in balls" 
      :key="ball.id" 
      class="ball" 
      :style="ball.style"
    >
      <view class="ball-inner"></view>
    </view>

    <BottomNav active="services" />

    <FoodDetailModal
        :visible="isModalVisible"
        :item="selectedFood"
        @close="isModalVisible = false"
        @add-to-cart="handleAddToCart"
    />
  </view>
</template>

<script setup>
import BottomNav from '@/components/BottomNav.vue'
import { addItem, getCart, setCartCanteen, getCartCanteen } from '@/store/cart.js'
import { ref, computed } from 'vue'
import FoodDetailModal from '@/components/FoodDetailModal.vue'
import { getCanteenList, getFoodByCanteen, searchFood, getFoodById } from '@/api/canteen.js'
import { onLoad, onShow } from '@dcloudio/uni-app'

// 食堂数据
const canteenList = ref([])
const currentCanteen = ref(null)
const foodList = ref([])
const loading = ref(false)

const cats = ['全部','主食','亚洲风味','西式','饮品','小吃']
const filter = ref('全部')
const keyword = ref('')

// 根据分类和关键词过滤菜品
const filtered = computed(()=> {
  let list = foodList.value
  if (filter.value !== '全部') {
    list = list.filter(i => i.category === filter.value)
  }
  if (keyword.value) {
    const k = keyword.value.toLowerCase()
    list = list.filter(i => i.name.toLowerCase().includes(k))
  }
  return list
})

const cartCount = ref(0)
const balls = ref([])
const statusBarHeight = ref(20)
const customBarHeight = ref(44)

onLoad(() => {
  const sys = uni.getSystemInfoSync()
  statusBarHeight.value = sys.statusBarHeight || 20
  
  // #ifdef MP-WEIXIN
  const menuButton = uni.getMenuButtonBoundingClientRect()
  customBarHeight.value = (menuButton.top - statusBarHeight.value) * 2 + menuButton.height
  // #endif
  
  // 加载食堂列表
  loadCanteenList()
})

onShow(() => {
  cartCount.value = getCart().reduce((a,b)=>a+(b.qty||1),0)
})

// 加载食堂列表
async function loadCanteenList() {
  try {
    loading.value = true
    const res = await getCanteenList()
    if (res.code === 200 && res.data) {
      canteenList.value = res.data
      // 默认选择第一个食堂
      if (res.data.length > 0) {
        currentCanteen.value = res.data[0]
        loadFoodList(res.data[0].id)
      }
    }
  } catch (e) {
    console.error('加载食堂列表失败', e)
  } finally {
    loading.value = false
  }
}

// 加载菜品列表
async function loadFoodList(canteenId) {
  try {
    loading.value = true
    const res = await getFoodByCanteen(canteenId)
    if (res.code === 200 && res.data) {
      foodList.value = res.data
    }
  } catch (e) {
    console.error('加载菜品列表失败', e)
  } finally {
    loading.value = false
  }
}

// 切换食堂
function switchCanteen(canteen) {
  currentCanteen.value = canteen
  loadFoodList(canteen.id)
}

const selectedFood = ref(null)
const isModalVisible = ref(false)

async function showFoodDetail(food) {
  try {
    uni.showLoading({ title: '加载中...' })
    const res = await getFoodById(food.id)
    uni.hideLoading()
    if (res.code === 200 && res.data) {
      selectedFood.value = res.data
      isModalVisible.value = true
    } else {
      uni.showToast({ title: '获取菜品详情失败', icon: 'none' })
    }
  } catch (e) {
    uni.hideLoading()
    console.error('获取菜品详情失败', e)
    uni.showToast({ title: '获取菜品详情失败', icon: 'none' })
  }
}

function handleAddToCart(payload) {
  // 检查食堂是否营业
  if (currentCanteen.value && currentCanteen.value.status !== 'open') {
    uni.showToast({
      title: '该食堂已打烊，暂时无法点餐',
      icon: 'none',
    });
    return;
  }
  // 保存当前食堂信息
  if (currentCanteen.value) {
    setCartCanteen(currentCanteen.value)
  }
  const list = addItem({ ...payload.item, qty: payload.quantity });
  cartCount.value = list.reduce((a, b) => a + (b.qty || 1), 0);
  uni.showToast({
    title: `已加入购物车`,
    icon: 'success',
  });
}

function handleQuickAddToCart(item, event) {
  // 检查食堂是否营业
  if (currentCanteen.value && currentCanteen.value.status !== 'open') {
    uni.showToast({
      title: '该食堂已打烊，暂时无法点餐',
      icon: 'none',
    });
    return;
  }
  
  // 检查库存限制
  const cart = getCart();
  const existItem = cart.find(i => i.id === item.id);
  const currentQty = existItem ? existItem.qty : 0;
  const maxStock = item.stock > 0 ? item.stock : 99;
  
  if (currentQty >= maxStock) {
    uni.showToast({
      title: `库存不足，最多可购买${maxStock}个`,
      icon: 'none',
    });
    return;
  }
  
  // 保存当前食堂信息
  if (currentCanteen.value) {
    setCartCanteen(currentCanteen.value)
  }
  // 添加商品到购物车
  const list = addItem({ ...item, qty: 1 });
  cartCount.value = list.reduce((a, b) => a + (b.qty || 1), 0);
  uni.vibrateShort();
  
  // 获取目标位置 (购物车FAB位置)
  const sys = uni.getSystemInfoSync()
  const endX = sys.windowWidth - 52 // FAB center X
  const endY = sys.windowHeight - 118 // FAB center Y
  
  // 获取起始位置
  let startX = sys.windowWidth / 2
  let startY = sys.windowHeight / 2
  
  if (event) {
    // 兼容不同平台的事件对象
    if (event.touches && event.touches.length > 0) {
      startX = event.touches[0].clientX
      startY = event.touches[0].clientY
    } else if (event.detail && typeof event.detail.x === 'number') {
      startX = event.detail.x
      startY = event.detail.y
    } else if (event.clientX) {
      startX = event.clientX
      startY = event.clientY
    }
  }
  
  // 创建小球
  const id = Date.now()
  const ball = {
    id,
    style: {
      left: `${startX}px`,
      top: `${startY}px`,
      opacity: 1,
      transform: 'scale(1)',
      transition: 'none' // 初始无过渡
    },
    innerStyle: {
      transform: 'translateY(0)',
      transition: 'none'
    }
  }
  
  balls.value.push(ball)
  
  // 下一帧触发动画
  setTimeout(() => {
    const idx = balls.value.findIndex(b => b.id === id)
    if (idx > -1) {
      // 抛物线动画原理：
      // 外层控制水平方向 (linear)
      // 内层控制垂直方向 (cubic-bezier 模拟重力)
      
      // 这里简化：直接移动外层，利用 transition-timing-function 差异
      // 实际上直接改变 top/left 并分别设置 transition 也可以
      
      balls.value[idx].style = {
        left: `${endX}px`,
        top: `${endY}px`,
        opacity: 0, // 结束时消失
        transform: 'scale(0.5)', // 变小
        transition: 'left 0.6s linear, top 0.6s cubic-bezier(0.5, -0.1, 1, 1), opacity 0.6s ease-in, transform 0.6s linear'
      }
    }
  }, 50)
  
  // 动画结束后移除
  setTimeout(() => {
    const idx = balls.value.findIndex(b => b.id === id)
    if (idx > -1) balls.value.splice(idx, 1)
  }, 650)
}

function openCart(){ uni.navigateTo({ url:'/pages/cart/cart' }) }
function goNotifs(){ uni.navigateTo({ url:'/pages/notifications/notifications' }) }
function goRepair(){ uni.reLaunch({ url:'/pages/services/repair' }) }
</script>

<style scoped lang="scss">
/* 变量 */
$primary: #6366F1;
$primary-dark: #4F46E5;
$success: #10B981;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: -100px;
  left: -50px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.06) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.content {
  height: calc(100vh - 20px);
  padding: 12px 20px;
  position: relative;
  z-index: 1;
}

/* Search */
.search-wrapper {
  margin-bottom: 16px;
}

/* Tabs */
.tabs-wrapper {
  margin-top: 10px;
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
  transition: color 0.3s;

  &.active { color: $primary-dark; font-weight: 700; }
}

/* Canteen Card */
.canteen-card {
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  color: #fff;
  border-radius: 24px;
  padding: 20px;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 20px -5px rgba(99, 102, 241, 0.4);
}

.card-bg-pattern {
  position: absolute;
  top: 0; right: 0; bottom: 0; left: 0;
  background-image: radial-gradient(circle at 100% 0%, rgba(255,255,255,0.1) 0%, transparent 20%),
  radial-gradient(circle at 0% 100%, rgba(255,255,255,0.1) 0%, transparent 20%);
  z-index: 0;
}

.canteen-header {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;

  .ttl { font-size: 18px; font-weight: 700; letter-spacing: 0.5px; }
}

.status-badge {
  background: rgba(255,255,255,0.2);
  backdrop-filter: blur(4px);
  padding: 4px 10px;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;

  .dot { width: 6px; height: 6px; background: #34D399; border-radius: 50%; box-shadow: 0 0 0 2px rgba(52, 211, 153, 0.3); }
}

.stats-grid {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  background: rgba(255,255,255,0.1);
  border-radius: 16px;
  padding: 12px 0;
  backdrop-filter: blur(4px);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
}

.stat-val {
  font-size: 18px;
  font-weight: 700;
  line-height: 1.2;

  .unit { font-size: 11px; font-weight: 500; opacity: 0.8; margin-left: 1px; }
}

.stat-label {
  font-size: 11px;
  opacity: 0.8;
}

.stat-divider {
  width: 1px;
  background: rgba(255,255,255,0.2);
  margin: 4px 0;
}

/* Categories */
.cats-scroll {
  width: 100%;
  white-space: nowrap;
  margin-bottom: 16px;
}

.cats-flex {
  display: flex;
  padding-right: 20px;
}

.cat-chip {
  display: inline-flex;
  padding: 8px 16px;
  background: #fff;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
  color: $text-sub;
  margin-right: 10px;
  box-shadow: 0 2px 8px rgba(148, 163, 184, 0.08);
  transition: all 0.2s;

  &.active {
    background: $text-main;
    color: #fff;
    box-shadow: 0 4px 12px rgba(30, 41, 59, 0.2);
    transform: translateY(-1px);
  }
}

/* Menu Grid */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.food-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.06);
  transition: transform 0.1s;

  &:active { transform: scale(0.98); }
}

.img-wrapper {
  position: relative;
  width: 100%;
  height: 130px;

  .food-img { width: 100%; height: 100%; }

  .price-tag {
    position: absolute;
    bottom: 8px;
    right: 8px;
    background: rgba(255,255,255,0.95);
    backdrop-filter: blur(4px);
    padding: 4px 8px;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 800;
    color: $text-main;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);

    .currency { font-size: 10px; margin-right: 1px; }
  }
}

.card-body {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-main {
  flex: 1;
  margin-right: 8px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.food-name {
  font-size: 14px;
  font-weight: 700;
  color: $text-main;
}

.food-cat {
  font-size: 10px;
  color: $text-sub;
}

.food-stock {
  font-size: 10px;
  color: #F59E0B;
  font-weight: 500;
  &.unlimited {
    color: #10B981;
  }
  &.soldout {
    color: #EF4444;
  }
}

.add-btn {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
  transition: transform 0.2s;

  &:active { transform: scale(0.9); }
}

/* FAB */
.fab-wrapper {
  position: fixed;
  right: 24px;
  bottom: 90px;
  z-index: 100;
}

.fab {
  width: 56px;
  height: 56px;
  background: $text-main;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.4);
  position: relative;

  &:active { transform: scale(0.95); }
}

.fab-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #EF4444;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  min-width: 18px;
  height: 18px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid $bg-page;
}

/* Animations */
.animate-enter {
  opacity: 0;
  transform: translateY(15px);
  animation: fadeUp 0.5s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--delay);
}

.animate-bounce-in {
  animation: bounceIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}

@keyframes bounceIn {
  from { transform: scale(0); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

/* Animation Ball */
.ball {
  position: fixed;
  z-index: 9999; /* Ensure it's on top of everything */
  pointer-events: none;
  width: 20px;
  height: 20px;
  margin-left: -10px; /* Center anchor point */
  margin-top: -10px;
}

.ball-inner {
  width: 100%;
  height: 100%;
  background: $primary;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.5);
}

/* 食堂切换 */
.canteen-switch {
  margin-top: 16px;
  position: relative;
  z-index: 1;
}

.canteen-scroll {
  width: 100%;
  white-space: nowrap;
}

.canteen-list {
  display: flex;
  gap: 8px;
}

.canteen-item {
  display: inline-flex;
  padding: 6px 12px;
  background: rgba(255,255,255,0.2);
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
  color: rgba(255,255,255,0.8);
  transition: all 0.2s;
  
  &.active {
    background: #fff;
    color: $primary-dark;
    font-weight: 600;
  }
}

/* 状态徽章 */
.status-badge.closed {
  background: rgba(239, 68, 68, 0.2);
  
  .dot-closed {
    background: #EF4444;
    box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.3);
  }
}

/* 标签 */
.tag-wrapper {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 4px;
}

.food-tag {
  background: rgba(99, 102, 241, 0.9);
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  
  .empty-text {
    margin-top: 12px;
    font-size: 14px;
    color: $text-sub;
  }
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  
  .loading-text {
    margin-top: 12px;
    font-size: 14px;
    color: $text-sub;
  }
}
</style>