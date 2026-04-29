<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <router-link to="/" class="register-logo">
            <span class="logo-icon">&#x1f3e0;</span>
            <span class="logo-text">栖居家具</span>
          </router-link>
          <h2 class="register-title">创建账户</h2>
          <p class="register-subtitle">加入栖居，开启品质家居生活</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="register-form"
          @submit.prevent="handleRegister"
          label-width="0"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              :prefix-icon="User"
              size="large"
              autocomplete="username"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              placeholder="邮箱"
              :prefix-icon="Message"
              size="large"
              autocomplete="email"
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
              autocomplete="new-password"
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="确认密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              autocomplete="new-password"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form-item>

          <div class="register-footer">
            <span class="login-text">已有账户？</span>
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
        </el-form>
      </div>

      <div class="register-decoration">
        <div class="decoration-content">
          <div class="decoration-icon">&#x1f3e0;</div>
          <h3>匠心品质·自然生活</h3>
          <p>注册即享会员专享优惠</p>
          <div class="decoration-benefits">
            <div class="benefit-item">
              <el-icon><Present /></el-icon>
              <span>新人专享满减优惠</span>
            </div>
            <div class="benefit-item">
              <el-icon><Van /></el-icon>
              <span>满额包邮服务</span>
            </div>
            <div class="benefit-item">
              <el-icon><Star /></el-icon>
              <span>会员积分兑换好礼</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message, Present, Van, Star } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' },
    { max: 20, message: '用户名不超过20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含中英文、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function handleRegister() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.register({
      username: form.username,
      email: form.email,
      password: form.password
    })
    router.push('/')
  } catch (e) {
    // Error already handled by response interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #faf6f0 0%, #f0ebe4 50%, #e8e0d6 100%);
  padding: 40px 20px;
}

.register-container {
  display: flex;
  max-width: 900px;
  width: 100%;
  min-height: 580px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(139, 115, 85, 0.15);
  overflow: hidden;
}

.register-card {
  flex: 1;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-logo {
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

.register-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary, #2c2c2c);
  margin: 0 0 8px 0;
}

.register-subtitle {
  font-size: 14px;
  color: var(--text-secondary, #999);
  margin: 0;
}

.register-form {
  max-width: 360px;
  margin: 0 auto;
  width: 100%;
}

.register-form :deep(.el-input__wrapper) {
  background: #f8f6f3;
  border: 1px solid #e8e0d6;
  border-radius: 10px;
  box-shadow: none;
  padding: 4px 16px;
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color, #8b7355);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color, #8b7355);
  box-shadow: 0 0 0 2px rgba(139, 115, 85, 0.1);
}

.register-form :deep(.el-input__inner) {
  height: 48px;
  font-size: 15px;
}

.register-form :deep(.el-input__prefix) {
  margin-right: 8px;
}

.register-form :deep(.el-input__prefix-inner) {
  color: #b8a894;
}

.register-btn {
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

.register-btn:hover {
  background: var(--primary-hover, #7a6448);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(139, 115, 85, 0.3);
}

.register-btn:active {
  transform: translateY(0);
}

.register-footer {
  text-align: center;
  margin-top: 8px;
}

.login-text {
  font-size: 14px;
  color: var(--text-secondary, #999);
}

.login-link {
  font-size: 14px;
  color: var(--primary-color, #8b7355);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.login-link:hover {
  text-decoration: underline;
}

/* Decoration side */
.register-decoration {
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

.decoration-benefits {
  display: flex;
  flex-direction: column;
  gap: 16px;
  text-align: left;
  max-width: 220px;
  margin: 0 auto;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.benefit-item .el-icon {
  font-size: 18px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .register-decoration {
    display: none;
  }
  .register-card {
    padding: 36px 24px;
  }
  .register-title {
    font-size: 24px;
  }
}
</style>
