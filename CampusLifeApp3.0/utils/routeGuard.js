/**
 * 路由守卫 - 统一管理页面访问权限
 */

// 不需要登录就可以访问的页面白名单
const whiteList = [
  '/pages/home/home',
  '/pages/login/login',
  '/pages/register/register',
  '/pages/market/market',
  '/pages/market/detail',
  '/pages/activity/list',
  '/pages/activity/detail',
  '/pages/news/detail',
  '/pages/profile/profile'   // 个人中心允许访问，但会显示未登录状态
]

/**
 * 检查用户是否已登录
 */
export function isLoggedIn() {
  const token = uni.getStorageSync('token')
  const userInfo = uni.getStorageSync('userInfo')
  return !!(token && userInfo)
}

/**
 * 路由拦截
 * @param {Object} to - 目标页面信息 { url, success, fail, complete }
 */
function routeIntercept(to) {
  const url = to.url
  
  // 提取页面路径（去除参数）
  const pagePath = url.split('?')[0]
  
  // 检查是否在白名单中
  const needAuth = !whiteList.some(path => pagePath.includes(path))
  
  if (needAuth && !isLoggedIn()) {
    // 需要登录但未登录，跳转到登录页
    console.log('未登录，跳转到登录页')
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    
    // 保存目标页面，登录后跳转
    uni.setStorageSync('redirectUrl', url)
    
    uni.reLaunch({
      url: '/pages/login/login'
    })
    return false
  }
  
  return true
}

/**
 * 初始化路由守卫
 * 拦截所有页面跳转方法
 */
export function initRouteGuard() {
  // 保存原始方法
  const originalNavigateTo = uni.navigateTo
  const originalRedirectTo = uni.redirectTo
  const originalReLaunch = uni.reLaunch
  const originalSwitchTab = uni.switchTab
  
  // 拦截 navigateTo
  uni.navigateTo = function(options) {
    if (routeIntercept(options)) {
      return originalNavigateTo.call(this, options)
    }
  }
  
  // 拦截 redirectTo
  uni.redirectTo = function(options) {
    if (routeIntercept(options)) {
      return originalRedirectTo.call(this, options)
    }
  }
  
  // 拦截 reLaunch（通常用于登录/退出，不拦截）
  uni.reLaunch = function(options) {
    return originalReLaunch.call(this, options)
  }
  
  // switchTab 不拦截，tabBar页面在各自的onShow中检查登录状态
  uni.switchTab = function(options) {
    return originalSwitchTab.call(this, options)
  }
  
  console.log('路由守卫已初始化')
}

/**
 * 检查当前页面是否需要登录
 * 在页面 onLoad 中调用
 */
export function checkAuth() {
  if (!isLoggedIn()) {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const route = '/' + currentPage.route
    
    // 检查当前页面是否在白名单中
    const needAuth = !whiteList.some(path => route.includes(path))
    
    if (needAuth) {
      console.log('当前页面需要登录，跳转到登录页')
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      
      setTimeout(() => {
        uni.reLaunch({
          url: '/pages/login/login'
        })
      }, 1500)
      
      return false
    }
  }
  
  return true
}

// tabBar页面列表
const tabBarPages = [
  '/pages/home/home',
  '/pages/market/market',
  '/pages/messages/messages',
  '/pages/profile/profile'
]

/**
 * 登录后跳转首页
 */
export function redirectAfterLogin() {
  uni.reLaunch({
    url: '/pages/home/home'
  })
}

export default {
  initRouteGuard,
  checkAuth,
  redirectAfterLogin,
  isLoggedIn
}
