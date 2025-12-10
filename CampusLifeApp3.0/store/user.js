const KEY = 'campus_user'

const defaultUser = {
  name: '用户',
  id: '',
  balance: 0,
  points: 0,
  email: '',
  phone: '',
  avatar: ''
}

export function getUser(){
  const u = uni.getStorageSync(KEY)
  if (!u) return { ...defaultUser }
  // 兼容 {type, data} 格式和直接对象格式
  if (u.data && typeof u.data === 'object') {
    return u.data
  }
  return typeof u === 'object' ? u : { ...defaultUser }
}

export function setUser(u){
  uni.setStorageSync(KEY, u)
}

export function setBalance(amount){
  const u = getUser()
  u.balance = amount
  setUser(u)
}

