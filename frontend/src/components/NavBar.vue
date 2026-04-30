<template>
  <header class="navbar" :class="{ 'navbar-transparent': isTransparent }">
    <div class="navbar-inner">
      <div class="nav-left">
        <router-link to="/" class="logo">
          <span class="logo-icon">&#x1f3e0;</span>
          <span class="logo-text">栖居家具</span>
        </router-link>
      </div>

      <nav class="nav-center">
        <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
          <el-icon><HomeFilled /></el-icon>
          首页
        </router-link>
        <router-link to="/inquiries" class="nav-link" :class="{ active: $route.path.startsWith('/inquiries') }">
          <el-icon><List /></el-icon>
          我的询价单
        </router-link>
      </nav>

      <div class="nav-right">
        <router-link to="/inquiry-list" class="cart-link">
          <el-badge :value="inquiryStore.count" :hidden="inquiryStore.count === 0" class="cart-badge">
            <el-icon :size="22"><ChatDotSquare /></el-icon>
          </el-badge>
          <span class="cart-text">询价清单</span>
        </router-link>

        <!-- Logged in: user dropdown -->
        <template v-if="authStore.isLoggedIn">
          <el-dropdown trigger="click" class="user-dropdown" @command="handleUserCommand">
            <span class="user-trigger">
              <el-avatar :size="32" class="user-avatar">
                {{ displayName }}
              </el-avatar>
              <span class="user-nickname">{{ authStore.user?.nickname || authStore.user?.username || '用户' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="inquiries">
                  <el-icon><List /></el-icon>
                  我的询价单
                </el-dropdown-item>
                <el-dropdown-item v-if="authStore.isAdmin" command="admin">
                  <el-icon><Setting /></el-icon>
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <!-- Not logged in: auth buttons -->
        <template v-else>
          <router-link to="/login" class="auth-btn login-btn">登录</router-link>
          <router-link to="/register" class="auth-btn register-btn">注册</router-link>
        </template>

        <button class="theme-toggle" @click="toggleTheme" :title="isDark ? '切换到亮色模式' : '切换到暗色模式'">
          <el-icon :size="20">
            <Moon v-if="!isDark" />
            <Sunny v-else />
          </el-icon>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useInquiryListStore } from '@/stores/inquiryList'
import { useAuthStore } from '@/stores/auth'
import {
  HomeFilled, List, Moon, Sunny,
  User, ArrowDown, SwitchButton, Setting, ChatDotSquare
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const inquiryStore = useInquiryListStore()
const authStore = useAuthStore()

const isTransparent = ref(false)
const isDark = ref(false)

const displayName = computed(() => {
  const name = authStore.user?.nickname || authStore.user?.username || 'U'
  return name.charAt(0).toUpperCase()
})

watch(() => route.path, () => {
  isTransparent.value = route.path === '/'
})

onMounted(() => {
  isTransparent.value = route.path === '/'
  // Check saved theme preference
  const savedTheme = localStorage.getItem('qiju_theme')
  if (savedTheme === 'dark') {
    isDark.value = true
    document.documentElement.setAttribute('data-theme', 'dark')
  }
  // If logged in, fetch profile on mount (if not already loaded)
  if (authStore.isLoggedIn && !authStore.user) {
    authStore.fetchProfile()
  }
  // Fetch inquiry list count
  if (authStore.isLoggedIn) {
    inquiryStore.fetchInquiryList()
  }
})

function toggleTheme() {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.setAttribute('data-theme', 'dark')
    localStorage.setItem('qiju_theme', 'dark')
  } else {
    document.documentElement.removeAttribute('data-theme')
    localStorage.setItem('qiju_theme', 'light')
  }
}

function handleUserCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'inquiries':
      router.push('/inquiries')
      break
    case 'admin':
      router.push('/admin/dashboard')
      break
    case 'logout':
      authStore.logout()
      break
  }
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  z-index: 1000;
  background: var(--nav-bg);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.navbar-transparent {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(16px);
  border-bottom: none;
}

[data-theme="dark"] .navbar-transparent {
  background: rgba(30, 30, 30, 0.08);
}

.navbar-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.logo-icon {
  font-size: 32px;
}

.logo-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 2px;
}

.nav-center {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  color: var(--primary-color);
  background: var(--hover-bg);
}

.nav-link.active {
  color: var(--primary-color);
  background: var(--hover-bg);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cart-link {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: var(--text-secondary);
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.cart-link:hover {
  color: var(--primary-color);
  background: var(--hover-bg);
}

.cart-text {
  font-size: 14px;
  font-weight: 500;
}

.cart-badge :deep(.el-badge__content) {
  background: var(--primary-color);
}

/* Auth buttons */
.auth-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.login-btn {
  color: var(--primary-color);
  background: transparent;
  border: 1px solid var(--primary-color);
}

.login-btn:hover {
  background: var(--primary-color);
  color: #fff;
}

.register-btn {
  color: #fff;
  background: var(--primary-color);
  border: 1px solid var(--primary-color);
}

.register-btn:hover {
  background: var(--primary-hover);
  border-color: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 115, 85, 0.3);
}

/* User dropdown */
.user-dropdown {
  cursor: pointer;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.user-trigger:hover {
  background: var(--hover-bg);
}

.user-avatar {
  background: var(--primary-color);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-nickname {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  color: var(--text-secondary);
  transition: transform 0.3s ease;
}

.user-trigger:hover .dropdown-icon {
  transform: rotate(180deg);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  padding: 10px 20px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}

:deep(.el-dropdown-menu__item--divided) {
  border-top-color: var(--border-color);
}

.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border: none;
  background: var(--hover-bg);
  color: var(--text-secondary);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.theme-toggle:hover {
  color: var(--primary-color);
  background: var(--primary-light);
}

@media (max-width: 768px) {
  .navbar-inner {
    padding: 0 16px;
  }
  .nav-center {
    display: none;
  }
  .cart-text {
    display: none;
  }
  .logo-text {
    font-size: 20px;
  }
  .user-nickname {
    display: none;
  }
  .auth-btn {
    padding: 6px 14px;
    font-size: 13px;
  }
}
</style>
