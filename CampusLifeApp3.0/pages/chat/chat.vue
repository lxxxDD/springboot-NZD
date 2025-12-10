<template>
  <view class="page">
    <view class="chat">
      <scroll-view scroll-y class="list" :scroll-into-view="toView" scroll-with-animation>
        <view v-for="(m,i) in msgs" :key="i" :id="'m'+i" class="row" :class="m.sender">
          <view class="bubble">{{ m.text }}</view>
        </view>
        <view :id="'bottom'" style="height:1px" />
      </scroll-view>
      <view class="inputbar">
        <view class="plus"><text class="material-symbols-outlined">add_circle</text></view>
        <input v-model="text" class="ipt" placeholder="问我图书馆、食堂、报修、活动等任何问题..." @confirm="send" confirm-type="send" />
        <view class="send" @click="send"><text class="material-symbols-outlined">send</text></view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { sendChatMessage } from '@/api/chat.js'
import { ref, nextTick } from 'vue'

const userInfo = uni.getStorageSync('userInfo') || {}
const userName = userInfo.username || 'Alex'

const msgs = ref([
  { sender:'ai', text: `Hi ${userName}! I'm Parker, your campus assistant. How can I help you today?` }
])
const text = ref('')
const toView = ref('bottom')
const sessionId = ref('')
const loading = ref(false)

async function send(){
  const t = (text.value||'').trim()
  if (!t || loading.value) return
  
  msgs.value.push({ sender:'me', text: t })
  text.value = ''
  nextTick(()=>{ toView.value = 'm'+(msgs.value.length-1) })
  
  // 显示加载状态
  loading.value = true
  msgs.value.push({ sender:'ai', text: '正在思考中...' })
  const loadingIndex = msgs.value.length - 1
  nextTick(()=>{ toView.value = 'm'+loadingIndex })
  
  try {
    const res = await sendChatMessage({
      message: t,
      sessionId: sessionId.value || undefined
    })
    
    // 保存 sessionId
    if (res.data.sessionId) {
      sessionId.value = res.data.sessionId
    }
    
    // 替换加载消息为真实回复
    msgs.value[loadingIndex] = { sender:'ai', text: res.data.reply }
    nextTick(()=>{ toView.value = 'm'+loadingIndex })
  } catch (err) {
    console.error('发送消息失败:', err)
    msgs.value[loadingIndex] = { sender:'ai', text: '抱歉，我暂时无法回复，请稍后再试。' }
  } finally {
    loading.value = false
  }
}
</script>
<style scoped lang="scss">
.page{ background:#f5f5f5; min-height:100vh }
.chat{ height: calc(100vh - 56px); display:flex; flex-direction:column }
.list{ flex:1; padding: 12px 16px; display:flex; flex-direction:column; gap:10px }
.row{ display:flex }
.row.me{ justify-content:flex-end }
.bubble{ max-width: 76%; padding:10px 12px; border-radius:12px; font-size:14px; line-height:1.4; box-shadow:0 1px 4px rgba(0,0,0,.06) }
.row.ai .bubble{ background:#f1f5f9; color:#0f172a; border-top-left-radius:4px }
.row.me .bubble{ background:#2563eb; color:#fff; border-top-right-radius:4px }
.inputbar{ display:flex; align-items:center; gap:8px; padding:8px; background:#fff; border-top:1px solid #e5e7eb }
.plus{ color:#9ca3af }
.ipt{ flex:1; background:#f3f4f6; border-radius:999px; padding:10px 14px }
.send{ width:40px; height:40px; border-radius:999px; display:flex; align-items:center; justify-content:center; background:#2563eb; color:#fff }
</style>

