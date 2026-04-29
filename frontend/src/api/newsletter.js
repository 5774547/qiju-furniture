import request from '@/utils/request'

export function subscribeNewsletter(data) {
  return request.post('/newsletter', data)
}
