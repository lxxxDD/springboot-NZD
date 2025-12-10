// 简易购物车存储，使用本地存储实现跨页面共享
const KEY = 'campus_cart'
const CANTEEN_KEY = 'campus_cart_canteen'

export function getCart() {
  try { return uni.getStorageSync(KEY) || [] } catch (e) { return [] }
}

export function setCart(list) {
  try { uni.setStorageSync(KEY, list) } catch (e) {}
}

export function addItem(item) {
  const cart = getCart()
  const exist = cart.find(i => i.id === item.id)
  if (exist) {
    exist.qty = (exist.qty || 1) + (item.qty || 1)
  } else {
    cart.push({ ...item, qty: item.qty || 1 })
  }
  setCart(cart)
  return cart
}

export function updateItemQuantity(id, qty) {
  const cart = getCart()
  const item = cart.find(i => i.id === id)
  if (item) {
    item.qty = qty
  }
  setCart(cart)
  return cart
}

export function removeItem(id) {
  let cart = getCart();
  cart = cart.filter(i => i.id !== id);
  setCart(cart);
  return cart;
}

export function clearCart() { 
  setCart([]) 
  try { uni.removeStorageSync(CANTEEN_KEY) } catch (e) {}
}

// 获取当前食堂ID
export function getCartCanteen() {
  try { return uni.getStorageSync(CANTEEN_KEY) || null } catch (e) { return null }
}

// 设置当前食堂ID
export function setCartCanteen(canteen) {
  try { uni.setStorageSync(CANTEEN_KEY, canteen) } catch (e) {}
}

