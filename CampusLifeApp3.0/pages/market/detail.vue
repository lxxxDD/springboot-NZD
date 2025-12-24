<template>
  <view class="page">
    <u-navbar title=" " :autoBack="true" bgColor="transparent" leftIconColor="#fff"></u-navbar>

    <scroll-view scroll-y class="content">
      <view class="swiper-area">
        <u-swiper
            :list="swiperList"
            height="380"
            indicator
            indicatorMode="dot"
            indicatorActiveColor="#6366F1"
            indicatorInactiveColor="rgba(255,255,255,0.5)"
            circular
            radius="0"
            @click="previewImage"
        ></u-swiper>
        <view class="img-mask"></view>
      </view>

      <view class="main-card animate-slide-up">
        <view class="price-row">
          <view class="price-box">
            <text class="currency">¥</text>
            <text class="amount">{{ item.price }}</text>
          </view>
          <view class="share-btn hover-scale">
            <u-icon name="share-square" color="#64748B" size="20"></u-icon>
          </view>
        </view>

        <text class="title">{{ item.title }}</text>
        <view class="tags-row">
          <view class="tag-badge blue">
            <u-icon name="tags-fill" color="#3B82F6" size="12"></u-icon>
            <text>{{ item.category }}</text>
          </view>
          <view class="tag-badge" :class="item.status === 'active' ? 'green' : (item.status === 'violation' ? 'red' : 'gray')">
            <u-icon :name="item.status === 'active' ? 'checkmark-circle' : (item.status === 'sold' ? 'shopping-cart' : (item.status === 'violation' ? 'close-circle' : 'lock'))"
                    :color="item.status === 'active' ? '#10B981' : (item.status === 'violation' ? '#EF4444' : '#64748B')" size="12"></u-icon>
            <text>{{ item.status === 'active' ? '出售中' : (item.status === 'sold' ? '已售出' : (item.status === 'violation' ? '已违规' : '已下架')) }}</text>
          </view>
          <view class="tag-badge orange" v-if="swiperList.length > 1">
            <u-icon name="photo" color="#F97316" size="12"></u-icon>
            <text>{{ swiperList.length }}张图片</text>
          </view>
        </view>

        <view class="meta-row">
          <text>{{ item.viewCount || 0 }} 浏览</text>
          <text>•</text>
          <text>{{ formatTime(item.createTime) }}</text>
        </view>

        <!-- 违规提示 -->
        <view class="violation-alert" v-if="item.status === 'violation'">
          <view class="violation-header">
            <u-icon name="close-circle-fill" color="#EF4444" size="20"></u-icon>
            <text class="violation-title">该商品已被标记为违规</text>
          </view>
          <text class="violation-reason">原因：{{ item.rejectReason || '违反平台规定' }}</text>
        </view>

        <u-divider color="#F1F5F9"></u-divider>

        <view class="seller-card hover-scale">
          <u-avatar :src="item.avatar || 'https://via.placeholder.com/100'" size="48"></u-avatar>
          <view class="seller-info">
            <text class="seller-name">{{ item.seller }}</text>
            <text class="seller-credit">信用极好 • 交易 23 次</text>
          </view>
          <view class="follow-btn" v-if="!isOwner" @click="goUserProfile">
            <text>主页</text>
            <u-icon name="arrow-right" color="#6366F1" size="10"></u-icon>
          </view>
          <view class="follow-btn owner" v-else>
            <text>我自己</text>
          </view>
        </view>

        <view class="desc-section">
          <text class="section-title">商品详情</text>
          <text class="desc-text">{{ item.desc || '卖家很懒，没有填写详细描述。建议通过聊天询问细节，如成色、是否可小刀等。' }}</text>
        </view>

        <view class="safety-tip">
          <u-icon name="info-circle-fill" color="#F59E0B" size="16"></u-icon>
          <text class="tip-text">校园交易请注意人身财产安全，建议在公共场所（如食堂、图书馆）面交。</text>
        </view>

        <view style="height: 100px"></view>
      </view>
    </scroll-view>

    <view class="bottom-bar animate-fade-in">
      <view class="action-group">
        <view class="icon-action hover-scale" @click="handleFavorite">
          <u-icon :name="item.isFavorite ? 'heart-fill' : 'heart'" size="22" :color="item.isFavorite ? '#EF4444' : '#64748B'"></u-icon>
          <text>{{ item.isFavorite ? '已收藏' : '收藏' }}</text>
        </view>
      </view>

      <view class="btn-group" v-if="!isOwner">
        <u-button
            text="联系"
            shape="circle"
            customStyle="width: 100px; height: 44px; background: #EEF2FF; color: #6366F1; border: none; font-weight: 700; margin-right: 12px;"
            @click="contact"
        >
          <template #prefix>
            <u-icon name="chat-fill" color="#6366F1" size="18" style="margin-right: 4px"></u-icon>
          </template>
        </u-button>

        <u-button
            v-if="item.status === 'active'"
            text="立即购买"
            shape="circle"
            type="primary"
            customStyle="flex: 1; height: 44px; background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%); border: none; font-weight: 700; box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);"
            @click="handleBuy"
        >
        </u-button>
        <u-button
            v-else
            :text="item.status === 'sold' ? '已售出' : '已下架'"
            shape="circle"
            disabled
            customStyle="flex: 1; height: 44px; background: #E2E8F0; color: #94A3B8; border: none; font-weight: 700;"
        >
        </u-button>
      </view>

      <view class="btn-group" v-else>
        <u-button
            text="管理商品"
            shape="circle"
            type="primary"
            customStyle="flex: 1; height: 44px; background: #1E293B; border: none; font-weight: 700; box-shadow: 0 4px 12px rgba(30, 41, 59, 0.3);"
            @click="showManageSheet = true"
        >
          <template #prefix>
            <u-icon name="setting-fill" color="#fff" size="18" style="margin-right: 6px"></u-icon>
          </template>
        </u-button>
      </view>
    </view>

    <u-action-sheet
        :show="showManageSheet"
        :actions="manageActions"
        title=" "
        description=""
        @close="showManageSheet = false"
        @select="onManageSelect"
        :round="16"
    ></u-action-sheet>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMarketItemDetail, updateMarketItemStatus, deleteMarketItem, toggleFavorite } from '@/api/market.js'
import { getOrCreateConversation } from '@/api/messages.js'

const item = ref({
  id: '',
  status: 'active', // active: 出售中, inactive: 已下架, sold: 已售出
  seller: '',
  sellerId: null
})
const showManageSheet = ref(false)
const loading = ref(true)

// 获取当前用户信息
const userInfo = ref(uni.getStorageSync('userInfo') || {})

// 判断当前用户是否为卖家
const isOwner = computed(() => {
  const sellerId = item.value.sellerId
  console.log('=== isOwner 判断 ===')
  console.log('商品sellerId:', sellerId, typeof sellerId)
  console.log('userInfo:', JSON.stringify(userInfo.value))
  
  if (!sellerId) {
    console.log('sellerId为空，返回false')
    return false
  }
  
  // 尝试多种字段匹配
  const possibleIds = [
    userInfo.value.id,
    userInfo.value.userId, 
    userInfo.value.studentId
  ].filter(Boolean)
  
  console.log('possibleIds:', possibleIds)
  
  // 类型转换后比较（防止字符串与数字不匹配）
  const result = possibleIds.some(id => String(id) === String(sellerId))
  console.log('isOwner结果:', result)
  return result
})

// 动态生成管理菜单
const manageActions = computed(() => [
  {
    name: '编辑详情',
    value: 'edit',
    subname: '修改价格、描述等信息'
  },
  {
    name: item.value.status === 'active' ? '下架商品' : '重新上架',
    value: 'toggle_status',
    color: '#F59E0B' // 橙色警告
  },
  {
    name: '删除商品',
    value: 'delete',
    color: '#EF4444', // 红色危险
    subname: '删除后不可恢复'
  }
])

const swiperList = computed(() => {
  if (item.value.images && Array.isArray(item.value.images) && item.value.images.length > 0) {
    return item.value.images
  }
  if (item.value.coverImage) {
    return [item.value.coverImage]
  }
  return ['https://via.placeholder.com/400x400']
})

onLoad(async (options) => {
  const id = options.id
  await loadItemDetail(id)
})

async function loadItemDetail(id) {
  loading.value = true
  // 重新获取最新的用户信息
  userInfo.value = uni.getStorageSync('userInfo') || {}
  
  try {
    const res = await getMarketItemDetail(id)
    const data = res.data
    item.value = {
      id: data.id,
      title: data.title,
      price: data.price,
      originalPrice: data.originalPrice,
      category: data.category,
      condition: data.conditionLevel,
      desc: data.description,
      seller: data.sellerName || data.seller,
      sellerId: data.sellerId || data.userId || data.seller_id,
      avatar: data.sellerAvatar || data.avatar || 'https://via.placeholder.com/100',
      status: data.status || 'active',
      rejectReason: data.rejectReason,  // 违规原因
      images: data.images || [],
      coverImage: data.coverImage,
      viewCount: data.viewCount,
      favoriteCount: data.favoriteCount,
      isFavorite: data.isFavorite
    }
    
    console.log('商品详情:', item.value)
    console.log('当前用户:', userInfo.value)
    console.log('是否为卖家:', isOwner.value)
  } catch (err) {
    console.error('加载商品详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function formatTime(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getMonth()+1}月${date.getDate()}日`
}

function previewImage(index) {
  uni.previewImage({
    current: index,
    urls: swiperList.value
  })
}

function goUserProfile() {
  if (!item.value.sellerId) {
    uni.showToast({ title: '用户信息不完整', icon: 'none' })
    return
  }
  uni.navigateTo({
    url: `/pages/profile/user-detail?userId=${item.value.sellerId}`,
    fail: (err) => {
      console.error('跳转失败:', err)
      uni.showToast({ title: '页面跳转失败', icon: 'none' })
    }
  })
}

async function contact() {
  // 检查sellerId是否存在
  if (!item.value.sellerId) {
    console.error('卖家ID不存在:', item.value)
    uni.showToast({
      title: '卖家信息异常',
      icon: 'none'
    })
    return
  }
  
  try {
    console.log('创建会话，卖家ID:', item.value.sellerId)
    // 先创建会话
    await getOrCreateConversation(item.value.sellerId)
    
    // 然后导航到聊天页面
    const url = `/pages/messages/chat?userId=${item.value.sellerId}&name=${item.value.seller}&avatar=${encodeURIComponent(item.value.avatar)}&autoSend=true`;
    uni.navigateTo({ url })
  } catch (error) {
    console.error('创建会话失败:', error)
    uni.showToast({
      title: '操作失败',
      icon: 'none'
    })
  }
}

function handleBuy() {
  const url = `/pages/payment/payment?amount=${item.value.price}&count=1&type=market&itemId=${item.value.id}&itemName=${encodeURIComponent(item.value.title)}&seller=${encodeURIComponent(item.value.seller)}`;
  uni.navigateTo({ url });
}

// 处理管理操作
async function onManageSelect(action) {
  if (action.value === 'toggle_status') {
    try {
      const newStatus = item.value.status === 'active' ? 'inactive' : 'active'
      await updateMarketItemStatus(item.value.id, newStatus)
      item.value.status = newStatus
      uni.showToast({
        title: newStatus === 'active' ? '已重新上架' : '商品已下架',
        icon: 'success'
      })
    } catch (err) {
      console.error('更新状态失败:', err)
    }
  } else if (action.value === 'delete') {
    uni.showModal({
      title: '确认删除',
      content: '确定要删除这个商品吗？此操作无法撤销。',
      success: async (res) => {
        if (res.confirm) {
          try {
            await deleteMarketItem(item.value.id)
            uni.showToast({ title: '删除成功', icon: 'success' })
            setTimeout(() => uni.navigateBack(), 800)
          } catch (err) {
            console.error('删除失败:', err)
          }
        }
      }
    })
  } else if (action.value === 'edit') {
    uni.navigateTo({ url: `/pages/market/edit?id=${item.value.id}` })
  }
}

// 收藏/取消收藏
async function handleFavorite() {
  try {
    const res = await toggleFavorite(item.value.id)
    item.value.isFavorite = res.data.isFavorite
    uni.showToast({
      title: item.value.isFavorite ? '已收藏' : '已取消收藏',
      icon: 'success'
    })
  } catch (err) {
    console.error('收藏操作失败:', err)
  }
}
</script>

<style scoped lang="scss">
/* 变量 */
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page { background: $bg-page; min-height: 100vh; }

/* 顶部轮播区 */
.swiper-area { position: relative; width: 100%; }
.img-mask {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, transparent 40%, rgba(0,0,0,0.4) 100%);
  pointer-events: none; z-index: 10;
}

/* 主要内容卡片 */
.main-card {
  position: relative; top: -30px; background: #fff;
  border-top-left-radius: 32px; border-top-right-radius: 32px;
  padding: 24px; min-height: 60vh; z-index: 20;
}

/* 价格行 */
.price-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.price-box {
  color: $primary; font-weight: 800; display: flex; align-items: baseline;
  .currency { font-size: 18px; margin-right: 2px; }
  .amount { font-size: 32px; line-height: 1; letter-spacing: -1px; }
}
.share-btn {
  width: 40px; height: 40px; background: #F1F5F9; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
}

/* 标题与标签 */
.title {
  font-size: 22px; font-weight: 700; color: $text-main; line-height: 1.4;
  margin-bottom: 12px; display: block;
}
.tags-row { display: flex; gap: 8px; margin-bottom: 24px; flex-wrap: wrap; }
.tag-badge {
  display: flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 8px; font-size: 12px; font-weight: 500;
  &.blue { background: #EFF6FF; color: #3B82F6; }
  &.green { background: #ECFDF5; color: #10B981; }
  &.gray { background: #F8FAFC; color: #94A3B8; }
  &.orange { background: #FFF7ED; color: #F97316; }
  &.red { background: #FEF2F2; color: #EF4444; }
}

/* 违规提示 */
.violation-alert {
  background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
  border: 1px solid #FECACA;
  border-radius: 12px;
  padding: 14px 16px;
  margin: 16px 0;
}
.violation-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.violation-title {
  font-size: 15px;
  font-weight: 700;
  color: #DC2626;
}
.violation-reason {
  font-size: 13px;
  color: #991B1B;
  line-height: 1.5;
  display: block;
  padding-left: 28px;
}

.meta-row {
  display: flex; align-items: center; gap: 8px;
  font-size: 13px; color: #94A3B8; margin-bottom: 20px;
}

/* 卖家卡片 */
.seller-card {
  background: #F8FAFC; border-radius: 16px; padding: 12px 16px;
  display: flex; align-items: center; gap: 12px; margin: 24px 0; border: 1px solid #F1F5F9;
}
.seller-info {
  flex: 1; display: flex; flex-direction: column; gap: 2px;
  .seller-name { font-size: 15px; font-weight: 700; color: $text-main; }
  .seller-credit { font-size: 11px; color: #10B981; }
}
.follow-btn {
  display: flex; align-items: center; gap: 2px; font-size: 12px; color: $primary; font-weight: 600;
  padding: 4px 10px; background: #fff; border-radius: 100px; box-shadow: 0 2px 6px rgba(0,0,0,0.03);

  &.owner { color: #94A3B8; box-shadow: none; background: transparent; }
}

/* 描述区 */
.desc-section { margin-bottom: 24px; }
.section-title {
  font-size: 16px; font-weight: 700; color: $text-main;
  margin-bottom: 12px; display: block; border-left: 4px solid $primary; padding-left: 10px;
}
.desc-text { font-size: 15px; color: #475569; line-height: 1.8; white-space: pre-wrap; }

/* 安全提示 */
.safety-tip {
  background: #FFFBEB; border: 1px solid #FEF3C7; border-radius: 12px;
  padding: 12px; display: flex; gap: 8px; align-items: flex-start;
  .tip-text { font-size: 12px; color: #B45309; line-height: 1.5; flex: 1; }
}

/* 底部栏 */
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; padding: 12px 24px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  box-shadow: 0 -4px 20px rgba(0,0,0,0.05);
  display: flex; align-items: center; justify-content: space-between;
  z-index: 100;
}

.action-group {
  display: flex; margin-right: 16px;
  .icon-action {
    display: flex; flex-direction: column; align-items: center; gap: 2px;
    text { font-size: 10px; color: $text-sub; }
  }
}

.btn-group {
  flex: 1;
  display: flex;
  align-items: center;
}

/* 动画 */
.hover-scale:active { transform: scale(0.98); transition: 0.1s; }
.animate-slide-up { animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
.animate-fade-in { animation: fadeIn 0.6s 0.2s backwards; }
@keyframes slideUp { from { transform: translateY(40px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
</style>