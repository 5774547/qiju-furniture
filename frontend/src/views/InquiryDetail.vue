<template>
  <div class="inquiry-detail-page" v-loading="loading">
    <div class="detail-inner" v-if="inquiry">
      <div class="page-header">
        <el-button text @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回询价单列表
        </el-button>
      </div>

      <div class="inquiry-status-bar">
        <el-steps :active="currentStep" align-center>
          <el-step title="待报价" icon="ChatDotSquare" />
          <el-step title="已报价" icon="Money" />
          <el-step title="已确认" icon="Check" />
          <el-step title="已关闭" icon="CircleClose" />
        </el-steps>
      </div>

      <div class="inquiry-info-card">
        <div class="info-header">
          <h3 class="info-title">询价信息</h3>
          <el-tag :type="statusType" effect="dark" size="large">
            {{ statusText }}
          </el-tag>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">询价编号</span>
            <span class="info-value">{{ inquiry.id }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">提交时间</span>
            <span class="info-value">{{ formatDate(inquiry.createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系人</span>
            <span class="info-value">{{ inquiry.contactName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ inquiry.contactPhone }}</span>
          </div>
          <div class="info-item" v-if="inquiry.companyName">
            <span class="info-label">公司名称</span>
            <span class="info-value">{{ inquiry.companyName }}</span>
          </div>
          <div class="info-item full-width" v-if="inquiry.remarks">
            <span class="info-label">备注信息</span>
            <span class="info-value">{{ inquiry.remarks }}</span>
          </div>
        </div>
      </div>

      <!-- Quotation Info -->
      <div class="quotation-card" v-if="inquiry.quotationAmount">
        <h3 class="card-title">报价信息</h3>
        <div class="quotation-detail">
          <div class="quotation-row">
            <span class="quotation-label">报价金额</span>
            <span class="quotation-amount">¥{{ inquiry.quotationAmount?.toFixed(2) }}</span>
          </div>
          <div class="quotation-row" v-if="inquiry.quotationValidUntil">
            <span class="quotation-label">报价有效期</span>
            <span class="quotation-value">{{ formatDate(inquiry.quotationValidUntil) }}</span>
          </div>
          <div class="quotation-row" v-if="inquiry.adminRemarks">
            <span class="quotation-label">管理员备注</span>
            <span class="quotation-value">{{ inquiry.adminRemarks }}</span>
          </div>
        </div>
      </div>

      <div class="inquiry-items-card">
        <h3 class="card-title">商品清单</h3>
        <div class="inquiry-items">
          <div class="inquiry-item" v-for="item in (inquiry.items || [])" :key="item.id || item.productId">
            <img
              :src="item.productImage || item.image || 'https://via.placeholder.com/80x80/f5f0eb/8B7355'"
              :alt="item.productName || item.name"
              class="item-image"
              @error="onInquiryDetailImgError"
            />
            <div class="item-info">
              <span class="item-name">{{ item.productName || item.name }}</span>
              <span class="item-price">¥{{ (item.price || 0)?.toFixed(2) }}</span>
            </div>
            <span class="item-qty">x{{ item.quantity }}</span>
            <span class="item-subtotal">
              ¥{{ ((item.price || 0) * item.quantity)?.toFixed(2) }}
            </span>
          </div>
        </div>

        <div class="inquiry-total-row" v-if="inquiry.quotationAmount">
          <div class="total-line grand">
            <span>报价合计</span>
            <span class="total-amount">¥{{ inquiry.quotationAmount?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else-if="!loading" description="询价单未找到" :image-size="200" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getInquiryDetail } from '@/api/inquiry'
import { ArrowLeft, ChatDotSquare, Money, Check, CircleClose } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const inquiry = ref(null)
const loading = ref(true)

function onInquiryDetailImgError(e) {
  e.target.src = 'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80"><rect fill="#f0ebe5" width="80" height="80"/><text x="40" y="45" text-anchor="middle" fill="#b8860b" font-size="10">商品</text></svg>'
  )
}

const statusType = computed(() => {
  const map = {
    'PENDING': 'warning',
    'QUOTED': 'primary',
    'CONFIRMED': 'success',
    'CLOSED': 'info'
  }
  return map[inquiry.value?.status] || 'info'
})

const statusText = computed(() => {
  const map = {
    'PENDING': '待报价',
    'QUOTED': '已报价',
    'CONFIRMED': '已确认',
    'CLOSED': '已关闭'
  }
  return map[inquiry.value?.status] || inquiry.value?.status
})

const currentStep = computed(() => {
  const map = {
    'PENDING': 0,
    'QUOTED': 1,
    'CONFIRMED': 2,
    'CLOSED': 3
  }
  return map[inquiry.value?.status] ?? 0
})

function formatDate(date) {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

function goBack() {
  router.push('/inquiries')
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getInquiryDetail(route.params.id)
    inquiry.value = res
  } catch (e) {
    inquiry.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.inquiry-detail-page {
  padding-top: 90px;
  min-height: 80vh;
}

.detail-inner {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 40px 60px;
}

.page-header {
  padding: 16px 0;
}

.back-btn {
  font-size: 15px;
  color: var(--text-secondary);
}

.back-btn:hover {
  color: var(--primary-color);
}

.inquiry-status-bar {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 24px;
}

.inquiry-status-bar :deep(.el-step__title) {
  font-size: 13px;
}

.inquiry-info-card,
.quotation-card,
.inquiry-items-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.info-title,
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 13px;
  color: var(--text-tertiary);
}

.info-value {
  font-size: 15px;
  color: var(--text-primary);
  font-weight: 500;
}

.quotation-detail {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quotation-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.quotation-label {
  font-size: 14px;
  color: var(--text-secondary);
  min-width: 100px;
}

.quotation-amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
}

.quotation-value {
  font-size: 15px;
  color: var(--text-primary);
  font-weight: 500;
}

.inquiry-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.inquiry-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: 12px;
}

.item-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
  background: var(--bg-primary);
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.item-price {
  font-size: 13px;
  color: var(--text-tertiary);
}

.item-qty {
  font-size: 14px;
  color: var(--text-tertiary);
}

.item-subtotal {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  min-width: 80px;
  text-align: right;
}

.inquiry-total-row {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.total-line {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.total-line.grand {
  font-size: 16px;
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
}

@media (max-width: 768px) {
  .detail-inner {
    padding: 0 16px;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
