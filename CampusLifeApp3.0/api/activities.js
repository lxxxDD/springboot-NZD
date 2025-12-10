/**
 * 活动模块 API
 */
import { get, post } from './request'

/**
 * 获取活动列表
 * @param {Object} params - { status, page, size }
 */
export function getActivities(params) {
  return get('/api/activities', params)
}

/**
 * 获取活动详情
 * @param {number} id - 活动ID
 */
export function getActivityDetail(id) {
  return get(`/api/activities/${id}`)
}

/**
 * 报名活动
 * @param {number} id - 活动ID
 */
export function registerActivity(id, options = {}) {
  return post(`/api/activities/${id}/register`, {}, options)
}

/**
 * 取消报名
 * @param {number} id - 活动ID
 */
export function cancelActivity(id) {
  return post(`/api/activities/${id}/cancel`)
}

/**
 * 获取我的活动
 */
export function getMyActivities() {
  return get('/api/activities/my')
}

export default {
  getActivities,
  getActivityDetail,
  registerActivity,
  cancelActivity,
  getMyActivities
}
