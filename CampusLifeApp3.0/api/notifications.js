/**
 * 通知模块 API
 */
import { get, put } from './request'

/**
 * 获取通知列表
 * @param {Object} params - { page, size }
 */
export function getNotifications(params) {
  return get('/api/notifications', params)
}

/**
 * 标记已读
 * @param {number} id - 通知ID
 */
export function markAsRead(id) {
  return put(`/api/notifications/${id}/read`)
}

/**
 * 全部已读
 */
export function markAllAsRead() {
  return put('/api/notifications/read-all')
}

export default {
  getNotifications,
  markAsRead,
  markAllAsRead
}
