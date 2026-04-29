import request from '@/utils/request'
import { getSessionId } from '@/utils/session'

export function getCart() {
  const sessionId = getSessionId()
  return request.get('/cart', { params: { sessionId } })
}

export function addToCart(data) {
  return request.post('/cart', data)
}

export function updateCartItem(id, data) {
  return request.put(`/cart/${id}`, data)
}

export function removeCartItem(id) {
  return request.delete(`/cart/${id}`)
}

export function clearCart() {
  const sessionId = getSessionId()
  return request.delete('/cart/clear', { params: { sessionId } })
}
