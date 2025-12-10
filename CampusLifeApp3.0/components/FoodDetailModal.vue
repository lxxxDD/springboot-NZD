<template>
  <u-popup :show="visible" @close="closeModal" mode="bottom" round="24">
    <view class="modal-content" v-if="item">
      <view class="header-image">
        <up-image :src="item.image || '/static/images/food/default.jpg'" class="image" width="100%" height="100%" mode="aspectFill"></up-image>
        <view class="close-btn" @click="closeModal">
          <u-icon name="close" size="20" color="#1e293b"></u-icon>
        </view>
        <!-- 状态标签 -->
        <view class="status-tag" :class="item.status" v-if="item.status && item.status !== 'available'">
          {{ item.status === 'soldout' ? '已售罄' : '已下架' }}
        </view>
      </view>

      <scroll-view scroll-y class="details">
        <view class="p-6">
          <view class="flex-between mb-2">
            <text class="name">{{ item.name }}</text>
            <view class="price-box">
              <text class="price">¥{{ (item.price || 0).toFixed(2) }}</text>
              <text class="original-price" v-if="item.originalPrice && item.originalPrice > item.price">¥{{ item.originalPrice.toFixed(2) }}</text>
            </view>
          </view>
          
          <!-- 评分和销量 -->
          <view class="stats-row mb-3">
            <view class="stat-item" v-if="item.rating">
              <u-icon name="star-fill" size="14" color="#F59E0B"></u-icon>
              <text class="stat-value">{{ item.rating.toFixed(1) }}</text>
            </view>
            <view class="stat-item" v-if="item.monthlySales">
              <text class="stat-label">月售</text>
              <text class="stat-value">{{ item.monthlySales }}</text>
            </view>
            <view class="stat-item" v-if="item.stock !== undefined && item.stock !== -1">
              <text class="stat-label">库存</text>
              <text class="stat-value" :class="{ 'low-stock': item.stock < 10 }">{{ item.stock }}</text>
            </view>
          </view>
          
          <!-- 标签 -->
          <view class="tags mb-4" v-if="item.tags || item.category">
            <text class="tag" v-if="item.category">{{ item.category }}</text>
            <text class="tag highlight" v-for="tag in parseTags(item.tags)" :key="tag">{{ tag }}</text>
          </view>
          
          <!-- 描述 -->
          <view class="desc-section mb-6" v-if="item.description">
            <h3 class="section-title">菜品介绍</h3>
            <p class="desc">{{ item.description }}</p>
          </view>
        </view>
      </scroll-view>

      <view class="footer">
        <view class="qty-box">
          <up-number-box v-model="quantity" :min="1" :max="item.stock === -1 ? 99 : (item.stock || 99)" button-size="36"
                         disabledInput
                         color="#1A1A1A"
                         bgColor="#f1f1f1"
                         iconStyle="color: #1A1A1A"
          ></up-number-box>
        </view>
        <u-button 
          @click="addToCart" 
          :text="item.status === 'soldout' ? '已售罄' : '加入订单'" 
          :disabled="item.status === 'soldout' || item.status === 'offline'"
          :customStyle="item.status === 'soldout' || item.status === 'offline' ? 'flex:1;background:#cbd5e1;color:#fff;height:48px;border-radius:12px;' : 'flex:1;background:#6366F1;color:#fff;height:48px;border-radius:12px;box-shadow:0 8px 16px rgba(99,102,241,.25);'"
        ></u-button>
      </view>
    </view>
  </u-popup>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: Boolean,
  item: Object
})

const emit = defineEmits(['close', 'add-to-cart'])

const quantity = ref(1)

watch(() => props.visible, (newVal) => {
  if (newVal) {
    quantity.value = 1
  }
})

function closeModal() {
  emit('close')
}

// 解析标签字符串
function parseTags(tagsStr) {
  if (!tagsStr) return []
  return tagsStr.split(',').map(t => t.trim()).filter(t => t)
}



function addToCart() {
  emit('add-to-cart', { item: props.item, quantity: quantity.value })
  closeModal()
}
</script>

<style scoped lang="scss">
.modal-content {
  background-color: white;
  height: 85vh;
  display: flex;
  flex-direction: column;
}

.header-image {
  height: 240px;
  position: relative;
  flex-shrink: 0;
  .image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-top-left-radius: 24px;
    border-top-right-radius: 24px;
  }
  .close-btn {
    position: absolute;
    top: 16px;
    right: 16px;
    padding: 8px;
    background-color: rgba(255, 255, 255, 0.5);
    backdrop-filter: blur(4px);
    border-radius: 999px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #1e293b;
  }
}

.status-tag {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  
  &.soldout {
    background: rgba(239, 68, 68, 0.9);
    color: #fff;
  }
  
  &.offline {
    background: rgba(100, 116, 139, 0.9);
    color: #fff;
  }
}

.details {
  flex: 1;
  overflow-y: auto;
  .p-6 { padding: 24px; }
  .flex-between { display: flex; justify-content: space-between; align-items: flex-start; }
  .mb-2 { margin-bottom: 8px; }
  .mb-3 { margin-bottom: 12px; }
  .mb-4 { margin-bottom: 16px; }
  .mb-6 { margin-bottom: 24px; }
  .name { font-size: 22px; font-weight: 800; color: #1e293b; flex: 1; margin-right: 12px; }
  
  .price-box {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
  }
  .price { font-size: 20px; font-weight: 800; color: #6366F1; }
  .original-price {
    font-size: 13px;
    color: #94a3b8;
    text-decoration: line-through;
    margin-top: 2px;
  }
  
  .stats-row {
    display: flex;
    gap: 16px;
    align-items: center;
  }
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
  }
  
  .stat-label {
    font-size: 12px;
    color: #94a3b8;
  }
  
  .stat-value {
    font-size: 13px;
    font-weight: 600;
    color: #1e293b;
    
    &.low-stock {
      color: #ef4444;
    }
  }
  
  .tags { display: flex; gap: 8px; flex-wrap: wrap; }
  .tag {
    padding: 4px 10px;
    background-color: #f1f5f9;
    color: #64748b;
    font-size: 12px;
    border-radius: 6px;
    
    &.highlight {
      background-color: #fef3c7;
      color: #d97706;
    }
  }
  
  .desc-section {
    background: #f8fafc;
    padding: 16px;
    border-radius: 12px;
  }
  
  .desc { color: #475569; font-size: 14px; line-height: 1.7; }
  .section-title { font-weight: 700; font-size: 14px; margin-bottom: 8px; color: #1e293b; }
}

.footer {
  padding: 16px;
  border-top: 1px solid #f1f5f9;
  background-color: white;
  padding-bottom: calc(16px + constant(safe-area-inset-bottom));
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  display: flex;
  gap: 16px;
  align-items: center;
}



.add-to-cart-btn {
  flex: 1;
  background-color: #2563eb;
  color: white;
  padding: 14px 0;
  border-radius: 12px;
  font-weight: 700;
  text-align: center;
  box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2), 0 2px 4px -2px rgba(37, 99, 235, 0.2);
}
</style>
