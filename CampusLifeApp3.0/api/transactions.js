/**
 * 交易记录模块 API
 */
import { get } from './request'

/**
 * 获取交易记录列表
 * @param {Object} params - { page, size, type }
 */
export function getTransactions(params) {
  return get('/api/transactions', params)
}

export default {
  getTransactions
}
