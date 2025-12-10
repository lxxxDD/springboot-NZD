const KEY = 'campus_orders'

export function listOrders(){
  try { return uni.getStorageSync(KEY) || [] } catch (e) { return [] }
}

export function addOrder(order){
  const list = listOrders()
  const id = `#${Math.floor(Math.random()*9000+1000)}`
  const item = { id, status: 'Pending', time: 'just now', ...order }
  list.unshift(item)
  uni.setStorageSync(KEY, list)
  return item
}

export function clearOrders(){ uni.removeStorageSync(KEY) }

export function updateOrderStatus(orderId, status) {
  const list = listOrders()
  const order = list.find(o => o.id === orderId)
  if (order) {
    order.status = status
    uni.setStorageSync(KEY, list)
  }
  return order
}

