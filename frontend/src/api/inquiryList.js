import request from '@/utils/request'

export function getInquiryList() {
  return request.get('/inquiry-lists')
}

export function addToInquiryList(data) {
  return request.post('/inquiry-lists', data)
}

export function updateInquiryItem(id, data) {
  return request.put(`/inquiry-lists/${id}`, data)
}

export function removeFromInquiryList(id) {
  return request.delete(`/inquiry-lists/${id}`)
}

export function clearInquiryList() {
  return request.delete('/inquiry-lists/clear')
}
