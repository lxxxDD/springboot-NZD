// 消息存储模块
const CONVERSATIONS_KEY = 'campus_conversations'
const MESSAGES_KEY = 'campus_messages'

// 初始化默认会话数据
const defaultConversations = [
  {
    id: 1,
    name: 'Alex Tan',
    avatar: 'https://via.placeholder.com/100/3b82f6/fff',
    lastMsg: '好的，那我们下午两点在图书馆见？',
    time: '10:30',
    unread: 2,
    online: true
  },
  {
    id: 2,
    name: '二手书交易小助手',
    avatar: 'https://via.placeholder.com/100/10b981/fff',
    lastMsg: '[系统消息] 您的《高等数学》已售出！',
    time: '昨天',
    unread: 0,
    online: false
  },
  {
    id: 3,
    name: 'Sarah Lee',
    avatar: 'https://via.placeholder.com/100/f59e0b/fff',
    lastMsg: '你可以发我一下那张活动海报吗？',
    time: '星期一',
    unread: 5,
    online: true
  }
]

// 初始化默认消息数据
const defaultMessages = {
  1: [
    { id: 1, content: '你好！请问那本《算法导论》还在吗？', isMe: false, time: '10:23' },
    { id: 2, content: '在的，九成新，笔记不多。', isMe: true, time: '10:25' },
    { id: 3, content: '太好了！方便在图书馆面交吗？', isMe: false, time: '10:28' },
    { id: 4, content: '好的，那我们下午两点在图书馆见？', isMe: false, time: '10:30' }
  ],
  2: [
    { id: 1, content: '[系统消息] 您的《高等数学》已售出！买家：张三', isMe: false, time: '昨天 14:30' }
  ],
  3: [
    { id: 1, content: '你可以发我一下那张活动海报吗？', isMe: false, time: '星期一 09:15' }
  ]
}

// 获取会话列表
export function getConversations() {
  try {
    const data = uni.getStorageSync(CONVERSATIONS_KEY)
    return data && data.length > 0 ? data : defaultConversations
  } catch (e) {
    return defaultConversations
  }
}

// 保存会话列表
export function setConversations(list) {
  try {
    uni.setStorageSync(CONVERSATIONS_KEY, list)
  } catch (e) {}
}

// 获取某个会话的消息
export function getMessages(conversationId) {
  try {
    const allMessages = uni.getStorageSync(MESSAGES_KEY) || defaultMessages
    return allMessages[conversationId] || []
  } catch (e) {
    return defaultMessages[conversationId] || []
  }
}

// 发送消息
export function sendMessage(conversationId, content) {
  try {
    const allMessages = uni.getStorageSync(MESSAGES_KEY) || { ...defaultMessages }
    
    if (!allMessages[conversationId]) {
      allMessages[conversationId] = []
    }
    
    const newMessage = {
      id: Date.now(),
      content,
      isMe: true,
      time: formatTime(new Date())
    }
    
    allMessages[conversationId].push(newMessage)
    uni.setStorageSync(MESSAGES_KEY, allMessages)
    
    // 更新会话列表的最后一条消息
    const conversations = getConversations()
    const conv = conversations.find(c => c.id === conversationId)
    if (conv) {
      conv.lastMsg = content
      conv.time = '刚刚'
      // 将该会话移到最前面
      const idx = conversations.indexOf(conv)
      conversations.splice(idx, 1)
      conversations.unshift(conv)
      setConversations(conversations)
    }
    
    return newMessage
  } catch (e) {
    return null
  }
}

// 创建或获取会话
export function getOrCreateConversation(userId, userName, userAvatar) {
  const conversations = getConversations()
  
  // 查找是否已有会话
  let conv = conversations.find(c => c.id === userId || c.name === userName)
  
  if (!conv) {
    // 创建新会话
    conv = {
      id: userId || Date.now(),
      name: userName,
      avatar: userAvatar || 'https://via.placeholder.com/100/6366f1/fff',
      lastMsg: '',
      time: '刚刚',
      unread: 0,
      online: false
    }
    conversations.unshift(conv)
    setConversations(conversations)
  }
  
  return conv
}

// 标记会话已读
export function markAsRead(conversationId) {
  const conversations = getConversations()
  const conv = conversations.find(c => c.id === conversationId)
  if (conv) {
    conv.unread = 0
    setConversations(conversations)
  }
}

// 标记全部已读
export function markAllAsRead() {
  const conversations = getConversations()
  conversations.forEach(c => c.unread = 0)
  setConversations(conversations)
}

// 删除会话
export function deleteConversation(conversationId) {
  const conversations = getConversations()
  const newList = conversations.filter(c => c.id !== conversationId)
  setConversations(newList)
  
  // 同时删除消息
  try {
    const allMessages = uni.getStorageSync(MESSAGES_KEY) || {}
    delete allMessages[conversationId]
    uni.setStorageSync(MESSAGES_KEY, allMessages)
  } catch (e) {}
}

// 格式化时间
function formatTime(date) {
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 获取未读消息总数
export function getTotalUnread() {
  const conversations = getConversations()
  return conversations.reduce((sum, c) => sum + (c.unread || 0), 0)
}
