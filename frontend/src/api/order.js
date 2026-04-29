import request from '@/utils/request'
import { getSessionId } from '@/utils/session'

export function createOrder(data) {
  return request.post('/orders', data)
}

export function getOrders() {
  const sessionId = getSessionId()
  return request.get('/orders', { params: { sessionId } })
}

export function getMyOrders() {
  return request.get('/orders/my')
}

export function getOrder(id) {
  return request.get(`/orders/${id}`)
}
