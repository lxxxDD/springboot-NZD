<template>
  <view class="page">
    <view class="bg-decoration-1"></view>
    <view class="bg-decoration-2"></view>
    <view class="bg-decoration-3"></view>
    <view class="content">
      <!-- 固定头部区域 -->
      <view class="fixed-header" :style="{ paddingTop: statusBarHeight + 'px' }">
        <view 
          class="search-container" 
          :style="{ paddingRight: headerPaddingRight + 'px' }"
        >
          <view class="brand-text">Market</view>
          <u-search 
            v-model="keyword" 
            placeholder="搜好物..." 
            :showAction="false" 
            bgColor="rgba(255,255,255,0.9)" 
            shape="round"
            :clearabled="true"
            height="36"
            :inputStyle="{fontWeight: '500'}"
          ></u-search>
        </view>
        
        <view class="filter-section">
          <MarketFilter 
            :cats="cats"
            v-model:activeCat="activeCat"
            v-model:currentSort="currentSort"
          />
        </view>
      </view>
      
      <!-- 占位元素，防止内容被固定头部遮挡 -->
      <view class="header-placeholder" :style="{ height: headerHeight + 'px' }"></view>
      
      <view class="waterfall-container animate-enter" style="--delay: 0.2s">
        <u-waterfall 
          v-model="displayItems" 
          ref="waterfallRef"
          :add-time="0"
          :columns="2"
          :gap="16"
        >
          <template #default="{ item, index }">
            <view class="market-card" @click="showDetail(item.id)">
              <view class="img-box">
                <image :src="item.image" mode="aspectFill" class="cover-img"></image>
                <view class="price-tag">¥{{ item.price }}</view>
              </view>
              <view class="card-body">
                <text class="item-name u-line-2">{{ item.title }}</text>
                <view class="seller-row">
                  <u-avatar :src="item.sellerAvatar || '/static/avatar/1.png'" size="16"></u-avatar>
                  <text class="seller-name">{{ item.seller }}</text>
                </view>
                <view class="action-row">
                  <text class="time">刚刚发布</text>
                  <u-icon name="chat" color="#6366F1" size="16"></u-icon>
                </view>
              </view>
            </view>
          </template>
        </u-waterfall>
      </view>

      <u-loadmore :status="status" :loading-text="'加载更多好物...'" :nomore-text="'到底啦，快去发布闲置吧'" line marginTop="20" marginBottom="20" />
      
      <view style="height: 100px" />
    </view>

    <view class="fab-wrapper animate-bounce-in">
      <button class="fab-btn" @click="createListing">
        <u-icon name="plus" color="#fff" size="24"></u-icon>
      </button>
    </view>

    <BottomNav active="market" />
  </view>
</template>

<script setup>
import BottomNav from '@/components/BottomNav.vue'
import MarketFilter from '@/components/MarketFilter.vue'
// 用户信息从本地存储获取
import { getMarketItems, getMarketCategories } from '@/api/market.js'
import { baseURL } from '@/api/request.js'
import { computed, ref, watch } from 'vue'
import { onLoad, onReachBottom, onShow } from '@dcloudio/uni-app'

function formatAvatarUrl(url) {
  if (!url) return '/static/avatar/1.png'
  if (url.startsWith('http')) return url
  return baseURL + url
}

const unreadCount = ref(0)
const cats = ref(['全部'])
const categoryList = ref([]) // 存储分类完整数据
const activeCat = ref(0)
const keyword = ref('')
const currentSort = ref('newest') // 当前排序方式
const waterfallRef = ref(null) // 瀑布流组件引用

// 获取系统信息
const systemInfo = uni.getSystemInfoSync()
const statusBarHeight = systemInfo.statusBarHeight || 0
const headerHeight = statusBarHeight + 180 // 头部区域高度，用于占位（状态栏 + 搜索 + 筛选）
const headerPaddingRight = ref(16) // 头部右侧内边距

// 分页相关
const displayItems = ref([])
const status = ref('loadmore')
const page = ref(1)
const pageSize = 10
const hasMore = ref(true)

onLoad(async () => {
  // 获取胶囊按钮位置信息 (微信小程序适配关键)
  // #ifdef MP-WEIXIN
  const menuButtonInfo = uni.getMenuButtonBoundingClientRect()
  const sysInfo = uni.getSystemInfoSync()
  
  // 计算头部右侧需要留出的空白，避开胶囊按钮
  // 胶囊右边距 = 屏幕宽度 - 胶囊右边位置
  const menuButtonRight = sysInfo.screenWidth - menuButtonInfo.right
  // 头部右侧内边距 = 胶囊宽度 + 左右边距
  headerPaddingRight.value = sysInfo.screenWidth - menuButtonInfo.left + menuButtonRight
  // #endif

  // 获取分类列表
  try {
    const res = await getMarketCategories()
    if (res.code === 200 && res.data) {
      categoryList.value = res.data
      cats.value = ['全部', ...res.data.map(c => c.name)]
    }
  } catch (e) {
    console.error('获取分类失败', e)
  }

  reload()
})

onReachBottom(() => {
  loadMore()
})

// 页面显示时检查是否需要刷新（发布商品后返回）
onShow(() => {
  const needRefresh = uni.getStorageSync('marketNeedRefresh')
  if (needRefresh) {
    uni.removeStorageSync('marketNeedRefresh')
    reload()
  }
})

// 监听过滤条件变化，重置分页
watch([activeCat, keyword, currentSort], () => {
  reload()
})

function reload() {
  page.value = 1
  displayItems.value = []
  hasMore.value = true
  status.value = 'loadmore'
  loadMore()
}

async function loadMore() {
  if (status.value === 'nomore' || status.value === 'loading') return
  
  status.value = 'loading'
  
  try {
    // 构建请求参数
    const params = {
      page: page.value,
      size: pageSize
    }
    
    // 分类筛选
    if (activeCat.value !== 0) {
      const catName = cats.value[activeCat.value]
      // 使用分类名称或ID
      const category = categoryList.value.find(c => c.name === catName)
      params.category = category?.name || catName
    }
    
    // 关键词搜索
    if (keyword.value) {
      params.keyword = keyword.value
    }
    
    // 排序
    if (currentSort.value !== 'newest') {
      params.sort = currentSort.value
    }
    
    const res = await getMarketItems(params)
    
    // 处理返回数据
    const list = res.data.list.map(item => ({
      id: item.id,
      title: item.title,
      price: item.price,
      seller: item.sellerName,
      sellerAvatar: formatAvatarUrl(item.sellerAvatar),
      image: item.coverImage,
      category: item.category || 'Other',
      status: item.status
    }))
    
    displayItems.value = [...displayItems.value, ...list]
    page.value++
    
    if (!res.data.hasMore || displayItems.value.length >= res.data.total) {
      status.value = 'nomore'
      hasMore.value = false
    } else {
      status.value = 'loadmore'
    }
  } catch (err) {
    console.error('加载商品列表失败:', err)
    status.value = 'loadmore'
  }
}

function showDetail(id) {
  uni.navigateTo({ url: `/pages/market/detail?id=${id}` });
}
function createListing(){ uni.navigateTo({ url:'/pages/market/create' }) }
function goNotifs(){ uni.navigateTo({ url:'/pages/notifications/notifications' }) }
</script>

<style scoped lang="scss">
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page { 
  background: $bg-page; 
  min-height: 100vh; 
  position: relative; 
  overflow-x: visible; /* Ensure sticky works */
  overflow-y: visible;
}

.bg-decoration-1 {
  position: fixed;
  top: -150px; left: -150px;
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.12) 0%, rgba(99, 102, 241, 0.03) 40%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
  animation: float 20s ease-in-out infinite;
}

.bg-decoration-2 {
  position: fixed;
  top: 200px; right: -200px;
  width: 450px; height: 450px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.1) 0%, rgba(16, 185, 129, 0.02) 40%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
  animation: float 25s ease-in-out infinite reverse;
}

.bg-decoration-3 {
  position: fixed;
  bottom: -100px; left: 50%;
  transform: translateX(-50%);
  width: 400px; height: 400px;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.08) 0%, rgba(245, 158, 11, 0.02) 40%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
  animation: float 30s ease-in-out infinite;
}

.content { 
  min-height: 100vh; 
  padding: 0; 
  position: relative; 
  z-index: 1; 
  box-sizing: border-box;
  padding-bottom: calc(70px + env(safe-area-inset-bottom)); /* 底部导航栏高度 */
}

/* --- 固定头部区域 --- */
.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 99;
  background: linear-gradient(180deg, rgba(255,255,255,0.98) 0%, rgba(255,255,255,0.95) 100%);
  backdrop-filter: blur(20px);
  padding: 12px 16px 16px;
  border-radius: 0 0 28px 28px;
  box-shadow: 0 4px 24px rgba(99, 102, 241, 0.08);
}

.header-placeholder {
  /* 占位元素，防止内容被固定头部遮挡 */
  display: block;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  
  :deep(.u-search) {
    box-shadow: 0 2px 8px rgba(99, 102, 241, 0.1);
    border: 1px solid rgba(99, 102, 241, 0.08);
    transition: all 0.3s;
    flex: 1;
    
    &:active {
      transform: scale(0.98);
    }
  }
}

.brand-text {
  font-size: 20px;
  font-weight: 800;
  background: linear-gradient(135deg, $primary 0%, #4F46E5 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-family: 'Helvetica Neue', sans-serif;
  letter-spacing: -0.5px;
  flex-shrink: 0;
}

/* Waterfall Container */
.waterfall-container {
  padding: 0 20px;
}

.market-card {
  width: 100%;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(148, 163, 184, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  margin-bottom: 16px;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0; bottom: 0;
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.03) 0%, rgba(16, 185, 129, 0.02) 100%);
    opacity: 0;
    transition: opacity 0.3s;
    pointer-events: none;
  }

  &:hover::before {
    opacity: 1;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(99, 102, 241, 0.15);
  }

  &:active { 
    transform: translateY(-2px) scale(0.98); 
  }
}

.img-box {
  position: relative;
  width: 100%;
  height: 160px;

  .cover-img { width: 100%; height: 100%; }

  .price-tag {
    position: absolute;
    bottom: 8px; left: 8px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
    backdrop-filter: blur(8px);
    padding: 6px 12px;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 800;
    color: $primary;
    box-shadow: 0 2px 8px rgba(99, 102, 241, 0.2);
    border: 1px solid rgba(99, 102, 241, 0.1);
  }
}

.card-body { padding: 12px; }

.item-name { font-size: 14px; font-weight: 700; color: $text-main; margin-bottom: 8px; line-height: 1.4; height: 38px; }

.seller-row {
  display: flex; align-items: center; gap: 6px; margin-bottom: 8px;
  .seller-name { font-size: 11px; color: $text-sub; }
}

.action-row {
  display: flex; justify-content: space-between; align-items: center;
  .time { font-size: 10px; color: #94A3B8; }
}

/* FAB */
.fab-wrapper {
  position: fixed; right: 24px; bottom: 90px; z-index: 100;
}

.fab-btn {
  width: 56px; height: 56px;
  background: linear-gradient(135deg, $primary 0%, #4F46E5 100%);
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 24px rgba(79, 70, 229, 0.3);
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: scale(1.1) rotate(90deg);
    box-shadow: 0 12px 32px rgba(79, 70, 229, 0.4);
  }

  &:active { 
    transform: scale(0.95); 
  }
}

/* Animations */
.animate-enter { 
  opacity: 0; 
  transform: translateY(20px); 
  animation: fadeUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards; 
  animation-delay: var(--delay); 
}

.animate-bounce-in { 
  animation: bounceIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1); 
}

@keyframes fadeUp { 
  to { 
    opacity: 1; 
    transform: translateY(0); 
  } 
}

@keyframes bounceIn { 
  from { 
    transform: scale(0); 
    opacity: 0; 
  } 
  to { 
    transform: scale(1); 
    opacity: 1; 
  } 
}

@keyframes float {
  0%, 100% { 
    transform: translate(0, 0); 
  }
  50% { 
    transform: translate(30px, 30px); 
  }
}
</style>