<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="footer-grid">
        <div class="footer-col brand-col">
          <div class="footer-logo">
            <span class="logo-icon">&#x1f3e0;</span>
            <span class="logo-text">栖居家具</span>
          </div>
          <p class="brand-desc">匠心品质，自然生活。我们致力于为每一个家庭打造温馨、舒适、富有格调的家居空间。</p>
          <div class="social-links">
            <a href="#" class="social-link"><el-icon :size="20"><ChatRound /></el-icon></a>
            <a href="#" class="social-link"><el-icon :size="20"><Monitor /></el-icon></a>
            <a href="#" class="social-link"><el-icon :size="20"><Message /></el-icon></a>
          </div>
        </div>

        <div class="footer-col">
          <h4 class="footer-title">产品分类</h4>
          <ul class="footer-links">
            <li><a href="#" @click.prevent="scrollToTop">客厅家具</a></li>
            <li><a href="#" @click.prevent="scrollToTop">卧室家具</a></li>
            <li><a href="#" @click.prevent="scrollToTop">餐厅家具</a></li>
            <li><a href="#" @click.prevent="scrollToTop">书房家具</a></li>
          </ul>
        </div>

        <div class="footer-col">
          <h4 class="footer-title">客户服务</h4>
          <ul class="footer-links">
            <li><a href="#">配送说明</a></li>
            <li><a href="#">退换政策</a></li>
            <li><a href="#">售后服务</a></li>
            <li><a href="#">常见问题</a></li>
          </ul>
        </div>

        <div class="footer-col newsletter-col">
          <h4 class="footer-title">订阅资讯</h4>
          <p class="newsletter-desc">订阅我们的资讯，获取最新产品信息和优惠活动</p>
          <div class="newsletter-form">
            <el-input
              v-model="newsletterEmail"
              placeholder="输入您的邮箱"
              size="large"
              class="newsletter-input"
              :prefix-icon="Message"
            />
            <el-button type="primary" size="large" @click="handleSubscribe" :loading="subscribing">
              订阅
            </el-button>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <span class="copyright">&copy; 2024 栖居家具. All rights reserved.</span>
        <div class="footer-bottom-links">
          <a href="#">隐私政策</a>
          <a href="#">服务条款</a>
          <a href="#">联系我们</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { subscribeNewsletter } from '@/api/newsletter'
import { ChatRound, Monitor, Message } from '@element-plus/icons-vue'

const newsletterEmail = ref('')
const subscribing = ref(false)

async function handleSubscribe() {
  if (!newsletterEmail.value || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(newsletterEmail.value)) {
    ElMessage.warning('请输入有效的邮箱地址')
    return
  }
  subscribing.value = true
  try {
    await subscribeNewsletter({ email: newsletterEmail.value })
    ElMessage.success('订阅成功！感谢您的关注！')
    newsletterEmail.value = ''
  } catch (e) {
    // Error already handled by interceptor
  } finally {
    subscribing.value = false
  }
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.footer {
  background: var(--footer-bg);
  border-top: 1px solid var(--border-color);
  padding: 60px 0 0;
  margin-top: 60px;
}

.footer-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.5fr;
  gap: 40px;
  padding-bottom: 40px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 2px;
}

.brand-desc {
  color: var(--text-tertiary);
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 20px;
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: var(--hover-bg);
  color: var(--text-tertiary);
  text-decoration: none;
  transition: all 0.3s ease;
}

.social-link:hover {
  color: var(--primary-color);
  background: var(--primary-light);
  transform: translateY(-2px);
}

.footer-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: var(--text-tertiary);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: var(--primary-color);
}

.newsletter-desc {
  color: var(--text-tertiary);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
}

.newsletter-form {
  display: flex;
  gap: 8px;
}

.newsletter-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  background: var(--input-bg);
  box-shadow: none;
  border: 1px solid var(--border-color);
}

.newsletter-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color);
}

.newsletter-form .el-button {
  border-radius: 10px;
  flex-shrink: 0;
  padding: 0 24px;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 0;
  border-top: 1px solid var(--border-color);
}

.copyright {
  color: var(--text-tertiary);
  font-size: 13px;
}

.footer-bottom-links {
  display: flex;
  gap: 24px;
}

.footer-bottom-links a {
  color: var(--text-tertiary);
  text-decoration: none;
  font-size: 13px;
  transition: color 0.3s ease;
}

.footer-bottom-links a:hover {
  color: var(--primary-color);
}

@media (max-width: 768px) {
  .footer-inner {
    padding: 0 16px;
  }
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  .newsletter-form {
    flex-direction: column;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}
</style>
