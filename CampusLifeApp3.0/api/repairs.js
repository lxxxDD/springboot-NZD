/**
 * 报修模块 API
 */
import { get, post } from './request'

/**
 * 获取报修列表
 * @param {string} status - 状态筛选
 */
export function getRepairs(status) {
  // 只有status有值时才传参数
  return get('/api/repairs', status ? { status } : {})
}

/**
 * 获取报修详情
 * @param {number} id - 报修ID
 */
export function getRepairDetail(id) {
  return get(`/api/repairs/${id}`)
}

/**
 * 提交报修
 * @param {Object} data - { location, issueType, description, images }
 */
export function createRepair(data) {
  return post('/api/repairs', data)
}

/**
 * 评价报修
 * @param {number} id - 报修ID
 * @param {Object} data - { rating, feedback }
 */
export function rateRepair(id, data) {
  return post(`/api/repairs/${id}/rate`, data)
}

export default {
  getRepairs,
  getRepairDetail,
  createRepair,
  rateRepair
}
