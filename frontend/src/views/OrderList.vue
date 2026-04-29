<template>
  <div class="order-list-page">
    <div class="order-inner">
      <div class="page-header">
        <h1 class="page-title">我的订单</h1>
      </div>

      <div v-loading="loading">
        <div class="order-cards" v-if="orders.length > 0">
          <div class="order-card" v-for="order in orders" :key="order.id" @click="goToDetail(order.id)">
            <div class="order-header">
              <div class="order-info-left">
                <span class="order-id">订单号：{{ order.id }}</span>
                <span class="order-date">{{ formatDate(order.createdAt) }}</span>
              </div>
              <el-tag :type="statusType(order.status)" effect="dark" size="large">
                {{ statusText(order.status) }}
              </el-tag>
            </div>

            <div class="order-items">
              <div class="order-item" v-for="item in (order.items || []).slice(0, 3)" :key="item.id">
                <img
                  :src="item.productImage || item.image || 'https://via.placeholder.com/80x80/f5f0eb/8B7355'"
                  :alt="item.productName || item.name"
                  class="order-item-img"
                  @error="onOrderImgError"
                />
                <div class="order-item-info">
                  <span class="order-item-name">{{ item.productName || item.name }}</span>
                  <span class="order-item-qty">x{{ item.quantity }}</span>
                </div>
                <span class="order-item-price">¥{{ (item.price || item.productPrice)?.toFixed(2) }}</span>
              </div>
              <div class="more-items" v-if="order.items && order.items.length > 3">
                还有 {{ order.items.length - 3 }} 件商品
              </div>
            </div>

            <div class="order-footer">
              <div class="order-total">
                <span class="total-label">合计：</span>
                <span class="total-value">¥{{ order.totalAmount?.toFixed(2) }}</span>
              </div>
              <div class="order-actions">
                <el-button size="small" round @click.stop="goToDetail(order.id)">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无订单记录" :image-size="200">
          <template #description>
            <p class="empty-text">您还没有下过订单</p>
          </template>
          <router-link to="/">
            <el-button type="primary" size="large">去购物</el-button>
          </router-link>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrders, getMyOrders } from '@/api/order'
import { useAuthStore } from '@/stores/auth'
import dayjs from 'dayjs'

const router = useRouter()
const authStore = useAuthStore()
const orders = ref([])
const loading = ref(true)

function onOrderImgError(e) {
  e.target.src = 'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80"><rect fill="#f0ebe5" width="80" height="80"/><text x="40" y="45" text-anchor="middle" fill="#b8860b" font-size="10">商品</text></svg>'
  )
}

function statusType(status) {
  const map = {
    'PENDING': 'warning',
    'PROCESSING': 'primary',
    'SHIPPED': 'info',
    'DELIVERED': 'success',
    'COMPLETED': 'success',
    'CANCELLED': 'danger',
    'REFUNDED': 'danger'
  }
  return map[status] || 'info'
}

function statusText(status) {
  const map = {
    'PENDING': '待付款',
    'PROCESSING': '处理中',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return map[status] || status
}

function formatDate(date) {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
}

function goToDetail(id) {
  router.push(`/order/${id}`)
}

onMounted(async () => {
  loading.value = true
  try {
    // Use user-specific orders API if logged in, otherwise use session-based orders
    if (authStore.isLoggedIn) {
      const res = await getMyOrders()
      orders.value = Array.isArray(res) ? res : (res?.records || res?.content || [])
    } else {
      const res = await getOrders()
      orders.value = Array.isArray(res) ? res : (res?.records || res?.content || [])
    }
  } catch (e) {
    orders.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-list-page {
  padding-top: 90px;
  min-height: 80vh;
}

.order-inner {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 40px 60px;
}

.page-header {
  padding: 20px 0;
}

.page-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.order-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 8px 24px var(--shadow-color);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.order-info-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-id {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.order-date {
  font-size: 13px;
  color: var(--text-tertiary);
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-item-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
  background: var(--bg-secondary);
}

.order-item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-item-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.order-item-qty {
  font-size: 13px;
  color: var(--text-tertiary);
}

.order-item-price {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.more-items {
  font-size: 13px;
  color: var(--text-tertiary);
  text-align: center;
  padding: 4px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.order-total {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.total-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.total-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
}

.empty-text {
  color: var(--text-tertiary);
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .order-inner {
    padding: 0 16px;
  }
}
</style>
