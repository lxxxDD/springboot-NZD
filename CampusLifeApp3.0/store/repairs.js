const KEY = 'campus_repairs'

export function listRepairs() {
  try { return uni.getStorageSync(KEY) || [] } catch (e) { return [] }
}

export function addRepair(repair) {
  const list = listRepairs()
  const item = { id: repair.id || `#R-${Math.floor(Math.random()*900+100)}`, status: 'Received', date: 'just now', rating: null, ...repair }
  list.unshift(item)
  uni.setStorageSync(KEY, list)
  return item
}

export function updateRepair(id, data){
  const list = listRepairs()
  const idx = list.findIndex(r=>r.id===id)
  if (idx>-1){ list[idx] = { ...list[idx], ...data }; uni.setStorageSync(KEY, list) }
}

