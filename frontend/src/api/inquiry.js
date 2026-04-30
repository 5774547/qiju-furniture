import request from '@/utils/request'

export function createInquiry(data) {
  return request.post('/inquiries', data)
}

export function getMyInquiries() {
  return request.get('/inquiries/my')
}

export function getInquiryDetail(id) {
  return request.get(`/inquiries/${id}`)
}
