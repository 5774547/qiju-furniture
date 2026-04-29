import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getProfile, updateProfile, updatePassword } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

const TOKEN_KEY = 'qiju_token'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'admin' || user.value?.role === 'ROLE_ADMIN')

  function saveToken(t) {
    token.value = t
    localStorage.setItem(TOKEN_KEY, t)
  }

  function removeToken() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  async function login(loginDTO) {
    try {
      const data = await loginApi(loginDTO)
      const t = data.token || data.accessToken || (typeof data === 'string' ? data : null)
      if (t && typeof t === 'string') {
        saveToken(t)
      } else if (t && typeof t === 'object' && t.token) {
        saveToken(t.token)
      }
      if (data.user) {
        user.value = data.user
      } else {
        await fetchProfile()
      }
      ElMessage.success('登录成功')
      return data
    } catch (e) {
      throw e
    }
  }

  async function register(registerDTO) {
    try {
      const data = await registerApi(registerDTO)
      const t = data.token || data.accessToken || (typeof data === 'string' ? data : null)
      if (t && typeof t === 'string') {
        saveToken(t)
      } else if (t && typeof t === 'object' && t.token) {
        saveToken(t.token)
      }
      if (data.user) {
        user.value = data.user
      } else {
        await fetchProfile()
      }
      ElMessage.success('注册成功')
      return data
    } catch (e) {
      throw e
    }
  }

  function logout() {
    removeToken()
    ElMessage.success('已退出登录')
    router.push('/')
  }

  async function fetchProfile() {
    if (!token.value) {
      user.value = null
      return null
    }
    try {
      const data = await getProfile()
      user.value = data
      return data
    } catch (e) {
      if (e.response?.status === 401) {
        removeToken()
      }
      return null
    }
  }

  async function updateProfileData(data) {
    const result = await updateProfile(data)
    user.value = { ...(user.value || {}), ...result }
    ElMessage.success('个人信息更新成功')
    return result
  }

  async function changePassword(data) {
    const result = await updatePassword(data)
    ElMessage.success('密码修改成功')
    return result
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchProfile,
    updateProfile: updateProfileData,
    changePassword
  }
})
