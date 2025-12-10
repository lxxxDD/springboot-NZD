import request from './request'
import axios from 'axios'

// ========== 认证相关 ==========
export const login = (data) => request.post('/auth/login', data)
export const getAdminInfo = () => request.get('/auth/info')
export const logout = () => request.post('/auth/logout')

// ========== 用户管理 ==========
export const getUserList = (params) => request.get('/users', { params })
export const updateUserStatus = (id, status) => request.post(`/users/${id}/status`, { status })

// ========== 报修管理 ==========
export const getRepairList = (params) => request.get('/repairs', { params })
export const getRepairById = (id) => request.get(`/repairs/${id}`)
export const updateRepairStatus = (id, status) => request.post(`/repairs/${id}/status`, { status })
export const deleteRepair = (id) => request.delete(`/repairs/${id}`)
export const assignRepair = (id, technicianId) => request.post(`/repairs/${id}/assign`, { technicianId })

// ========== 维修人员管理 ==========
export const getTechnicianList = () => request.get('/technicians')
export const createTechnician = (data) => request.post('/technicians', data)
export const updateTechnician = (id, data) => request.put(`/technicians/${id}`, data)
export const deleteTechnician = (id) => request.delete(`/technicians/${id}`)

// ========== 食堂管理 ==========
export const getCanteenList = (params) => request.get('/canteens', { params })
export const getCanteenById = (id) => request.get(`/canteens/${id}`)
export const createCanteen = (data) => request.post('/canteens', data)
export const updateCanteen = (id, data) => request.put(`/canteens/${id}`, data)
export const deleteCanteen = (id) => request.delete(`/canteens/${id}`)
export const updateCanteenStatus = (id, status) => request.post(`/canteens/${id}/status`, { status })

// ========== 二手商品管理 ==========
export const getMarketItemList = (params) => request.get('/market/items', { params })
export const getMarketItemById = (id) => request.get(`/market/items/${id}`)
export const updateMarketItemStatus = (id, status, reason) => request.post(`/market/items/${id}/status`, { status, reason })
export const deleteMarketItem = (id) => request.delete(`/market/items/${id}`)

// ========== 商品分类 ==========
export const getMarketCategories = () => request.get('/market/categories')
export const getAllMarketCategories = () => request.get('/market/categories/all')
export const createMarketCategory = (data) => request.post('/market/categories', data)
export const updateMarketCategory = (id, data) => request.put(`/market/categories/${id}`, data)
export const deleteMarketCategory = (id) => request.delete(`/market/categories/${id}`)

// ========== 活动管理 ==========
export const getActivityList = (params) => request.get('/activities', { params })
export const getActivityById = (id) => request.get(`/activities/${id}`)
export const createActivity = (data) => request.post('/activities', data)
export const updateActivityStatus = (id, status) => request.post(`/activities/${id}/status`, { status })
export const deleteActivity = (id) => request.delete(`/activities/${id}`)

// ========== 新闻/内容管理 ==========
export const getNewsList = (params) => request.get('/news', { params })
export const getNewsById = (id) => request.get(`/news/${id}`)
export const createNews = (data) => request.post('/news', data)
export const updateNewsStatus = (id, status) => request.post(`/news/${id}/status`, { status })
export const deleteNews = (id) => request.delete(`/news/${id}`)

// ========== 财务管理 ==========
export const getTransactionList = (params) => request.get('/finance/transactions', { params })
export const getTransactionById = (id) => request.get(`/finance/transactions/${id}`)
export const getFinanceStats = () => request.get('/finance/stats')
export const rechargeUser = (data) => request.post('/finance/recharge', data)

// ========== 管理员账号管理 ==========
export const getAdminList = (params) => request.get('/accounts', { params })
export const getAdminById = (id) => request.get(`/accounts/${id}`)
export const createAdmin = (data) => request.post('/accounts', data)
export const updateAdmin = (id, data) => request.put(`/accounts/${id}`, data)
export const deleteAdmin = (id) => request.delete(`/accounts/${id}`)
export const updateAdminStatus = (id, status) => request.post(`/accounts/${id}/status`, { status })
export const resetAdminPassword = (id, password) => request.post(`/accounts/${id}/reset-password`, { password })

// ========== 角色管理 ==========
export const getRoleList = (params) => request.get('/roles', { params })
export const getAllRoles = () => request.get('/roles/all')
export const getRoleById = (id) => request.get(`/roles/${id}`)
export const createRole = (data) => request.post('/roles', data)
export const updateRole = (id, data) => request.put(`/roles/${id}`, data)
export const deleteRole = (id) => request.delete(`/roles/${id}`)
export const updateRoleStatus = (id, status) => request.post(`/roles/${id}/status`, { status })

// ========== 菜品管理 ==========
export const getFoodItemList = (params) => request.get('/food-items', { params })
export const getFoodItemById = (id) => request.get(`/food-items/${id}`)
export const createFoodItem = (data) => request.post('/food-items', data)
export const updateFoodItem = (id, data) => request.put(`/food-items/${id}`, data)
export const deleteFoodItem = (id) => request.delete(`/food-items/${id}`)
export const updateFoodItemStatus = (id, status) => request.post(`/food-items/${id}/status`, { status })

// ========== 订餐订单管理 ==========
export const getFoodOrderList = (params) => request.get('/food-orders', { params })
export const getFoodOrderById = (id) => request.get(`/food-orders/${id}`)
export const updateFoodOrderStatus = (id, status) => request.post(`/food-orders/${id}/status`, { status })

// ========== 二手订单管理 ==========
export const getOrderList = (params) => request.get('/orders', { params })
export const getOrderById = (id) => request.get(`/orders/${id}`)
export const updateOrderStatus = (id, status) => request.post(`/orders/${id}/status`, { status })

// ========== 控制台统计 ==========
export const getDashboardStats = () => request.get('/dashboard/stats')
export const getOrdersTrend = () => request.get('/dashboard/charts/orders-trend')
export const getModuleDistribution = () => request.get('/dashboard/charts/module-distribution')
export const getUserTrend = () => request.get('/dashboard/charts/user-trend')
export const getRepairStatus = () => request.get('/dashboard/charts/repair-status')

// ========== 系统日志 ==========
export const getRecentLogs = (limit = 10) => request.get('/logs/recent', { params: { limit } })
export const getSystemLogs = (params) => request.get('/logs', { params })

// ========== 文件上传 ==========
export const uploadImage = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  const token = localStorage.getItem('token')
  const res = await axios.post('http://localhost:8080/api/upload/image', formData, {
    headers: { 
      'Content-Type': 'multipart/form-data',
      'Authorization': token ? `Bearer ${token}` : ''
    }
  })
  return res.data
}

// 保存新闻（创建或更新）
export const saveNews = (data) => {
  if (data.id) {
    return request.put(`/news/${data.id}`, data)
  }
  return request.post('/news', data)
}
