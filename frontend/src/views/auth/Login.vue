<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <router-link to="/" class="login-logo">
            <span class="logo-icon">&#x1f3e0;</span>
            <span class="logo-text">栖居家具</span>
          </router-link>
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">登录您的账户，继续选购心仪家具</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin"
          label-width="0"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名或邮箱"
              :prefix-icon="User"
              size="large"
              autocomplete="username"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              autocomplete="current-password"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>

          <div class="login-footer">
            <span class="register-text">还没有账户？</span>
            <router-link to="/register" class="register-link">立即注册</router-link>
          </div>
        </el-form>
      </div>

      <div class="login-decoration">
        <div class="decoration-content">
          <div class="decoration-icon">&#x1f3e0;</div>
          <h3>匠心品质·自然生活</h3>
          <p>每一件家具，都是对生活的热爱</p>
          <div class="decoration-features">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>优质实木，环保耐用</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>匠心工艺，精雕细琢</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>7天无理由退换</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock, Check } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login({
      username: form.username,
      password: form.password
    })
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    // Error already handled by response interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #faf6f0 0%, #f0ebe4 50%, #e8e0d6 100%);
  padding: 40px 20px;
}

.login-container {
  display: flex;
  max-width: 900px;
  width: 100%;
  min-height: 520px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(139, 115, 85, 0.15);
  overflow: hidden;
}

.login-card {
  flex: 1;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-logo {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 32px;
}

.logo-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary, #2c2c2c);
  letter-spacing: 2px;
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary, #2c2c2c);
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 14px;
  color: var(--text-secondary, #999);
  margin: 0;
}

.login-form {
  max-width: 360px;
  margin: 0 auto;
  width: 100%;
}

.login-form :deep(.el-input__wrapper) {
  background: #f8f6f3;
  border: 1px solid #e8e0d6;
  border-radius: 10px;
  box-shadow: none;
  padding: 4px 16px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color, #8b7355);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color, #8b7355);
  box-shadow: 0 0 0 2px rgba(139, 115, 85, 0.1);
}

.login-form :deep(.el-input__inner) {
  height: 48px;
  font-size: 15px;
}

.login-form :deep(.el-input__prefix) {
  margin-right: 8px;
}

.login-form :deep(.el-input__prefix-inner) {
  color: #b8a894;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: 10px;
  background: var(--primary-color, #8b7355);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: var(--primary-hover, #7a6448);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(139, 115, 85, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  margin-top: 8px;
}

.register-text {
  font-size: 14px;
  color: var(--text-secondary, #999);
}

.register-link {
  font-size: 14px;
  color: var(--primary-color, #8b7355);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.register-link:hover {
  text-decoration: underline;
}

/* Decoration side */
.login-decoration {
  width: 380px;
  background: linear-gradient(135deg, var(--primary-color, #8b7355), #6b5a42);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #fff;
}

.decoration-content {
  text-align: center;
}

.decoration-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.decoration-content h3 {
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 10px 0;
  letter-spacing: 2px;
}

.decoration-content > p {
  font-size: 14px;
  opacity: 0.85;
  margin: 0 0 32px 0;
}

.decoration-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  text-align: left;
  max-width: 200px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-item .el-icon {
  font-size: 16px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .login-decoration {
    display: none;
  }
  .login-card {
    padding: 36px 24px;
  }
  .login-title {
    font-size: 24px;
  }
}
</style>
