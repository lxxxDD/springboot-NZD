<template>
  <scroll-view scroll-y class="scroll-content">
    <view v-if="list.length > 0" class="list-container">
      <view
          v-for="(item, index) in list"
          :key="item.id"
          class="item-card animate-enter"
          :style="{ '--delay': index * 0.05 + 's' }"
          @click="goDetail(item)"
      >
        <view class="main-row">
          <image :src="item.coverImage || item.image || (item.images && item.images[0])" mode="aspectFill" class="item-img"></image>
          
          <view class="item-info">
            <view class="info-main">
              <view class="title-row">
                <text class="title u-line-1">{{ item.title }}</text>
                <view class="status-badge" :class="item.status">
                  <text>{{ getStatusText(item.status) }}</text>
                </view>
              </view>
              
              <view class="price-row">
                <text class="currency">¥</text>
                <text class="price">{{ item.price }}</text>
              </view>
              
              <view class="meta-row">
                <text>{{ item.viewCount || 0 }} 浏览</text>
                <text>•</text>
                <text>{{ formatTime(item.createTime || Date.now()) }}</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="action-row">
          <view class="more-btn" @click.stop="openActionSheet(item)">
            <u-icon name="more-dot-fill" size="20" color="#94A3B8"></u-icon>
          </view>
          
          <!-- 违规商品只能删除，不能编辑 -->
          <view v-if="item.status === 'violation'" class="delete-btn" @click.stop="handleDelete(item)">
            <text>删除</text>
            <u-icon name="trash" size="12" color="#EF4444"></u-icon>
          </view>
          <view v-else class="edit-btn" @click.stop="goEdit(item)">
            <text>编辑</text>
            <u-icon name="arrow-right" size="12" color="#6366F1"></u-icon>
          </view>
        </view>
      </view>
      <view style="height: 40px"></view>
    </view>
    <view v-else class="empty-state">
      <u-empty mode="list" text="暂无发布记录" icon-size="100" marginTop="100"></u-empty>
      <u-button
          text="去发布"
          shape="circle"
          type="primary"
          customStyle="width: 140px; margin-top: 20px; background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%); border: none;"
          @click="goPublish"
      ></u-button>
    </view>

    <!-- Action Sheet -->
    <u-action-sheet
        :show="showAction"
        :actions="actionList"
        title="更多操作"
        @close="showAction = false"
        @select="handleActionSelect"
        round="16"
    ></u-action-sheet>
  </scroll-view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { updateMarketItemStatus, deleteMarketItem } from '@/api/market.js'

const props = defineProps({
  list: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['refresh'])

const showAction = ref(false)
const currentItem = ref(null)

// Dynamic actions based on current item status
const actionList = computed(() => {
  if (!currentItem.value) return []
  
  const status = currentItem.value.status
  const isViolation = status === 'violation'
  const isSold = status === 'sold'
  const isActive = status === 'active'
  
  // 违规商品只能删除
  if (isViolation) {
    return [
      {
        name: '删除商品',
        subname: '删除后不可恢复',
        color: '#EF4444',
        fontSize: '16',
        code: 'delete'
      }
    ]
  }
  
  return [
    {
      name: isActive ? '下架商品' : (isSold ? '重新上架' : '上架商品'),
      subname: isActive ? '暂停售卖' : (isSold ? '再次出售' : '恢复售卖'),
      color: isActive ? '#F59E0B' : '#10B981',
      fontSize: '16',
      disabled: false,
      code: 'toggle'
    },
    {
      name: '删除商品',
      subname: '删除后不可恢复',
      color: '#EF4444',
      fontSize: '16',
      code: 'delete'
    },
    {
      name: '分享商品',
      subname: '分享给好友',
      color: '#3B82F6',
      fontSize: '16',
      code: 'share'
    }
  ]
})

function getStatusText(status) {
  switch(status) {
    case 'active': return '出售中'
    case 'sold': return '已售出'
    case 'inactive': return '已下架'
    case 'violation': return '已违规'
    default: return '未知'
  }
}

function formatTime(timestamp) {
  const date = new Date(timestamp)
  return `${date.getMonth()+1}月${date.getDate()}日`
}

function goDetail(item) {
  uni.navigateTo({ url: `/pages/market/detail?id=${item.id}` })
}

function goEdit(item) {
  uni.navigateTo({ url: `/pages/market/edit?id=${item.id}` })
}

function goPublish() {
  uni.navigateTo({ url: `/pages/market/create` })
}

function openActionSheet(item) {
  currentItem.value = item
  showAction.value = true
}

function handleActionSelect(action) {
  if (action.code === 'toggle') {
    toggleStatus(currentItem.value)
  } else if (action.code === 'delete') {
    handleDelete(currentItem.value)
  } else if (action.code === 'share') {
    uni.showToast({ title: '分享功能开发中', icon: 'none' })
  }
  showAction.value = false
}

async function toggleStatus(item) {
  const newStatus = item.status === 'active' ? 'inactive' : 'active'
  try {
    await updateMarketItemStatus(item.id, newStatus)
    item.status = newStatus
    uni.showToast({ title: newStatus === 'active' ? '已上架' : '已下架', icon: 'none' })
    emit('refresh')
  } catch (e) {
    console.error('更新状态失败', e)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

function handleDelete(item) {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个商品吗？删除后无法恢复。',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteMarketItem(item.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          emit('refresh')
        } catch (e) {
          console.error('删除失败', e)
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}
</script>

<style scoped lang="scss">
$primary: #6366F1;
$text-main: #1E293B;
$text-sub: #64748B;

.scroll-content { height: 100%; }
.list-container { padding: 16px 20px; display: flex; flex-direction: column; gap: 16px; }

.item-card {
  background: #fff; border-radius: 20px; padding: 16px;
  display: flex; flex-direction: column; gap: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  border: 1px solid #F1F5F9;
  transition: all 0.2s;
  
  &:active { transform: scale(0.98); }
}

.main-row {
  display: flex; gap: 16px; width: 100%;
}

.item-img { 
  width: 100px; height: 100px; 
  border-radius: 16px; 
  background: #F8FAFC; 
  flex-shrink: 0;
}

.item-info { 
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  justify-content: space-between; 
  padding: 2px 0; 
}

.info-main {
  display: flex; flex-direction: column; gap: 6px;
}

.title-row {
  display: flex; justify-content: space-between; align-items: center;
  .title { font-size: 16px; font-weight: 700; color: $text-main; flex: 1; margin-right: 8px; }
}

.status-badge {
  font-size: 10px; padding: 3px 8px; border-radius: 100px; font-weight: 600;
  &.active { background: #ECFDF5; color: #10B981; }
  &.inactive { background: #F1F5F9; color: #94A3B8; }
  &.sold { background: #EFF6FF; color: #3B82F6; }
  &.violation { background: #FEF2F2; color: #EF4444; }
}

.price-row { 
  color: $primary; font-weight: 800; 
  .currency { font-size: 13px; margin-right: 2px; } 
  .price { font-size: 20px; } 
}

.meta-row {
  display: flex; align-items: center; gap: 6px;
  font-size: 11px; color: #94A3B8;
}

.action-row { 
  display: flex; justify-content: space-between; align-items: center; 
  margin-top: 4px; 
  padding-top: 12px;
  border-top: 1px dashed #F1F5F9;
  width: 100%;
}

.more-btn {
  width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%;
  background: #F8FAFC;
  transition: background 0.2s;
  &:active { background: #E2E8F0; }
}

.edit-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 14px;
  background: #EEF2FF;
  border-radius: 100px;
  
  text { font-size: 13px; font-weight: 600; color: $primary; }
  
  &:active { opacity: 0.8; }
}

.delete-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 14px;
  background: #FEF2F2;
  border-radius: 100px;
  
  text { font-size: 13px; font-weight: 600; color: #EF4444; }
  
  &:active { opacity: 0.8; }
}

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; }
.animate-enter { opacity: 0; transform: translateY(20px); animation: fadeUp 0.5s forwards; animation-delay: var(--delay); }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
</style>
