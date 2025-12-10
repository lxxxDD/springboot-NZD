import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 基础URL配置
export const BASE_URL = 'http://localhost:8080'
export const API_BASE_URL = BASE_URL + '/api/admin'

// 是否正在跳转登录页（防止重复跳转和提示）
let isRedirectingToLogin = false

// 清除登录信息并跳转登录页
const handleLogout = (message = '登录已过期，请重新登录') => {
  if (isRedirectingToLogin) return
  isRedirectingToLogin = true
  
  // 清除本地存储的登录信息
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  
  // 只提示一次
  ElMessage.warning(message)
  
  // 跳转到登录页
  router.push('/login').finally(() => {
    // 延迟重置标志，防止短时间内重复触发
    setTimeout(() => {
      isRedirectingToLogin = false
    }, 1000)
  })
}

// 检查是否是token相关错误
const isTokenError = (message) => {
  if (!message) return false
  const tokenErrorKeywords = ['JWT', 'jwt', 'token', 'Token', '令牌', '未登录', '登录过期', '认证失败', '无效的凭证']
  return tokenErrorKeywords.some(keyword => message.includes(keyword))
}

// 创建axios实例
const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 检查是否是token相关错误
      if (res.code === 401 || isTokenError(res.message)) {
        handleLogout()
        return Promise.reject(new Error('登录已过期'))
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    // 处理HTTP错误状态码
    const status = error.response?.status
    const message = error.response?.data?.message || error.message
    
    // 401 未授权 或 token相关错误
    if (status === 401 || isTokenError(message)) {
      handleLogout()
      return Promise.reject(new Error('登录已过期'))
    }
    
    // 403 禁止访问
    if (status === 403) {
      ElMessage.error('没有权限访问该资源')
      return Promise.reject(error)
    }
    
    // 其他错误
    ElMessage.error(message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
