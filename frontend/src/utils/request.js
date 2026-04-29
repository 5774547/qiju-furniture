import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - add auth token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('qiju_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor - auto unwrap Result wrapper
request.interceptors.response.use(
  (response) => {
    const body = response.data
    // Unwrap Result wrapper: { code, msg, data }
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 200) {
        return body.data
      }
      ElMessage.error(body.msg || '请求失败')
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return body
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      const msg = data?.msg || data?.message || '请求失败'

      if (status === 401) {
        // Token expired or invalid — clear and redirect to login
        localStorage.removeItem('qiju_token')
        ElMessage.error('登录已过期，请重新登录')
        const currentPath = router.currentRoute?.value?.fullPath || '/'
        if (currentPath !== '/login' && currentPath !== '/register') {
          router.push({ path: '/login', query: { redirect: currentPath } })
        }
        return Promise.reject(error)
      }

      switch (status) {
        case 400:
          ElMessage.error(`请求参数错误: ${msg}`)
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误，请稍后重试')
          break
        default:
          ElMessage.error(msg)
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

export default request
