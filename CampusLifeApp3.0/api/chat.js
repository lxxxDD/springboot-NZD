/**
 * AI助手模块 API
 */
import { get, post, del } from './request'

/**
 * 发送消息获取AI回复
 * @param {Object} data - { message, sessionId }
 */
export function sendChatMessage(data) {
  return post('/api/chat/send', data)
}

/**
 * 获取推荐问题
 */
export function getChatSuggestions() {
  return get('/api/chat/suggestions')
}

/**
 * 获取会话列表
 */
export function getChatSessions() {
  return get('/api/chat/sessions')
}

/**
 * 获取聊天历史
 * @param {Object} params - { sessionId, page, size }
 */
export function getChatHistory(params) {
  return get('/api/chat/history', params)
}

/**
 * 清除会话历史
 * @param {string} sessionId - 会话ID
 */
export function clearChatHistory(sessionId) {
  return del(`/api/chat/history/${sessionId}`)
}

export default {
  sendChatMessage,
  getChatSuggestions,
  getChatSessions,
  getChatHistory,
  clearChatHistory
}
