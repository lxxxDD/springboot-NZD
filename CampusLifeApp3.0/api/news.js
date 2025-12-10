/**
 * 校园新闻模块 API
 * 注意：此接口需要后端实现，目前使用模拟数据
 */
import { get } from './request'

/**
 * 获取新闻列表
 * @param {Object} params - { page, size, category }
 */
export function getNewsList(params) {
  return get('/api/news', params)
}

/**
 * 获取新闻详情
 * @param {number} id - 新闻ID
 */
export function getNewsDetail(id) {
  return get(`/api/news/${id}`)
}

export default {
  getNewsList,
  getNewsDetail
}
