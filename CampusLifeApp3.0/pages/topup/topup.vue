<template>
  <view class="page">


    <u-navbar title="钱包充值" :autoBack="true" bgColor="#fff" placeholder></u-navbar>
    <scroll-view scroll-y class="content">
      <view class="balance-card">
        <text class="label">当前余额</text>
        <text class="balance">¥{{ balance.toFixed(2) }}</text>
      </view>

      <view class="section">
        <text class="sec-title">快速选择金额</text>
        <view class="presets">
          <view v-for="a in presetAmounts" :key="a" class="preset-btn" :class="{ active: amount === a }" @click="amount = a">¥{{ a }}</view>
        </view>
      </view>

      <view class="section">
        <text class="sec-title">自定义金额</text>
        <u-input v-model="amount" type="number" placeholder="输入金额（单位：元）" clearable :customStyle="{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: '12px', padding: '0 12px', height: '44px' }" />
      </view>

      <view class="section">
        <text class="sec-title">支付方式</text>
        <view class="payment-methods">
          <view v-for="(item, index) in paymentOptions" :key="index" class="payment-btn" :class="{ active: paymentMethodIndex === index }" @click="paymentMethodIndex = index">
            <u-icon :name="item.icon" size="22" :color="paymentMethodIndex === index ? '#3b82f6' : '#6b7280'"></u-icon>
            <text class="payment-text">{{ item.title }}</text>
          </view>
        </view>
      </view>

      <u-button type="primary" shape="circle" :loading="loading" :disabled="loading" customStyle="height: 50px; width: 100%; font-size: 16px; font-weight: 600;" @click="topup">确认充值</u-button>
      <view style="height:40px" />
    </scroll-view>
  </view>
</template>

<script setup>
import { getUser, setBalance } from '@/store/user.js'
import { reactive, ref, onMounted } from 'vue'
import { topup as topupApi, getBalance as getBalanceApi } from '@/api/user.js'

const u = reactive(getUser())
const balance = ref(u.balance)
const amount = ref(20)
const presetAmounts = [10, 20, 50, 100]
const paymentMethodIndex = ref(0)
const loading = ref(false)

const paymentOptions = ref([
  { id: 0, title: '微信支付', icon: 'weixin-fill' },
  { id: 1, title: '支付宝', icon: 'zhifubao' },
  { id: 2, title: '银行卡', icon: 'coupon-fill' }
])

// 加载当前余额
onMounted(async () => {
  try {
    const res = await getBalanceApi()
    if (res && res.data && res.data.balance !== undefined) {
      balance.value = res.data.balance
    }
  } catch (error) {
    console.error('获取余额失败:', error)
  }
})

async function topup(){
  const a = Number(amount.value||0)
  if (isNaN(a) || a<=0) return uni.showToast({ title:'请输入正确金额', icon:'none' })
  
  if (loading.value) return
  loading.value = true
  
  try {
    const paymentMethod = paymentOptions.value[paymentMethodIndex.value].title
    const res = await topupApi({
      amount: a,
      paymentMethod: paymentMethod
    })
    
    if (res && res.data && res.data.newBalance !== undefined) {
      // 更新本地余额
      setBalance(res.data.newBalance)
      balance.value = res.data.newBalance
      
      uni.showToast({ title:'充值成功', icon:'success' })
    }
  } catch (error) {
    console.error('充值失败:', error)
    uni.showToast({ 
      title: error.message || '充值失败，请重试', 
      icon: 'none' 
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.payment-methods {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.payment-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  height: 48px;
  background: #fff;
  transition: all 0.2s;
}
.payment-btn.active {
  background: #eff6ff;
  border-color: #3b82f6;
}
.payment-text {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
}
.payment-btn.active .payment-text {
  color: #3b82f6;
}
.page { background: #fff; min-height: 100vh; }
.header { position: relative; text-align: center; padding: 16px; border-bottom: 1px solid #f3f4f6; }
.title { font-size: 16px; font-weight: 600; }
.close-btn { position: absolute; right: 16px; top: 50%; transform: translateY(-50%); }

.content { padding: 16px; height: calc(100vh - 57px); box-sizing: border-box; }
.balance-card { text-align: left; background: #1f2937; color: #fff; border-radius: 20px; padding: 20px; margin-bottom: 32px; }
.label { font-size: 13px; color: #9ca3af; }
.balance { font-size: 32px; font-weight: 700; display: block; margin-top: 4px; }

.section { margin-bottom: 28px; }
.sec-title { font-size: 13px; color: #6b7280; margin-bottom: 12px; display: block; }
.presets { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.preset-btn { background: #fff; border: 1px solid #e5e7eb; border-radius: 12px; padding: 12px 0; text-align: center; font-size: 15px; font-weight: 600; color: #111827; }
.preset-btn.active { background: #eff6ff; color: #3b82f6; border-color: #3b82f6; }

.input { background: #fff; border: 1px solid #e5e7eb; border-radius: 12px; padding: 14px; width: 100%; box-sizing: border-box; font-size: 14px; }



.submit-btn { margin-top: 16px; background: #3b82f6; color: #fff; text-align: center; padding: 14px; border-radius: 16px; font-weight: 600; font-size: 15px; box-shadow: 0 8px 24px rgba(59, 130, 246, .3); }
</style>
