import request from '@/utils/request'

export function getProducts(params) {
  return request.get('/products', { params })
}

export function getProduct(id) {
  return request.get(`/products/${id}`)
}

export function getCategories() {
  return request.get('/products/categories')
}
