<template>
  <view class="page">
    <view class="header">
      <view class="left">
        <view class="icon-bg">
          <u-icon name="kefu-ermai" color="#fff" size="24"></u-icon>
        </view>
        <view>
          <text class="title">Parker AI</text>
          <text class="subtitle">校园助手</text>
        </view>
      </view>
      <u-icon name="close" size="20" color="#fff" @click="goBack"></u-icon>
    </view>

    <scroll-view scroll-y class="chat-content" :scroll-into-view="'msg-' + (chatHistory.length - 1)" scroll-with-animation>
      <view v-for="(msg, index) in chatHistory" :key="index" :id="'msg-' + index">
        <view class="message" :class="[msg.sender]">
          <view class="bubble">{{ msg.text }}</view>
        </view>
      </view>
    </scroll-view>

    <view class="input-bar">
      <u-input v-model="newMessage" placeholder="问我任何问题..." border="none" customStyle="background:#f1f5f9; padding: 12px 16px; border-radius: 999px;" @confirm="sendChat"></u-input>
      <u-button type="primary" shape="circle" @click="sendChat" customStyle="width: 44px; height: 44px; background: linear-gradient(to right, #4f46e5, #7c3aed);">
        <u-icon name="arrow-upward" color="#fff" size="22"></u-icon>
      </u-button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { sendChatMessage, getChatSuggestions } from '@/api/chat.js'

const chatHistory = ref([
  { sender: 'ai', text: '你好！我是 Parker，你的校园智能助手。有什么可以帮你的吗？' }
]);
const newMessage = ref('');
const sessionId = ref('');
const loading = ref(false);
const suggestions = ref([]);

onMounted(async () => {
  // 生成会话ID
  sessionId.value = 'session_' + Date.now()
  
  // 获取推荐问题
  try {
    const res = await getChatSuggestions()
    suggestions.value = Array.isArray(res.data) ? res.data : []
  } catch (err) {
    console.error('获取推荐问题失败:', err)
  }
})

function goBack() {
  uni.navigateBack();
}

async function sendChat() {
  const text = newMessage.value.trim();
  if (!text || loading.value) return;

  chatHistory.value.push({ sender: 'user', text });
  newMessage.value = '';
  loading.value = true;

  try {
    // 调用 AI 聊天 API
    // 请求参数: { message, sessionId }
    // 响应: ChatResponseVO { reply, sessionId }
    const res = await sendChatMessage({
      message: text,
      sessionId: sessionId.value
    })
    
    const data = res.data
    if (data.reply) {
      chatHistory.value.push({ sender: 'ai', text: data.reply });
    }
    // 更新 sessionId（如果服务端返回了新的）
    if (data.sessionId) {
      sessionId.value = data.sessionId
    }
  } catch (err) {
    console.error('发送消息失败:', err)
    chatHistory.value.push({ sender: 'ai', text: '抱歉，我暂时无法回答。请稍后再试。' });
  } finally {
    loading.value = false;
  }
}

function useSuggestion(text) {
  newMessage.value = text
  sendChat()
}
</script>

<style scoped lang="scss">
.page { display: flex; flex-direction: column; height: 100vh; background: #f7f8fa; }

.header {
  background: linear-gradient(90deg, #4f46e5, #6d28d9);
  color: #fff; padding: 16px; display: flex; justify-content: space-between; align-items: center;
  padding-top: calc(16px + var(--status-bar-height));
  .left { display: flex; align-items: center; gap: 12px; }
  .icon-bg { width: 40px; height: 40px; background: rgba(255,255,255,0.2); border-radius: 50%; display: flex; align-items: center; justify-content: center; }
  .title { font-weight: 700; display: block; }
  .subtitle { font-size: 12px; color: rgba(255,255,255,0.8); display: block; }
}

.chat-content { flex: 1; padding: 16px; min-height: 0; }

.message {
  display: flex; margin-bottom: 16px;
  .bubble { padding: 10px 14px; border-radius: 16px; max-width: 80%; font-size: 14px; line-height: 1.5; }
  &.ai { 
    justify-content: flex-start;
    .bubble { background: #fff; color: #1e293b; border-top-left-radius: 4px; }
  }
  &.user {
    justify-content: flex-end;
    .bubble { background: #4f46e5; color: #fff; border-top-right-radius: 4px; }
  }
}

.input-bar {
  padding: 12px 16px; background: #fff; border-top: 1px solid #f1f5f9;
  display: flex; align-items: center; gap: 12px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
}
</style>
