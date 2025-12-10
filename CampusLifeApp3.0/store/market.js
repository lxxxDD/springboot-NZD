const KEY = 'campus_market'

export function listMarket(){
  try { return uni.getStorageSync(KEY) || [] } catch(e){ return [] }
}

export function addMarket(item){
  const list = listMarket()
  const id = Date.now()
  list.unshift({ id, ...item })
  uni.setStorageSync(KEY, list)
  return id
}

export function updateMarketItem(item){
  const list = listMarket()
  // 兼容字符串和数字类型的id比较
  const index = list.findIndex(i => String(i.id) === String(item.id))
  if (index !== -1) {
    list[index] = { ...list[index], ...item }
    uni.setStorageSync(KEY, list)
    return true
  }
  return false
}

// 删除商品
export function deleteMarketItem(id) {
  const list = listMarket()
  const newList = list.filter(i => String(i.id) !== String(id))
  uni.setStorageSync(KEY, newList)
  return true
}

// 获取单个商品
export function getMarketItem(id) {
  const list = listMarket()
  return list.find(i => String(i.id) === String(id))
}

