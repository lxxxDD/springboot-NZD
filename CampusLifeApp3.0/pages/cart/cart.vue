<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar title="购物车" placeholder :autoBack="true" bgColor="transparent" leftIconColor="#1e293b" titleStyle="color: #1e293b; font-weight: 600;">

    </u-navbar>

    <scroll-view scroll-y class="content">
      <view class="header animate-enter" style="--delay: 0s">
        <view class="header-row">
          <text class="title">订单结算</text>
          <view v-if="list.length" class="clear-btn" @click="confirmClear">
            <u-icon name="trash" color="#94A3B8" size="14"></u-icon>
            <text>清空</text>
          </view>
        </view>
        <view class="badge">
          <text>共 {{ totalItems }} 件商品</text>
        </view>
      </view>

      <view v-if="list.length" class="cart-list">
        <u-swipe-action>
          <view
              v-for="(it, index) in list"
              :key="it.id"
              class="swipe-card-wrap animate-slide-in"
              :style="{ animationDelay: index * 0.05 + 's' }"
          >
            <u-swipe-action-item
                :name="it.id"
                :options="swipeOptions"
                :autoClose="true"
                @click="onSwipeClick"
            >
              <view class="cart-item">
                <view class="item-inner">
                  <u-image :src="it.image" width="80px" height="80px" radius="16" mode="aspectFill" class="item-img"></u-image>

                  <view class="item-info">
                    <view class="info-top">
                      <text class="name u-line-1">{{ it.name }}</text>
                      <text class="price">¥{{ it.price.toFixed(2) }}</text>
                    </view>

                    <view class="info-bottom">
                      <text class="specs">默认规格</text>
                      <u-number-box
                          v-model="it.qty"
                          :min="1"
                          :max="it.stock > 0 ? it.stock : 99"
                          :button-size="26"
                          bgColor="#F1F5F9"
                          color="#1E293B"
                          inputWidth="40"
                          @change="val => updateQty(it.id, val.value)"
                      ></u-number-box>
                    </view>
                  </view>
                </view>
              </view>
            </u-swipe-action-item>
          </view>
        </u-swipe-action>
      </view>

      <view v-else class="empty-state animate-enter" style="--delay: 0.1s">
        <u-empty mode="car" text="购物车空空如也" icon-size="100" marginTop="60"></u-empty>
        <u-button
            text="去逛逛"
            shape="circle"
            plain
            type="primary"
            customStyle="width: 120px; margin-top: 20px;"
            @click="goFood"
        ></u-button>
      </view>

      <view style="height: 120px"></view>
    </scroll-view>

    <view class="footer animate-slide-up" v-if="list.length">
      <view class="footer-inner">
        <view class="total-section">
          <text class="total-label">总计</text>
          <view class="total-price">
            <text class="currency">¥</text>{{ total.toFixed(2) }}
          </view>
        </view>

        <view class="action-box">
          <u-button
              type="primary"
              shape="circle"
              text="立即支付"
              @click="checkout"
              customStyle="width: 120px; height: 44px; font-weight: 700; background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%); border: none; box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);"
          ></u-button>
        </view>
      </view>
    </view>

    <u-modal
        :show="showClearModal"
        title="确认清空"
        content="确定要清空购物车中的所有商品吗？"
        showCancelButton
        @confirm="handleClearAll"
        @cancel="showClearModal = false"
        confirmColor="#EF4444"
    ></u-modal>
  </view>
</template>

<script setup>
import { getCart, clearCart, updateItemQuantity, removeItem } from '@/store/cart.js'
import { addOrder } from '@/store/orders.js'
import { ref, computed } from 'vue'

const list = ref(getCart())
const showClearModal = ref(false)

const swipeOptions = [
  {
    text: '删除',
    style: {
      backgroundColor: '#EF4444'
    }
  }
]

function onSwipeClick(e) {
  const { name, index } = e
  if (index === 0) {
    handleDeleteOne(name)
  }
}

function handleDeleteOne(id) {
  removeItem(id)
  list.value = getCart()
  uni.showToast({ title: '已删除', icon: 'none' })
}

const total = computed(() => list.value.reduce((a, b) => a + (b.price * (b.qty || 1)), 0))
const totalItems = computed(() => list.value.reduce((a, b) => a + (b.qty || 1), 0))

function updateQty(id, qty) {
  updateItemQuantity(id, qty)
}

function confirmClear() {
  showClearModal.value = true
}

function handleClearAll() {
  clearCart()
  list.value = []
  showClearModal.value = false
  uni.showToast({ title: '购物车已清空', icon: 'none' })
}

function checkout() {
  if (!list.value.length) return

  // Manual query string construction for WeChat Mini Program compatibility
  // URLSearchParams is not available in Mini Program environment
  const params = {
    type: 'food',
    amount: total.value.toFixed(2),
    items: totalItems.value,
    from: 'cart'
  }
  
  const queryString = Object.keys(params)
    .map(key => `${key}=${encodeURIComponent(params[key])}`)
    .join('&')

  uni.navigateTo({
    url: `/pages/payment/payment?${queryString}`
  })
}

function goFood() {
  uni.reLaunch({ url: '/pages/services/food' })
}
</script>

<style scoped lang="scss">
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;

.page { background: $bg-page; min-height: 100vh; position: relative; overflow: hidden; }

.bg-decoration {
  position: absolute; top: -100px; right: -50px; width: 300px; height: 300px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
}

.clear-btn {
  display: flex; align-items: center; gap: 4px; padding: 6px 12px;
  background: #fff; border-radius: 100px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  text { font-size: 12px; color: $text-sub; font-weight: 500; }
  &:active { opacity: 0.6; background: #F1F5F9; }
}

.content { padding: 0 20px; height: calc(100vh - 44px); position: relative; z-index: 1; }

.header { margin-top: 10px; margin-bottom: 20px; }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.title { font-size: 24px; font-weight: 800; color: $text-main; }
.badge { display: inline-block; background: #F1F5F9; padding: 4px 10px; border-radius: 100px; font-size: 12px; color: $text-sub; font-weight: 600; }

.cart-list { display: flex; flex-direction: column; gap: 16px; }

/* 核心修复方案：
  1. swipe-card-wrap: 作为物理容器，负责裁切所有溢出内容 (overflow: hidden)
  2. cart-item: 负责背景遮挡 (z-index: 10, bg: #fff)
*/
.swipe-card-wrap {
  //border-radius: 20px;
  overflow: hidden; /* 关键：物理裁剪圆角，防止红色底色溢出 */
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.06);
  margin-bottom: 16px;
  background-color: #fff; /* 容器底色 */
  transform: translateZ(0); /* 开启硬件加速，修复部分机型渲染裂缝 */
}

.cart-item {
  background: #fff;
  padding: 16px;
  width: 100%;
  position: relative;
  z-index: 10; /* 关键：层级提升，压住底下的红色按钮 */
  box-sizing: border-box;
}

.item-inner { display: flex; gap: 14px; }
.item-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; padding: 2px 0; }
.info-top { display: flex; justify-content: space-between; align-items: flex-start; .name { font-size: 15px; font-weight: 700; color: $text-main; flex: 1; margin-right: 10px; } .price { font-size: 16px; font-weight: 800; color: $text-main; } }
.info-bottom { display: flex; justify-content: space-between; align-items: center; .specs { font-size: 12px; color: $text-sub; background: #F8FAFC; padding: 2px 6px; border-radius: 4px; } }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; margin-top: 60px; }

/* Footer */
.footer { position: fixed; bottom: 0; left: 0; right: 0; padding: 12px 20px; padding-bottom: calc(12px + env(safe-area-inset-bottom)); z-index: 100; }
.footer-inner { background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(12px); border-radius: 100px; padding: 6px 4px 6px 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08); border: 1px solid rgba(255, 255, 255, 0.5); }
.total-section { display: flex; flex-direction: column; justify-content: center; flex: 1; .total-label { font-size: 10px; color: $text-sub; font-weight: 600; } .total-price { font-size: 20px; font-weight: 800; color: $text-main; line-height: 1.2; } .currency { font-size: 14px; margin-right: 2px; } }

/* Animations */
.animate-enter { opacity: 0; transform: translateY(20px); animation: fadeUp 0.5s forwards; animation-delay: var(--delay); }
.animate-slide-in { opacity: 0; transform: translateX(20px); animation: slideLeft 0.4s forwards; }
.animate-slide-up { transform: translateY(100%); animation: slideUp 0.5s 0.2s forwards; }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideLeft { to { opacity: 1; transform: translateX(0); } }
@keyframes slideUp { to { transform: translateY(0); } }
</style>