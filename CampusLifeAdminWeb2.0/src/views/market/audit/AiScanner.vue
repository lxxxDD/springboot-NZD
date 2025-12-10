<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import BlurReveal from '@/components/animations/BlurReveal.vue'

const props = defineProps({
  // 表格数据
  tableData: {
    type: Array,
    default: () => []
  },
  // 解析图片函数
  parseImages: {
    type: Function,
    required: true
  }
})

const emit = defineEmits(['update:scanResults'])

// ==================== 状态管理 ====================
const isScanning = ref(false)
const currentIndex = ref(0)
const totalItems = ref(0)
const scanResults = ref([]) // { itemId, riskStatus, riskLabel, confidence }
const aiAvailable = ref(true) // AI 服务是否可用
const aiModelName = ref('') // 检测到的可用模型名

// 标题危险品关键词定义（中英文）
const DANGEROUS_KEYWORDS = [
  // 刀具类
  '刀', '刀具', '菜刀', '水果刀', '折叠刀', '匕首', 'knife', 'blade',
  // 剪刀类  
  '剪刀', '剪子', 'scissors',
  // 武器类
  '枪', '弓箭', '弩', '武器', 'gun', 'weapon', 'rifle',
  // 危险品
  '炸药', '爆竹', '烟花', '打火机', '火柴',
  // 违禁品
  '毒品', '香烟', '烟草', '电子烟'
]

// ==================== 计算属性 ====================
const statusText = computed(() => {
  if (isScanning.value) return `SYSTEM ANALYZING [${currentIndex.value}/${totalItems.value}]`
  if (scanResults.value.length > 0) {
    const riskyCount = scanResults.value.filter(r => r.riskStatus === 'danger').length
    return riskyCount > 0 
      ? `SCAN COMPLETE: DETECTED ${riskyCount} THREATS` 
      : 'SCAN COMPLETE: SYSTEM SECURE'
  }
  return 'AI GUARD SYSTEM: STANDBY'
})

const canScan = computed(() => {
  return !isScanning.value && props.tableData.length > 0
})

// ==================== 核心函数 (保持逻辑不变) ====================

/**
 * 1. 图片 URL 转 Base64
 */
async function urlToBase64(url) {
  try {
    const response = await fetch(url)
    const blob = await response.blob()
    const img = new Image()
    img.crossOrigin = 'anonymous'
    return new Promise((resolve, reject) => {
      img.onload = () => {
        const maxSize = 512
        let width = img.width
        let height = img.height
        if (width > maxSize || height > maxSize) {
          if (width > height) {
            height = Math.round(height * maxSize / width)
            width = maxSize
          } else {
            width = Math.round(width * maxSize / height)
            height = maxSize
          }
        }
        const canvas = document.createElement('canvas')
        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)
        const base64 = canvas.toDataURL('image/jpeg', 0.8).split(',')[1]
        resolve(base64)
      }
      img.onerror = reject
      img.src = URL.createObjectURL(blob)
    })
  } catch (error) {
    console.error('图片转 Base64 失败:', error)
    throw error
  }
}

/**
 * 2. 调用 Ollama LLaVA 检测图片
 */
async function checkImageWithAi(base64Image) {
  try {
    const modelName = aiModelName.value || 'llava'
    const res = await axios.post('/ollama/api/generate', {
      model: modelName,
      prompt: 'Is there knife, gun, weapon, drug, cigarette? YES or NO only.',
      images: [base64Image],
      stream: false,
      options: { num_predict: 5 }
    }, { timeout: 180000 })
    return res.data.response
  } catch (error) {
    console.error('Ollama AI 调用失败:', error)
    throw error
  }
}

/**
 * 3. 解析 AI 回复
 */
function parseAiResponse(response) {
  const text = response.toUpperCase().trim()
  if (text.startsWith('YES')) {
    const match = response.match(/YES[：:](.*)/i)
    const item = match ? match[1].trim() : '违禁物品'
    return { isDangerous: true, keyword: `AI检测: ${item}`, confidence: 0.9 }
  }
  return { isDangerous: false, keyword: '', confidence: 0 }
}

/**
 * 4. 基于标题关键词检测
 */
function analyzeByKeywords(title, description = '') {
  const text = `${title} ${description}`.toLowerCase()
  for (const keyword of DANGEROUS_KEYWORDS) {
    if (text.includes(keyword.toLowerCase())) {
      return { isDangerous: true, keyword: `标题含"${keyword}"`, confidence: 0.95 }
    }
  }
  return { isDangerous: false, keyword: '', confidence: 0 }
}

/**
 * 5. 检测 Ollama 服务是否可用
 */
async function checkOllamaAvailable() {
  try {
    const res = await axios.get('/ollama/api/tags', { timeout: 5000 })
    const models = res.data?.models || []
    return models.some(m => m.name.includes('llava'))
  } catch (error) {
    return false
  }
}

/**
 * 6. 主扫描流程
 */
async function handleScan() {
  if (!canScan.value) return
  
  isScanning.value = true
  scanResults.value = []
  currentIndex.value = 0
  totalItems.value = props.tableData.length
  
  ElMessage.info('正在启动智能巡检协议...')
  
  // 逐个扫描
  for (let i = 0; i < props.tableData.length; i++) {
    currentIndex.value = i + 1
    const item = props.tableData[i]
    let result = { isDangerous: false, keyword: '', confidence: 0, source: '' }
    
    // 仅检测标题关键词
    const titleResult = analyzeByKeywords(item.title || '', item.description || '')
    if (titleResult.isDangerous) {
      result = { ...titleResult, source: 'title' }
    }
    
    scanResults.value.push({
      itemId: item.id,
      itemTitle: item.title,
      riskStatus: result.isDangerous ? 'danger' : 'safe',
      riskLabel: result.keyword,
      confidence: result.confidence,
      source: result.source
    })
    
    // 模拟一点延迟以展示动画效果
    if (props.tableData.length < 50) await new Promise(r => setTimeout(r, 50))
  }
  
  emit('update:scanResults', scanResults.value)
  
  const riskyCount = scanResults.value.filter(r => r.riskStatus === 'danger').length
  if (riskyCount > 0) {
    ElMessage.warning(`扫描完成，拦截 ${riskyCount} 个潜在威胁`)
  } else {
    ElMessage.success('扫描完成，系统安全')
  }
  
  isScanning.value = false
}

function getItemResult(itemId) {
  return scanResults.value.find(r => r.itemId === itemId)
}

function clearResults() {
  scanResults.value = []
  currentIndex.value = 0
  totalItems.value = 0
  emit('update:scanResults', [])
}

defineExpose({ handleScan, getItemResult, clearResults, scanResults })
</script>

<template>
  <BlurReveal :delay="0.1">
    <div class="ai-scanner relative overflow-hidden">
      <!-- 动态扫描光效背景 -->
      <div v-if="isScanning" class="absolute inset-0 bg-gradient-to-r from-transparent via-indigo-500/10 to-transparent scan-line-anim pointer-events-none z-0"></div>

      <!-- 顶部控制栏 -->
      <div class="flex flex-col md:flex-row justify-between items-center gap-6 relative z-10">
        
        <!-- 左侧：状态显示 -->
        <div class="flex items-center gap-4 flex-1">
          <div class="relative">
            <div 
              class="w-12 h-12 rounded-xl flex items-center justify-center border border-white/10 shadow-[0_0_15px_rgba(99,102,241,0.3)] bg-slate-900/50"
              :class="{ 'animate-pulse border-indigo-500': isScanning }"
            >
              <el-icon size="24" :class="isScanning ? 'text-indigo-400' : 'text-slate-400'">
                <Monitor />
              </el-icon>
            </div>
            <!-- 状态指示灯 -->
            <div class="absolute -top-1 -right-1 flex h-3 w-3">
              <span v-if="isScanning" class="animate-ping absolute inline-flex h-full w-full rounded-full bg-indigo-400 opacity-75"></span>
              <span class="relative inline-flex rounded-full h-3 w-3" :class="isScanning ? 'bg-indigo-500' : (scanResults.length > 0 ? 'bg-emerald-500' : 'bg-slate-500')"></span>
            </div>
          </div>

          <div>
            <h3 class="text-white font-bold text-lg tracking-wide font-mono">AI GUARD <span class="text-indigo-400 text-xs align-top">PRO</span></h3>
            <p class="text-xs font-mono font-medium" 
               :class="[
                 isScanning ? 'text-indigo-300' : '',
                 !isScanning && scanResults.some(r => r.riskStatus === 'danger') ? 'text-rose-400' : 'text-slate-500'
               ]">
              {{ statusText }}
            </p>
          </div>
        </div>

        <!-- 右侧：操作按钮 -->
        <div class="flex gap-3">
          <el-button 
            type="primary" 
            :loading="isScanning"
            :disabled="!canScan"
            @click="handleScan"
            class="btn-glow !rounded-lg !h-10 !px-6 !font-bold"
          >
            {{ isScanning ? 'ANALYZING...' : 'START SCAN' }}
          </el-button>
          
          <el-button 
            v-if="scanResults.length > 0" 
            type="info" 
            plain 
            @click="clearResults"
            class="!bg-white/5 !border-white/10 !text-slate-300 hover:!bg-white/10"
          >
            CLEAR
          </el-button>
        </div>
      </div>
      
      <!-- 进度条区域 -->
      <div v-if="isScanning || scanResults.length > 0" class="mt-6 space-y-2 relative z-10">
        <div class="flex justify-between text-xs font-mono text-slate-400 px-1">
          <span>PROGRESS</span>
          <span>{{ Math.round((currentIndex / totalItems) * 100) }}%</span>
        </div>
        <div class="h-2 bg-slate-800 rounded-full overflow-hidden border border-white/5">
          <div 
            class="h-full bg-gradient-to-r from-indigo-600 via-purple-500 to-indigo-400 transition-all duration-300 ease-out relative"
            :style="{ width: `${(currentIndex / totalItems) * 100}%` }"
          >
            <div class="absolute inset-0 bg-white/20 w-full h-full animate-[shimmer_2s_infinite]"></div>
          </div>
        </div>
      </div>
      
      <!-- 扫描结果仪表盘 -->
      <div v-if="scanResults.length > 0 && !isScanning" class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4 relative z-10">
        <!-- 安全 -->
        <div class="result-card bg-emerald-500/5 border-emerald-500/20">
          <div class="text-emerald-400 text-xs font-bold uppercase tracking-wider mb-1">Pass</div>
          <div class="text-2xl font-bold text-white font-mono">{{ scanResults.filter(r => r.riskStatus === 'safe').length }}</div>
        </div>
        <!-- 违规 -->
        <div class="result-card bg-rose-500/5 border-rose-500/20 shadow-[0_0_15px_rgba(244,63,94,0.1)]">
          <div class="text-rose-400 text-xs font-bold uppercase tracking-wider mb-1">Threats</div>
          <div class="text-2xl font-bold text-white font-mono">{{ scanResults.filter(r => r.riskStatus === 'danger').length }}</div>
        </div>
        <!-- 异常 -->
        <div class="result-card bg-amber-500/5 border-amber-500/20">
          <div class="text-amber-400 text-xs font-bold uppercase tracking-wider mb-1">Errors</div>
          <div class="text-2xl font-bold text-white font-mono">{{ scanResults.filter(r => r.riskStatus === 'error').length }}</div>
        </div>
      </div>
      
      <!-- 违规详情列表 -->
      <div v-if="scanResults.some(r => r.riskStatus === 'danger') && !isScanning" class="mt-4 relative z-10">
        <div class="bg-rose-950/20 border border-rose-500/30 rounded-lg p-4 backdrop-blur-md">
          <div class="flex items-center gap-2 mb-3 text-rose-400 font-bold text-sm uppercase tracking-wider">
            <el-icon><Warning /></el-icon>
            Threat Detected Log
          </div>
          <div class="space-y-2 max-h-40 overflow-y-auto custom-scrollbar">
            <div 
              v-for="item in scanResults.filter(r => r.riskStatus === 'danger')" 
              :key="item.itemId"
              class="flex items-center gap-3 p-2 rounded bg-rose-500/10 border border-rose-500/10 hover:border-rose-500/30 transition-colors"
            >
              <span class="text-xs font-mono text-rose-300 bg-rose-950/50 px-2 py-0.5 rounded">#{{ item.itemId }}</span>
              <span class="flex-1 text-sm text-slate-200 truncate">{{ item.itemTitle }}</span>
              <span class="text-xs text-rose-300 font-mono border border-rose-500/30 px-2 py-0.5 rounded uppercase">
                {{ item.riskLabel }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </BlurReveal>
</template>

<style scoped>
.ai-scanner {
  padding: 24px;
  background: rgba(17, 24, 39, 0.6); /* Glass dark */
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.2);
}

.result-card {
  border-width: 1px;
  border-style: solid;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.result-card:hover {
  transform: translateY(-2px);
}

.scan-line-anim {
  animation: scan 2s linear infinite;
  width: 50%;
  opacity: 0.5;
  filter: blur(10px);
}

@keyframes scan {
  0% { transform: translateX(-150%) skewX(-15deg); }
  100% { transform: translateX(250%) skewX(-15deg); }
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(0,0,0,0.1);
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(244, 63, 94, 0.3);
  border-radius: 4px;
}
</style>