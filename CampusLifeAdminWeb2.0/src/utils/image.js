import { BASE_URL } from '@/api/request'

/**
 * 获取完整的图片URL
 * 如果已经是完整URL（http/https开头）则直接返回，否则拼接BASE_URL
 * @param {String} url - 图片路径或完整URL
 * @returns {String} 完整的图片URL
 */
export function getFullImageUrl(url) {
  if (!url) return ''
  // 已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 拼接BASE_URL
  return `${BASE_URL}${url.startsWith('/') ? '' : '/'}${url}`
}
