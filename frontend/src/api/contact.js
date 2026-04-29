import request from '@/utils/request'

export function submitContact(data) {
  return request.post('/contacts', data)
}
