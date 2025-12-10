/**
 * API 统一导出
 */
import request from './request'
import userApi from './user'
import marketApi from './market'
import ordersApi from './orders'
import messagesApi from './messages'
import repairsApi from './repairs'
import activitiesApi from './activities'
import notificationsApi from './notifications'
import chatApi from './chat'
import newsApi from './news'

export {
  request,
  userApi,
  marketApi,
  ordersApi,
  messagesApi,
  repairsApi,
  activitiesApi,
  notificationsApi,
  chatApi,
  newsApi
}

export default {
  request,
  user: userApi,
  market: marketApi,
  orders: ordersApi,
  messages: messagesApi,
  repairs: repairsApi,
  activities: activitiesApi,
  notifications: notificationsApi,
  chat: chatApi,
  news: newsApi
}
