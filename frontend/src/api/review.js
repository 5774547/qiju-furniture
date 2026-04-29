import request from '@/utils/request'

export function getReviews(productId) {
  return request.get(`/reviews/product/${productId}`)
}

export function submitReview(data) {
  return request.post('/reviews', data)
}
