<template>
  <div class="profile-page">
    <div class="profile-container">
      <div class="page-header">
        <h1 class="page-title">个人中心</h1>
        <p class="page-subtitle">管理您的个人信息和账户设置</p>
      </div>

      <div class="profile-content">
        <div class="profile-sidebar">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :src="avatarUrl" class="profile-avatar">
                {{ displayName }}
              </el-avatar>
              <div class="avatar-overlay" @click="triggerUpload">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </div>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleAvatarChange"
            />
            <h3 class="user-name">{{ authStore.user?.nickname || authStore.user?.username || '用户' }}</h3>
            <p class="user-role">{{ authStore.isAdmin ? '管理员' : '会员用户' }}</p>
          </div>

          <div class="sidebar-menu">
            <div class="menu-item active">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </div>
            <router-link to="/inquiries" class="menu-item">
              <el-icon><List /></el-icon>
              <span>我的询价单</span>
            </router-link>
          </div>

          <div class="sidebar-stats">
            <div class="stat-item">
              <span class="stat-label">注册时间</span>
              <span class="stat-value">{{ joinDate }}</span>
            </div>
          </div>
        </div>

        <div class="profile-main">
          <el-alert
            v-if="passwordSuccess"
            title="密码修改成功"
            type="success"
            show-icon
            :closable="true"
            @close="passwordSuccess = false"
            class="alert-message"
          />
          <el-alert
            v-if="profileSuccess"
            title="个人信息更新成功"
            type="success"
            show-icon
            :closable="true"
            @close="profileSuccess = false"
            class="alert-message"
          />

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            class="profile-form"
          >
            <div class="form-section-title">基本信息</div>

            <el-form-item label="用户名">
              <el-input
                v-model="form.username"
                disabled
                class="form-input"
              />
              <div class="form-tip">用户名不可修改</div>
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
              <el-input
                v-model="form.nickname"
                placeholder="设置您的昵称"
                class="form-input"
                maxlength="20"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="form.email"
                placeholder="请输入邮箱"
                class="form-input"
              />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="form.phone"
                placeholder="请输入手机号（选填）"
                class="form-input"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="save-btn"
                :loading="saving"
                @click="handleSave"
              >
                {{ saving ? '保存中...' : '保存修改' }}
              </el-button>
              <el-button
                size="large"
                class="reset-btn"
                @click="resetForm"
              >
                重置
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div class="password-section">
            <div class="password-section-header" @click="showPasswordForm = !showPasswordForm">
              <div class="form-section-title">安全设置</div>
              <el-button
                type="default"
                class="toggle-pwd-btn"
                :icon="showPasswordForm ? 'ArrowUp' : 'ArrowDown'"
              >
                {{ showPasswordForm ? '收起' : '修改密码' }}
              </el-button>
            </div>

            <el-collapse-transition>
              <div v-show="showPasswordForm">
                <el-form
                  ref="pwdFormRef"
                  :model="pwdForm"
                  :rules="pwdRules"
                  label-width="100px"
                  class="profile-form password-form"
                >
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input
                      v-model="pwdForm.oldPassword"
                      type="password"
                      placeholder="请输入当前密码"
                      class="form-input"
                      show-password
                    />
                  </el-form-item>

                  <el-form-item label="新密码" prop="newPassword">
                    <el-input
                      v-model="pwdForm.newPassword"
                      type="password"
                      placeholder="请输入新密码（至少6位）"
                      class="form-input"
                      show-password
                      minlength="6"
                    />
                  </el-form-item>

                  <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                      v-model="pwdForm.confirmPassword"
                      type="password"
                      placeholder="请再次输入新密码"
                      class="form-input"
                      show-password
                    />
                  </el-form-item>

                  <el-form-item>
                    <el-button
                      type="primary"
                      size="large"
                      class="save-btn"
                      :loading="pwdSaving"
                      @click="handleChangePassword"
                    >
                      {{ pwdSaving ? '修改中...' : '确认修改' }}
                    </el-button>
                    <el-button
                      size="large"
                      class="reset-btn"
                      @click="resetPwdForm"
                    >
                      重置
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-collapse-transition>
          </div>

          <el-divider />

          <div class="logout-section">
            <el-button
              type="danger"
              size="large"
              class="logout-btn"
              :icon="'SwitchButton'"
              @click="handleLogout"
            >
              退出登录
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, List, Camera, ArrowDown, ArrowUp, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const formRef = ref(null)
const pwdFormRef = ref(null)
const fileInput = ref(null)
const saving = ref(false)
const pwdSaving = ref(false)
const showPasswordForm = ref(false)
const passwordSuccess = ref(false)
const profileSuccess = ref(false)

const avatarUrl = computed(() => {
  return authStore.user?.avatar || ''
})

const displayName = computed(() => {
  const name = authStore.user?.nickname || authStore.user?.username || ''
  return name.charAt(0).toUpperCase()
})

const joinDate = computed(() => {
  if (authStore.user?.createTime) {
    return authStore.user.createTime.slice(0, 10)
  }
  return '——'
})

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: ''
})

const rules = {
  nickname: [
    { max: 20, message: '昵称不超过20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

function loadProfile() {
  if (authStore.user) {
    form.username = authStore.user.username || ''
    form.nickname = authStore.user.nickname || ''
    form.email = authStore.user.email || ''
    form.phone = authStore.user.phone || ''
  }
}

onMounted(async () => {
  await authStore.fetchProfile()
  loadProfile()
})

function triggerUpload() {
  fileInput.value?.click()
}

function handleAvatarChange(e) {
  const file = e.target.files?.[0]
  if (file) {
    ElMessage.info('头像上传功能即将上线')
    // Clear input so re-selecting same file triggers change
    if (fileInput.value) fileInput.value.value = ''
  }
}

async function handleSave() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await authStore.updateProfile({
      nickname: form.nickname,
      email: form.email,
      phone: form.phone
    })
    profileSuccess.value = true
    setTimeout(() => { profileSuccess.value = false }, 3000)
  } catch (e) {
    // Error handled by interceptor
  } finally {
    saving.value = false
  }
}

function resetForm() {
  loadProfile()
  formRef.value?.clearValidate()
}

async function handleChangePassword() {
  if (!pwdFormRef.value) return
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return

  pwdSaving.value = true
  try {
    await authStore.changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    passwordSuccess.value = true
    setTimeout(() => { passwordSuccess.value = false }, 3000)
    resetPwdForm()
    showPasswordForm.value = false
  } catch (e) {
    // Error handled by interceptor
  } finally {
    pwdSaving.value = false
  }
}

function resetPwdForm() {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdFormRef.value?.clearValidate()
}

function handleLogout() {
  authStore.logout()
}
</script>

<style scoped>
.profile-page {
  min-height: 80vh;
  background: var(--bg-primary, #faf6f0);
  padding: 40px 20px;
}

.profile-container {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary, #2c2c2c);
  margin: 0 0 6px 0;
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-secondary, #999);
  margin: 0;
}

.profile-content {
  display: flex;
  gap: 28px;
}

/* Sidebar */
.profile-sidebar {
  width: 260px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  height: fit-content;
}

.avatar-section {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color, #f0ebe4);
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 12px;
}

.profile-avatar {
  background: var(--primary-color, #8b7355);
  font-size: 36px;
  font-weight: 600;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  color: #fff;
  font-size: 11px;
  opacity: 0;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.avatar-overlay .el-icon {
  font-size: 18px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary, #2c2c2c);
  margin: 0 0 4px 0;
}

.user-role {
  font-size: 13px;
  color: var(--primary-color, #8b7355);
  margin: 0;
}

.sidebar-menu {
  margin-bottom: 24px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  font-size: 14px;
  color: var(--text-secondary, #666);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background: var(--hover-bg, #f8f6f3);
  color: var(--primary-color, #8b7355);
}

.menu-item.active {
  background: var(--primary-light, #f5f0ea);
  color: var(--primary-color, #8b7355);
  font-weight: 500;
}

.menu-item .el-icon {
  font-size: 18px;
}

.sidebar-stats {
  padding-top: 24px;
  border-top: 1px solid var(--border-color, #f0ebe4);
}

.stat-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 8px;
}

.stat-label {
  color: var(--text-secondary, #999);
}

.stat-value {
  color: var(--text-primary, #2c2c2c);
}

/* Main */
.profile-main {
  flex: 1;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.form-section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary, #2c2c2c);
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--primary-color, #8b7355);
  display: inline-block;
}

.profile-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.profile-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-primary, #2c2c2c);
  font-size: 14px;
}

.form-input {
  max-width: 360px;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  border: 1px solid var(--border-color, #e8e0d6);
  box-shadow: none;
  padding: 2px 14px;
}

.form-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color, #8b7355);
}

.form-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color, #8b7355);
  box-shadow: 0 0 0 2px rgba(139, 115, 85, 0.1);
}

.form-input :deep(.el-input__inner) {
  height: 42px;
}

.form-input :deep(.el-input.is-disabled .el-input__wrapper) {
  background: #f5f5f5;
  border-color: #e8e0d6;
}

.form-input :deep(.el-input.is-disabled .el-input__inner) {
  color: #999;
  -webkit-text-fill-color: #999;
}

.form-tip {
  font-size: 12px;
  color: #bbb;
  margin-top: 4px;
}

.save-btn {
  background: var(--primary-color, #8b7355);
  border: none;
  border-radius: 10px;
  padding: 12px 32px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.save-btn:hover {
  background: var(--primary-hover, #7a6448);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 115, 85, 0.3);
}

.reset-btn {
  border-radius: 10px;
  border: 1px solid var(--border-color, #e8e0d6);
  color: var(--text-secondary, #666);
  padding: 12px 24px;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  border-color: var(--primary-color, #8b7355);
  color: var(--primary-color, #8b7355);
}

/* Password Section */
.password-section {
  margin-top: 8px;
}

.password-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  margin-bottom: 20px;
}

.password-section-header .form-section-title {
  margin-bottom: 0;
}

.toggle-pwd-btn {
  border-radius: 8px;
  border: 1px solid var(--border-color, #e8e0d6);
  color: var(--text-secondary, #666);
  transition: all 0.3s ease;
}

.toggle-pwd-btn:hover {
  border-color: var(--primary-color, #8b7355);
  color: var(--primary-color, #8b7355);
}

.password-form {
  padding: 20px 0 8px 0;
}

/* Logout Section */
.logout-section {
  margin-top: 8px;
}

.logout-btn {
  border-radius: 10px;
  padding: 12px 32px;
  font-weight: 500;
}

/* Alert messages */
.alert-message {
  margin-bottom: 24px;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }
  .profile-sidebar {
    width: 100%;
  }
  .profile-main {
    padding: 24px 20px;
  }
  .page-title {
    font-size: 24px;
  }
}
</style>
