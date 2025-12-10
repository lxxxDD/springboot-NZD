<template>
  <view class="page">
    <u-navbar title="交易记录" :autoBack="true" bgColor="#f5f5f5" placeholder></u-navbar>
    <scroll-view scroll-y class="content">
      <view v-for="(tx, i) in transactions" :key="i" class="tx-card">
        <view class="left">
          <view class="icon-wrapper" :class="tx.type">
            <text class="material-symbols-outlined">{{ tx.icon }}</text>
          </view>
          <view>
            <text class="tx-title">{{ tx.title }}</text>
            <text class="tx-time">{{ tx.time }}</text>
          </view>
        </view>
        <text class="amount" :class="tx.type">{{ tx.amount }}</text>
      </view>
      <view style="height:40px" />
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTransactions } from '@/api/transactions.js'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const transactions = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)

onMounted(() => {
  loadTransactions()
})

async function loadTransactions() {
  if (loading.value) return
  
  loading.value = true
  try {
    const res = await getTransactions({ page: page.value, size: pageSize.value })
    transactions.value = res.data.list.map(item => ({
      type: parseType(item.type, item.amount),
      icon: getIcon(item.type),
      title: item.description || getTypeLabel(item.type),
      time: dayjs(item.createTime).fromNow(),
      amount: formatAmount(item.amount)
    }))
  } catch (err) {
    console.error('加载交易记录失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function parseType(type, amount) {
  return amount > 0 ? 'income' : 'expense'
}

function getIcon(type) {
  const iconMap = {
    recharge: 'add_card',
    payment: 'shopping_cart',
    income: 'account_balance_wallet',
    food_payment: 'restaurant',
    withdraw: 'remove_circle'
  }
  return iconMap[type] || 'payment'
}

function getTypeLabel(type) {
  const labelMap = {
    recharge: '钱包充值',
    payment: '二手市场支付',
    income: '二手商品收入',
    food_payment: '食堂订单支付',
    withdraw: '提现'
  }
  return labelMap[type] || '交易'
}

function formatAmount(amount) {
  const num = parseFloat(amount)
  return num >= 0 ? `+¥${num.toFixed(2)}` : `-¥${Math.abs(num).toFixed(2)}`
}
</script>

<style scoped lang="scss">
.page { background: #f5f5f5; min-height: 100vh; }
.content { padding: 12px 16px; height: calc(100vh - 44px); box-sizing: border-box; }
.tx-card { display: flex; justify-content: space-between; align-items: center; background: #fff; border-radius: 12px; padding: 12px; margin-bottom: 10px; }
.left { display: flex; align-items: center; gap: 12px; }
.icon-wrapper { width: 40px; height: 40px; border-radius: 999px; display: flex; align-items: center; justify-content: center; }
.icon-wrapper.expense { background: #fee2e2; color: #ef4444; }
.icon-wrapper.income { background: #dcfce7; color: #22c55e; }
.tx-title { font-size: 14px; font-weight: 600; color: #111827; display: block; }
.tx-time { font-size: 12px; color: #6b7280; }
.amount { font-size: 16px; font-weight: 700; }
.amount.expense { color: #ef4444; }
.amount.income { color: #22c55e; }
</style>
