/**
 * 商品模块 API
 */
import { get, post, put, del } from './request'

/**
 * 获取商品列表
 * @param {Object} params - { page, size, category, keyword, sort }
 */
export function getMarketItems(params) {
  return get('/api/market/items', params)
}

/**
 * 获取商品详情
 * @param {number} id - 商品ID
 */
export function getMarketItemDetail(id) {
  return get(`/api/market/items/${id}`)
}

/**
 * 发布商品
 * @param {Object} data - { title, price, originalPrice, category, conditionLevel, images, description }
 */
export function createMarketItem(data) {
  return post('/api/market/items', data)
}

/**
 * 更新商品
 * @param {number} id - 商品ID
 * @param {Object} data - { title, price, originalPrice, category, conditionLevel, images, description }
 */
export function updateMarketItem(id, data) {
  return put(`/api/market/items/${id}`, data)
}

/**
 * 更新商品状态
 * @param {number} id - 商品ID
 * @param {string} status - 状态: active/inactive/sold
 */
export function updateMarketItemStatus(id, status) {
  return put(`/api/market/items/${id}/status`, { status })
}

/**
 * 删除商品
 * @param {number} id - 商品ID
 */
export function deleteMarketItem(id) {
  return del(`/api/market/items/${id}`)
}

/**
 * 获取我的商品
 * @param {string} status - 状态筛选
 */
export function getMyMarketItems(status) {
  return get('/api/market/my-items', { status })
}

/**
 * 收藏/取消收藏
 * @param {number} id - 商品ID
 */
export function toggleFavorite(id) {
  return post(`/api/market/items/${id}/favorite`)
}

/**
 * 获取商品分类列表
 */
export function getMarketCategories() {
  return get('/api/market/categories')
}

export default {
  getMarketItems,
  getMarketItemDetail,
  createMarketItem,
  updateMarketItem,
  updateMarketItemStatus,
  deleteMarketItem,
  getMyMarketItems,
  toggleFavorite,
  getMarketCategories
}
