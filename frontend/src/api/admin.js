import request from '@/utils/request'

// ===== 商品管理 =====
export function getAdminProducts() {
  return request.get('/admin/products/all')
}

export function createProduct(data) {
  return request.post('/admin/products', data)
}

export function updateProduct(id, data) {
  return request.put(`/admin/products/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/admin/products/${id}`)
}

// ===== 仪表盘 =====
export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}

// ===== 询价管理 =====
export function getAdminInquiries() {
  return request.get('/admin/inquiries')
}

export function getAdminInquiryDetail(id) {
  return request.get(`/admin/inquiries/${id}`)
}

export function setInquiryQuotation(id, params) {
  return request.put(`/admin/inquiries/${id}/quotation`, null, { params })
}

export function closeInquiry(id) {
  return request.put(`/admin/inquiries/${id}/close`)
}

// ===== 用户管理 =====
export function getAdminUsers() {
  return request.get('/admin/users')
}

export function toggleUserStatus(id) {
  return request.put(`/admin/users/${id}/status`)
}

// ===== 评价管理 =====
export function getAdminReviews() {
  return request.get('/admin/reviews')
}

export function deleteAdminReview(id) {
  return request.delete(`/admin/reviews/${id}`)
}
