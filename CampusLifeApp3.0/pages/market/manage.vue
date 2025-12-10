<template>
  <view class="page">
    <view class="bg-decoration-top"></view>
    <view class="bg-decoration-bottom"></view>
    
    <u-navbar 
      title="交易管理" 
      :autoBack="true" 
      bgColor="transparent" 
      leftIconColor="#1E293B" 
      titleStyle="color: #1E293B; font-weight: 800; font-size: 18px;" 
      placeholder
    ></u-navbar>
    
    <view class="tabs-section animate-enter" style="--delay: 0.1s">
      <u-tabs
          :list="tabs"
          :current="currentTab"
          @change="onTabChange"
          lineColor="#6366F1"
          lineWidth="30"
          lineHeight="4"
          activeStyle="color: #6366F1; font-weight: 800; font-size: 16px; transform: scale(1.05);"
          inactiveStyle="color: #94A3B8; font-weight: 600; font-size: 15px;"
          itemStyle="padding-left: 15px; padding-right: 15px; height: 50px;"
          :scrollable="false"
      ></u-tabs>
    </view>
    
    <swiper :current="currentTab" class="swiper-content animate-enter" style="--delay: 0.2s" @change="onSwiperChange">
      <!-- Tab 0: 我发布的 -->
      <swiper-item>
        <market-listings 
          :list="myListings" 
          @refresh="loadData" 
          @delete="handleDelete"
        ></market-listings>
      </swiper-item>
      
      <!-- Tab 1: 我卖出的 -->
      <swiper-item>
        <market-sales :list="mySales"></market-sales>
      </swiper-item>
      
      <!-- Tab 2: 我买到的 -->
      <swiper-item>
        <market-purchases :list="myPurchases"></market-purchases>
      </swiper-item>
    </swiper>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyMarketItems } from '@/api/market.js'
import { getOrders } from '@/api/orders.js'

import MarketListings from './components/MarketListings.vue'
import MarketSales from './components/MarketSales.vue'
import MarketPurchases from './components/MarketPurchases.vue'

const tabs = [{ name: '我发布的' }, { name: '我卖出的' }, { name: '我买到的' }]
const currentTab = ref(0)
const myListings = ref([])
const mySales = ref([])
const myPurchases = ref([])

onShow(() => {
  loadData()
})

async function loadData() {
  uni.showLoading({ title: '加载中...' })
  try {
    // 1. Load My Listings
    const listingsRes = await getMyMarketItems()
    if (listingsRes.code === 0 || listingsRes.success) {
      myListings.value = listingsRes.data || []
    }
    
    // 2. Load My Sales (Orders where I am seller)
    const salesRes = await getOrders({ role: 'seller', size: 100 })
    if (salesRes.code === 0 || salesRes.success) {
      // Backend returns PageVO, data is in records or directly in data depending on implementation
      // Based on Controller: Result<PageVO<OrderVO>>
      const pageData = salesRes.data
      mySales.value = (pageData.records || pageData.list || []).map(mapOrder)
    }
    
    // 3. Load My Purchases (Orders where I am buyer)
    const purchasesRes = await getOrders({ role: 'buyer', size: 100 })
    if (purchasesRes.code === 0 || purchasesRes.success) {
      const pageData = purchasesRes.data
      myPurchases.value = (pageData.records || pageData.list || []).map(mapOrder)
    }
  } catch (e) {
    console.error('加载数据失败', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

// Map backend OrderVO to frontend display format if needed
function mapOrder(order) {
  return {
    id: order.id,
    item: order.items && order.items.length > 0 ? order.items[0].itemName : '未知商品',
    image: order.items && order.items.length > 0 ? order.items[0].itemImage : '',
    amount: order.totalAmount,
    buyer: order.buyerName,
    seller: order.sellerName,
    timestamp: new Date(order.createTime).getTime(),
    status: order.status
  }
}

function onTabChange(item) {
  currentTab.value = item.index
}

function onSwiperChange(e) {
  currentTab.value = e.detail.current
}

function handleDelete(item) {
  const index = myListings.value.findIndex(i => i.id === item.id)
  if (index !== -1) {
    myListings.value.splice(index, 1)
  }
}
</script>

<style scoped lang="scss">
$bg-page: #F8FAFC;

.page { 
  background: $bg-page; 
  height: 100vh; 
  display: flex; 
  flex-direction: column; 
  position: relative;
  overflow: hidden;
}

.bg-decoration-top {
  position: absolute; top: -150px; right: -100px; width: 400px; height: 400px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
}

.bg-decoration-bottom {
  position: absolute; bottom: -150px; left: -100px; width: 300px; height: 300px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.05) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
}

.tabs-section {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  z-index: 10;
  margin: 0 16px 10px 16px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(148, 163, 184, 0.05);
  padding: 4px;
}

.swiper-content { flex: 1; }

.animate-enter { opacity: 0; transform: translateY(10px); animation: fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards; animation-delay: var(--delay); }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
</style>
