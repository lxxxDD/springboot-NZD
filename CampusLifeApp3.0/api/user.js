/**
 * 用户模块 API
 */
import { get, post, put } from './request'

/**
 * 用户登录
 * @param {Object} data - { studentId, password }
 */
export function login(data) {
  return post('/api/user/login', data)
}

/**
 * 用户注册
 * @param {Object} data - { studentId, username, password, email }
 */
export function register(data) {
  return post('/api/user/register', data)
}

/**
 * 获取用户信息
 */
export function getUserProfile() {
  return get('/api/user/profile')
}

/**
 * 更新用户信息
 * @param {Object} data - { username, avatar, phone }
 */
export function updateProfile(data) {
  return put('/api/user/profile', data)
}

/**
 * 获取余额
 */
export function getBalance() {
  return get('/api/user/balance')
}

/**
 * 充值
 * @param {Object} data - { amount, paymentMethod }
 */
export function topup(data) {
  return post('/api/user/topup', data)
}

/**
 * 获取用户在线状态
 * @param {string} userId
 */
export function getUserStatus(userId) {
  return get(`/api/user/status/${userId}`)
}

/**
 * 获取其他用户的公开信息
 * @param {number} userId - 用户ID
 */
export function getUserById(userId) {
  return get(`/api/user/${userId}`)
}

/**
 * 获取用户发布的商品列表
 * @param {number} userId - 用户ID
 */
export function getUserMarketItems(userId) {
  return get(`/api/market/items`, { userId, status: 'active' })
}

export default {
  login,
  register,
  getUserProfile,
  updateProfile,
  getBalance,
  topup,
  getUserStatus,
  getUserById,
  getUserMarketItems
}
