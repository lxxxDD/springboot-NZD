// 权限控制工具
import { routes } from '@/router'

/**
 * 检查用户是否有权限访问某个路由
 * @param {Array} userPermissions 用户权限列表
 * @param {String} permission 需要的权限
 * @returns {Boolean}
 */
export function hasPermission(userPermissions, permission) {
  if (!permission) return true // 没有权限要求的路由，所有人都可以访问
  if (!userPermissions || userPermissions.length === 0) return false
  return userPermissions.includes(permission)
}

/**
 * 根据用户权限过滤路由
 * @param {Array} routes 路由配置
 * @param {Array} permissions 用户权限列表
 * @returns {Array} 过滤后的路由
 */
export function filterRoutes(routes, permissions) {
  const res = []
  
  routes.forEach(route => {
    const tmp = { ...route }
    
    // 检查路由权限
    if (hasRoutePermission(tmp, permissions)) {
      // 如果有子路由，递归过滤
      if (tmp.children) {
        tmp.children = filterRoutes(tmp.children, permissions)
      }
      res.push(tmp)
    }
  })
  
  return res
}

/**
 * 检查路由权限
 * @param {Object} route 路由对象
 * @param {Array} permissions 用户权限列表
 * @returns {Boolean}
 */
function hasRoutePermission(route, permissions) {
  // 如果路由有权限要求
  if (route.meta && route.meta.permission) {
    return hasPermission(permissions, route.meta.permission)
  }
  // 如果有子路由，只要有一个子路由有权限就显示父路由
  if (route.children && route.children.length) {
    return route.children.some(child => hasRoutePermission(child, permissions))
  }
  // 没有权限要求的路由，默认可以访问
  return true
}

/**
 * 从localStorage获取用户权限
 * @returns {Array}
 */
export function getUserPermissions() {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const rolePermissions = userInfo.role?.permissions
    
    if (typeof rolePermissions === 'string') {
      return JSON.parse(rolePermissions)
    }
    return rolePermissions || []
  } catch (error) {
    console.error('获取用户权限失败:', error)
    return []
  }
}
