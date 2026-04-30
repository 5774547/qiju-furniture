<template>
  <footer class="footer">
    <div class="footer-inner">
      <div class="footer-grid">
        <div class="footer-col brand-col">
          <div class="footer-logo">
            <span class="logo-icon">&#x1f3e0;</span>
            <span class="logo-text">栖居家具</span>
          </div>
          <p class="brand-desc">匠心品质，直供源头。工厂直营，批发定制，为您的项目提供优质家具解决方案。</p>
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
            <li><a href="#">批发说明</a></li>
            <li><a href="#">定制服务</a></li>
            <li><a href="#">售后服务</a></li>
            <li><a href="#">常见问题</a></li>
          </ul>
        </div>

        <div class="footer-col contact-col">
          <h4 class="footer-title">联系我们</h4>
          <p class="contact-desc">如有采购需求，欢迎联系我们获取报价</p>
          <div class="contact-info">
            <div class="contact-item">
              <el-icon><Phone /></el-icon>
              <span>400-888-8888</span>
            </div>
            <div class="contact-item">
              <el-icon><Message /></el-icon>
              <span>contact@qijufurniture.com</span>
            </div>
            <router-link to="/inquiry-list" class="contact-btn">
              <el-button type="primary" size="large" round>立即询价</el-button>
            </router-link>
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
import { ChatRound, Monitor, Message, Phone } from '@element-plus/icons-vue'

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

.contact-desc {
  color: var(--text-tertiary);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 13px;
}

.contact-btn {
  margin-top: 8px;
  text-decoration: none;
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
  .contact-form {
    flex-direction: column;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}
</style>
