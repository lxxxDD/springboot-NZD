<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import SplitText from '@/components/animations/SplitText.vue'
import NumberTicker from '@/components/animations/NumberTicker.vue'
import BlurReveal from '@/components/animations/BlurReveal.vue'
import { getDashboardStats, getRecentLogs, getOrdersTrend, getModuleDistribution, getUserTrend, getRepairStatus } from '@/api'

const router = useRouter()
const chartEl = ref(null)
const pieChartEl = ref(null)
const userChartEl = ref(null)
const repairChartEl = ref(null)
const loading = ref(false)

// 跳转到系统日志页面
const goToLogs = () => {
  router.push('/system/logs')
}

// 刷新所有数据
const refreshAll = async () => {
  loading.value = true
  try {
    await Promise.all([
      fetchStats(),
      fetchLogs(),
      initOrdersChart(),
      initPieChart(),
      initUserChart(),
      initRepairChart()
    ])
    ElMessage.success('数据刷新成功')
  } catch (e) {
    ElMessage.error('刷新失败')
  } finally {
    loading.value = false
  }
}

const stats = ref([
  { title: '注册用户', value: 0, trend: 8.2, isUp: true, icon: 'User', accent: '#6366f1', key: 'userCount' },
  { title: '待处理报修', value: 0, trend: 5.1, isUp: false, icon: 'Tools', accent: '#f59e0b', key: 'pendingRepairs' },
  { title: '今日订单', value: 0, trend: 12.0, isUp: true, icon: 'ShoppingCart', accent: '#06b6d4', key: 'todayOrders' },
  { title: '二手商品', value: 0, trend: 3.5, isUp: true, icon: 'Goods', accent: '#ec4899', key: 'marketItems' }
])

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await getDashboardStats()
    console.log('API返回:', res)
    const data = res.data || res
    console.log('解析数据:', data)
    if (data) {
      stats.value.forEach(stat => {
        if (data[stat.key] !== undefined) {
          stat.value = data[stat.key]
          console.log(`设置 ${stat.key} = ${data[stat.key]}`)
        }
      })
      console.log('更新后stats:', stats.value)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

const logs = ref([])

// 格式化时间为相对时间
const formatRelativeTime = (dateStr) => {
  if (!dateStr) return '未知时间'
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  return `${days}天前`
}

// 获取系统日志
const fetchLogs = async () => {
  try {
    const res = await getRecentLogs(10)
    if (res.data) {
      logs.value = res.data.map(log => ({
        ...log,
        time: formatRelativeTime(log.createTime)
      }))
    }
  } catch (error) {
    console.error('获取日志失败:', error)
    // 如果获取失败，显示默认数据
    logs.value = [
      { id: 1, title: '暂无日志数据', time: '-', type: 'info' }
    ]
  }
}

// 初始化订单趋势图
const initOrdersChart = async () => {
  if (!chartEl.value) return
  const chart = echarts.init(chartEl.value)
  try {
    const res = await getOrdersTrend()
    const data = res.data || res
    chart.setOption({
      grid: { top: 10, right: 10, bottom: 20, left: 40 },
      tooltip: { trigger: 'axis', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#6366f1', textStyle: { color: '#fff' } },
      xAxis: { type: 'category', data: data.dates || [], axisLine: { lineStyle: { color: '#334155' } }, axisLabel: { color: '#94a3b8' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#334155' } }, axisLabel: { color: '#94a3b8' } },
      series: [{ data: data.orders || [], type: 'line', smooth: true, showSymbol: false,
        lineStyle: { color: '#6366f1', width: 4, shadowBlur: 10, shadowColor: 'rgba(99,102,241,0.5)' },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(99,102,241,0.3)' }, { offset: 1, color: 'transparent' }]) }
      }]
    })
  } catch (e) { console.error('订单趋势图加载失败', e) }
  window.addEventListener('resize', () => chart.resize())
}

// 初始化模块分布饼图
const initPieChart = async () => {
  if (!pieChartEl.value) return
  const chart = echarts.init(pieChartEl.value)
  try {
    const res = await getModuleDistribution()
    const data = res.data || res
    chart.setOption({
      tooltip: { trigger: 'item', backgroundColor: 'rgba(0,0,0,0.8)', textStyle: { color: '#fff' } },
      legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { color: '#94a3b8' } },
      series: [{ type: 'pie', radius: ['40%', '70%'], center: ['40%', '50%'], avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#1e293b', borderWidth: 2 },
        label: { show: false }, emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold', color: '#fff' } },
        data: data, color: ['#6366f1', '#f59e0b', '#06b6d4', '#ec4899']
      }]
    })
  } catch (e) { console.error('饼图加载失败', e) }
  window.addEventListener('resize', () => chart.resize())
}

// 初始化用户趋势图
const initUserChart = async () => {
  if (!userChartEl.value) return
  const chart = echarts.init(userChartEl.value)
  try {
    const res = await getUserTrend()
    const data = res.data || res
    chart.setOption({
      grid: { top: 10, right: 10, bottom: 20, left: 40 },
      tooltip: { trigger: 'axis', backgroundColor: 'rgba(0,0,0,0.8)', textStyle: { color: '#fff' } },
      xAxis: { type: 'category', data: data.dates || [], axisLine: { lineStyle: { color: '#334155' } }, axisLabel: { color: '#94a3b8' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#334155' } }, axisLabel: { color: '#94a3b8' } },
      series: [{ data: data.users || [], type: 'bar', barWidth: '60%',
        itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#06b6d4' }, { offset: 1, color: '#0891b2' }]), borderRadius: [4, 4, 0, 0] }
      }]
    })
  } catch (e) { console.error('用户趋势图加载失败', e) }
  window.addEventListener('resize', () => chart.resize())
}

// 初始化报修状态图
const initRepairChart = async () => {
  if (!repairChartEl.value) return
  const chart = echarts.init(repairChartEl.value)
  try {
    const res = await getRepairStatus()
    const data = res.data || res
    chart.setOption({
      tooltip: { trigger: 'item', backgroundColor: 'rgba(0,0,0,0.8)', textStyle: { color: '#fff' } },
      series: [{ type: 'pie', radius: '70%', center: ['50%', '50%'],
        data: data, color: ['#f59e0b', '#3b82f6', '#10b981'],
        label: { color: '#94a3b8' }, emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
      }]
    })
  } catch (e) { console.error('报修状态图加载失败', e) }
  window.addEventListener('resize', () => chart.resize())
}

onMounted(() => {
  fetchStats()
  fetchLogs()
  initOrdersChart()
  initPieChart()
  initUserChart()
  initRepairChart()
})
</script>

<template>
  <div class="p-8 min-h-full">
    <!-- Header -->
    <div class="flex flex-col md:flex-row justify-between items-end mb-10">
      <div>
        <h1 class="text-4xl font-bold text-white mb-2 flex items-center gap-3">
          <SplitText text="控制台" />
          <span class="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-pink-500">
            <SplitText text="Ultra" :delay="0.1" />
          </span>
        </h1>
        <BlurReveal :delay="0.5">
          <p class="text-slate-400">
            系统状态: <span class="text-emerald-400 font-mono">ONLINE</span> · 节点同步率 99.9%
          </p>
        </BlurReveal>
      </div>
      <BlurReveal :delay="0.6">
        <div class="flex gap-4 mt-4 md:mt-0">
          <el-button class="!bg-white/5 !border-white/10 !text-white hover:!bg-white/10" @click="goToLogs">
            系统日志
          </el-button>
          <el-button type="primary" class="btn-glow !rounded-xl" :loading="loading" @click="refreshAll">
            <el-icon class="mr-1"><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </BlurReveal>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <BlurReveal 
        v-for="(stat, index) in stats" 
        :key="index"
        :delay="0.1 * (index + 1)"
      >
        <div class="neon-card p-6 relative group">
          <!-- 动态光晕 -->
          <div 
            class="absolute -top-10 -right-10 w-32 h-32 rounded-full opacity-10 blur-[40px] group-hover:opacity-20 transition-opacity"
            :style="{ backgroundColor: stat.accent }"
          ></div>
          
          <div class="flex justify-between items-start relative z-10">
            <div>
              <p class="text-slate-400 text-xs font-bold tracking-wider uppercase mb-1">{{ stat.title }}</p>
              <h3 class="text-3xl font-bold text-white">
                <NumberTicker :value="stat.value" :prefix="stat.prefix || ''" />
              </h3>
            </div>
            <div 
              class="w-10 h-10 rounded-xl flex items-center justify-center text-white shadow-lg"
              :style="{ background: `linear-gradient(135deg, ${stat.accent}cc, ${stat.accent}33)` }"
            >
              <el-icon size="20"><component :is="stat.icon" /></el-icon>
            </div>
          </div>
          
          <div class="mt-4 flex items-center gap-2 relative z-10">
            <span 
              class="px-2 py-0.5 rounded text-xs font-bold flex items-center"
              :class="stat.isUp ? 'bg-emerald-500/10 text-emerald-400' : 'bg-rose-500/10 text-rose-400'"
            >
              <el-icon class="mr-1"><component :is="stat.isUp ? 'Top' : 'Bottom'" /></el-icon>
              {{ stat.trend }}%
            </span>
            <span class="text-xs text-slate-500">较上周</span>
          </div>
        </div>
      </BlurReveal>
    </div>

    <!-- 图表区第一行 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
      <div class="lg:col-span-2 neon-card p-6">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-lg font-bold text-white">订单趋势</h3>
          <div class="flex gap-2">
            <span class="w-2 h-2 rounded-full bg-indigo-500 animate-pulse"></span>
            <span class="text-xs text-indigo-400">近7天</span>
          </div>
        </div>
        <div ref="chartEl" class="w-full h-80"></div>
      </div>

      <div class="neon-card p-0 flex flex-col">
        <div class="p-6 border-b border-white/5">
          <h3 class="text-lg font-bold text-white">系统日志</h3>
        </div>
        <div class="flex-1 overflow-auto p-4 space-y-3 max-h-72">
          <div 
            v-for="log in logs" 
            :key="log.id" 
            class="flex items-center gap-4 p-3 rounded-xl hover:bg-white/5 transition-colors cursor-pointer group"
          >
            <div 
              class="w-8 h-8 rounded-lg flex items-center justify-center bg-slate-800 border border-white/10 text-xs font-mono text-slate-400 group-hover:text-indigo-400 group-hover:border-indigo-500/50 transition-colors"
            >
              {{ log.id < 10 ? '0' + log.id : log.id }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="text-sm text-slate-200 truncate">{{ log.title }}</div>
              <div class="text-xs text-slate-600">{{ log.time }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区第二行 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
      <div class="neon-card p-6">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-lg font-bold text-white">用户注册趋势</h3>
          <div class="flex gap-2">
            <span class="w-2 h-2 rounded-full bg-cyan-500 animate-pulse"></span>
            <span class="text-xs text-cyan-400">近7天</span>
          </div>
        </div>
        <div ref="userChartEl" class="w-full h-60"></div>
      </div>

      <div class="neon-card p-6">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-lg font-bold text-white">报修状态</h3>
          <div class="flex gap-2">
            <span class="w-2 h-2 rounded-full bg-amber-500 animate-pulse"></span>
            <span class="text-xs text-amber-400">统计</span>
          </div>
        </div>
        <div ref="repairChartEl" class="w-full h-60"></div>
      </div>

      <div class="neon-card p-6">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-lg font-bold text-white">模块分布</h3>
          <div class="flex gap-2">
            <span class="w-2 h-2 rounded-full bg-pink-500 animate-pulse"></span>
            <span class="text-xs text-pink-400">实时</span>
          </div>
        </div>
        <div ref="pieChartEl" class="w-full h-60"></div>
      </div>
    </div>
  </div>
</template>
