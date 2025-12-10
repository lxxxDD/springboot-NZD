import { createRouter, createWebHashHistory } from 'vue-router'
import { routes } from './routes'
import { hasPermission, getUserPermissions } from '@/utils/permission'

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫 - 权限控制
router.beforeEach((to, from, next) => {
  // 登录页直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 检查是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    return
  }

  // 检查页面权限
  const permission = to.meta?.permission
  if (permission) {
    const userPermissions = getUserPermissions()
    if (!hasPermission(userPermissions, permission)) {
      // 无权限，显示提示并停留在当前页或返回上一页
      console.warn('无权限访问:', to.path)
      // 如果是从其他页面跳转来的，返回上一页；否则跳转到有权限的第一个页面
      if (from.path && from.path !== '/') {
        next(false) // 取消导航
      } else {
        // 找到第一个有权限的路由
        const accessibleRoute = routes.find(route => {
          if (route.children) {
            return route.children.some(child => 
              !child.meta?.permission || hasPermission(userPermissions, child.meta.permission)
            )
          }
          return !route.meta?.permission || hasPermission(userPermissions, route.meta.permission)
        })
        
        if (accessibleRoute) {
          const firstChild = accessibleRoute.children?.[0]
          next(firstChild ? `${accessibleRoute.path}/${firstChild.path}` : accessibleRoute.path)
        } else {
          next('/login') // 没有任何权限，返回登录页
        }
      }
      return
    }
  }

  next()
})

export default router
export { routes }
