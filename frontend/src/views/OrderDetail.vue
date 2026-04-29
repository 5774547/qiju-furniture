<template>
  <div class="order-detail-page" v-loading="loading">
    <div class="detail-inner" v-if="order">
      <div class="page-header">
        <el-button text @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回订单列表
        </el-button>
      </div>

      <div class="order-status-bar">
        <el-steps :active="currentStep" align-center>
          <el-step title="待付款" icon="Wallet" />
          <el-step title="处理中" icon="Setting" />
          <el-step title="已发货" icon="Van" />
          <el-step title="已完成" icon="Check" />
        </el-steps>
      </div>

      <div class="order-info-card">
        <div class="info-header">
          <h3 class="info-title">订单信息</h3>
          <el-tag :type="statusType" effect="dark" size="large">
            {{ statusText }}
          </el-tag>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">订单编号</span>
            <span class="info-value">{{ order.id }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">下单时间</span>
            <span class="info-value">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">收件人</span>
            <span class="info-value">{{ order.contactName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ order.contactPhone }}</span>
          </div>
          <div class="info-item full-width">
            <span class="info-label">收货地址</span>
            <span class="info-value">{{ order.contactAddress }}</span>
          </div>
          <div class="info-item" v-if="order.couponCode">
            <span class="info-label">优惠券</span>
            <span class="info-value">{{ order.couponCode }}</span>
          </div>
        </div>
      </div>

      <div class="order-items-card">
        <h3 class="card-title">商品列表</h3>
        <div class="order-items">
          <div class="order-item" v-for="item in (order.items || [])" :key="item.id || item.productId">
            <img
              :src="item.productImage || item.image || 'https://via.placeholder.com/80x80/f5f0eb/8B7355'"
              :alt="item.productName || item.name"
              class="item-image"
              @error="onOrderDetailImgError"
            />
            <div class="item-info">
              <span class="item-name">{{ item.productName || item.name }}</span>
              <span class="item-price">¥{{ (item.price || item.productPrice)?.toFixed(2) }}</span>
            </div>
            <span class="item-qty">x{{ item.quantity }}</span>
            <span class="item-subtotal">
              ¥{{ ((item.price || item.productPrice) * item.quantity)?.toFixed(2) }}
            </span>
          </div>
        </div>

        <div class="order-total-row">
          <div class="total-line" v-if="order.discount">
            <span>优惠</span>
            <span>-¥{{ order.discount?.toFixed(2) }}</span>
          </div>
          <div class="total-line grand">
            <span>合计</span>
            <span class="total-amount">¥{{ order.totalAmount?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else-if="!loading" description="订单未找到" :image-size="200" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrder } from '@/api/order'
import { ArrowLeft, Wallet, Setting, Van, Check } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)

function onOrderDetailImgError(e) {
  e.target.src = 'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80"><rect fill="#f0ebe5" width="80" height="80"/><text x="40" y="45" text-anchor="middle" fill="#b8860b" font-size="10">商品</text></svg>'
  )
}

const statusType = computed(() => {
  const map = {
    'PENDING': 'warning',
    'PROCESSING': 'primary',
    'SHIPPED': 'info',
    'DELIVERED': 'success',
    'COMPLETED': 'success',
    'CANCELLED': 'danger',
    'REFUNDED': 'danger'
  }
  return map[order.value?.status] || 'info'
})

const statusText = computed(() => {
  const map = {
    'PENDING': '待付款',
    'PROCESSING': '处理中',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return map[order.value?.status] || order.value?.status
})

const currentStep = computed(() => {
  const map = {
    'PENDING': 0,
    'PROCESSING': 1,
    'SHIPPED': 2,
    'DELIVERED': 3,
    'COMPLETED': 3
  }
  return map[order.value?.status] ?? 0
})

function formatDate(date) {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

function goBack() {
  router.push('/order')
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getOrder(route.params.id)
    order.value = res
  } catch (e) {
    order.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-detail-page {
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

.order-status-bar {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 24px;
}

.order-status-bar :deep(.el-step__title) {
  font-size: 13px;
}

.order-info-card,
.order-items-card {
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

.order-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
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

.order-total-row {
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
