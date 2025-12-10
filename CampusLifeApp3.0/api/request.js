/**
 * 统一请求封装
 * 基于 uni.request 封装，支持 Token 自动携带、错误处理、登录过期跳转
 */

export const baseURL = 'http://localhost:8080'

// 不需要 token 的白名单接口
const WHITE_LIST = [
  '/api/user/login',
  '/api/user/register'
]

/**
 * 检查接口是否在白名单中
 */
function isWhiteList(url) {
  return WHITE_LIST.some(item => url.startsWith(item))
}

/**
 * 统一请求方法
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求路径
 * @param {string} options.method - 请求方法，默认 GET
 * @param {Object} options.data - 请求数据
 * @param {boolean} options.showLoading - 是否显示加载提示，默认 false
 * @param {boolean} options.showError - 是否显示错误提示，默认 true
 */
export function request(options) {
  return new Promise((resolve, reject) => {
    // 显示加载提示
    if (options.showLoading) {
      uni.showLoading({ title: '加载中...', mask: true })
    }

    // 获取 token
    const token = uni.getStorageSync('token') || ''

    // 构建请求头
    const header = {
      'Content-Type': 'application/json',
      ...options.header
    }

    // 非白名单接口需要携带 token
    if (!isWhiteList(options.url) && token) {
      header['Authorization'] = token
    }

    uni.request({
      url: baseURL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header,
      timeout: 30000,
      success: (res) => {
        if (options.showLoading) {
          uni.hideLoading()
        }

        const { code, success, data, message } = res.data

        // 请求成功
        if (code === 0 || success) {
          resolve(res.data)
        }
        // Token 过期或未登录
        else if (code === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')

          if (options.showError !== false) {
            uni.showToast({ title: '请重新登录', icon: 'none' })
          }

          setTimeout(() => {
            uni.reLaunch({ url: '/pages/login/login' })
          }, 1500)

          reject(res.data)
        }
        // 其他错误
        else {
          // 如果是未授权相关的错误，静默处理不显示提示
          const isAuthError = message && (
            message.includes('Authorization') ||
            message.includes('token') ||
            message.includes('未登录')
          )

          if (options.showError !== false && !isAuthError) {
            uni.showToast({ title: message || '请求失败', icon: 'none' })
          }
          reject(res.data)
        }
      },
      fail: (err) => {
        if (options.showLoading) {
          uni.hideLoading()
        }

        if (options.showError !== false) {
          uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' })
        }

        reject(err)
      }
    })
  })
}

/**
 * GET 请求
 */
export function get(url, params, options = {}) {
  // GET请求参数需要拼接到URL上
  if (params && Object.keys(params).length > 0) {
    const queryString = Object.keys(params)
      .filter(key => params[key] !== null && params[key] !== undefined && params[key] !== '')
      .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
      .join('&')

    if (queryString) {
      url = url.includes('?') ? `${url}&${queryString}` : `${url}?${queryString}`
    }
  }
  return request({ url, method: 'GET', ...options })
}

/**
 * POST 请求
 */
export function post(url, data, options = {}) {
  return request({ url, method: 'POST', data, ...options })
}

/**
 * PUT 请求
 */
export function put(url, data, options = {}) {
  return request({ url, method: 'PUT', data, ...options })
}

/**
 * DELETE 请求
 */
export function del(url, data, options = {}) {
  return request({ url, method: 'DELETE', data, ...options })
}

/**
 * 上传文件
 */
export function uploadFile(filePath, formData = {}) {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token') || ''

    uni.uploadFile({
      url: baseURL + '/api/upload/image',
      filePath,
      name: 'file',
      formData,
      header: {
        'Authorization': token
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 0 || data.success) {
            resolve(data)
          } else {
            uni.showToast({ title: data.message || '上传失败', icon: 'none' })
            reject(data)
          }
        } catch (e) {
          reject(e)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

/**
 * 批量上传文件
 */
export async function uploadFiles(filePaths) {
  const urls = []
  for (const filePath of filePaths) {
    try {
      const res = await uploadFile(filePath)
      let imgUrl = ''
      if (res.data && res.data.url) {
        imgUrl = res.data.url
      } else if (res.url) {
        imgUrl = res.url
      }

      if (imgUrl) {
        urls.push(imgUrl)
      }
    } catch (e) {
      console.error('上传失败:', e)
    }
  }
  return urls
}

export default {
  request,
  get,
  post,
  put,
  del,
  uploadFile,
  uploadFiles
}
