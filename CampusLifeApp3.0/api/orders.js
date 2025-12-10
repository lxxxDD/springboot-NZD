/**
 * 订单模块 API
 */
import { get, post, put } from './request'

/**
 * 获取订单列表
 * @param {Object} params - { page, size, type, status }
 */
export function getOrders(params) {
  return get('/api/orders', params)
}

/**
 * 获取订单详情
 * @param {number} id - 订单ID
 */
export function getOrderDetail(id) {
  return get(`/api/orders/${id}`)
}

/**
 * 创建订单
 * @param {Object} data - { itemId, type, amount }
 */
export function createOrder(data) {
  return post('/api/orders', data)
}

/**
 * 支付订单
 * @param {number} id - 订单ID
 * @param {string} paymentMethod - 支付方式
 */
export function payOrder(id, paymentMethod) {
  return post(`/api/orders/${id}/pay`, { paymentMethod })
}

/**
 * 更新订单状态
 * @param {number} id - 订单ID
 * @param {string} status - 状态
 */
export function updateOrderStatus(id, status) {
  return put(`/api/orders/${id}/status`, { status })
}

/**
 * 取消订单
 * @param {number} id - 订单ID
 * @param {string} reason - 取消原因
 */
export function cancelOrder(id, reason) {
  return post(`/api/orders/${id}/cancel`, { reason })
}

export default {
  getOrders,
  getOrderDetail,
  createOrder,
  payOrder,
  updateOrderStatus,
  cancelOrder
}
