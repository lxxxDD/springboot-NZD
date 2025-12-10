import { request } from './request.js'

// 获取所有食堂列表
export function getCanteenList() {
  return request({
    url: '/api/canteen/list',
    method: 'GET'
  })
}

// 获取营业中的食堂
export function getOpenCanteens() {
  return request({
    url: '/api/canteen/open',
    method: 'GET'
  })
}

// 获取食堂详情
export function getCanteenById(id) {
  return request({
    url: `/api/canteen/${id}`,
    method: 'GET'
  })
}

// 根据食堂ID获取菜品列表
export function getFoodByCanteen(canteenId) {
  return request({
    url: `/api/food/canteen/${canteenId}`,
    method: 'GET'
  })
}

// 获取菜品详情
export function getFoodById(id) {
  return request({
    url: `/api/food/${id}`,
    method: 'GET'
  })
}

// 根据分类获取菜品
export function getFoodByCategory(category) {
  return request({
    url: `/api/food/category/${encodeURIComponent(category)}`,
    method: 'GET'
  })
}

// 搜索菜品
export function searchFood(keyword) {
  return request({
    url: '/api/food/search',
    method: 'GET',
    data: { keyword }
  })
}

// 获取热销菜品
export function getHotFood(limit = 10) {
  return request({
    url: '/api/food/hot',
    method: 'GET',
    data: { limit }
  })
}

// ==================== 食堂订单相关 ====================

// 创建食堂订单
export function createFoodOrder(data) {
  return request({
    url: '/api/food-orders',
    method: 'POST',
    data
  })
}

// 支付食堂订单
export function payFoodOrder(orderId, paymentMethod = 'balance') {
  return request({
    url: `/api/food-orders/${orderId}/pay`,
    method: 'POST',
    data: { paymentMethod }
  })
}

// 获取订单详情
export function getFoodOrderDetail(orderId) {
  return request({
    url: `/api/food-orders/${orderId}`,
    method: 'GET'
  })
}

// 获取用户食堂订单列表
export function getFoodOrderList(params) {
  return request({
    url: '/api/food-orders',
    method: 'GET',
    params: params  // GET请求使用params而不是data
  })
}

// 取消订单
export function cancelFoodOrder(orderId) {
  return request({
    url: `/api/food-orders/${orderId}/cancel`,
    method: 'POST'
  })
}

// 确认取餐（完成订单）
export function completeFoodOrder(orderId) {
  return request({
    url: `/api/food-orders/${orderId}/complete`,
    method: 'POST'
  })
}
