/**
 * 消息模块 API
 */
import { get, post, put, del } from './request'

/**
 * 获取会话列表
 */
export function getConversations() {
  return get('/api/messages/conversations')
}

/**
 * 获取或创建会话
 * @param {number} otherUserId - 对方用户ID
 */
export function getOrCreateConversation(otherUserId) {
  return get('/api/messages/conversation', { otherUserId })
}

/**
 * 获取聊天记录
 * @param {number} id - 会话ID
 * @param {Object} params - { page, size }
 */
export function getChatMessages(id, params) {
  return get(`/api/messages/conversations/${id}`, params)
}

/**
 * 发送消息
 * @param {Object} data - { receiverId, content, type }
 */
export function sendMessage(data) {
  return post('/api/messages/send', data)
}

/**
 * 标记已读
 * @param {number} conversationId - 会话ID
 */
export function markAsRead(conversationId) {
  return put(`/api/messages/read/${conversationId}`)
}

/**
 * 删除会话
 * @param {number} id - 会话ID
 */
export function deleteConversation(id) {
  return del(`/api/messages/conversations/${id}`)
}

export default {
  getConversations,
  getOrCreateConversation,
  getChatMessages,
  sendMessage,
  markAsRead,
  deleteConversation
}
