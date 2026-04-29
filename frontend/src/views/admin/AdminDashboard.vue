<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>📊 仪表盘</h2>
      <el-button @click="fetchStats" :icon="Refresh" circle />
    </div>

    <div class="stats-grid" v-loading="loading">
      <div class="stat-card card-products">
        <div class="stat-icon">📦</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalProducts }}</div>
          <div class="stat-label">商品总数</div>
          <div class="stat-sub">已上架 {{ stats.onlineProducts }}</div>
        </div>
      </div>

      <div class="stat-card card-orders">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalOrders }}</div>
          <div class="stat-label">订单总数</div>
          <div class="stat-sub">待处理 {{ stats.pendingOrders }}</div>
        </div>
      </div>

      <div class="stat-card card-revenue">
        <div class="stat-icon">💰</div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatRevenue }}</div>
          <div class="stat-label">总营业额</div>
          <div class="stat-sub">已支付订单</div>
        </div>
      </div>

      <div class="stat-card card-users">
        <div class="stat-icon">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">注册用户</div>
          <div class="stat-sub">累计注册</div>
        </div>
      </div>

      <div class="stat-card card-reviews">
        <div class="stat-icon">⭐</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalReviews }}</div>
          <div class="stat-label">用户评价</div>
          <div class="stat-sub">累计评价</div>
        </div>
      </div>

      <div class="stat-card card-pending">
        <div class="stat-icon">⏳</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingOrders }}</div>
          <div class="stat-label">待付款订单</div>
          <div class="stat-sub">需关注处理</div>
        </div>
      </div>
    </div>

    <!-- Order status breakdown -->
    <div class="section">
      <h3>📈 订单状态分布</h3>
      <div class="status-bar">
        <div
          v-for="s in statusData"
          :key="s.label"
          class="status-segment"
          :style="{ width: s.percent + '%', background: s.color }"
          :title="`${s.label}: ${s.count}`"
        />
      </div>
      <div class="status-legend">
        <span v-for="s in statusData" :key="s.label" class="legend-item">
          <span class="legend-dot" :style="{ background: s.color }" />
          {{ s.label }}: {{ s.count }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/admin'

const loading = ref(false)
const stats = ref({
  totalProducts: 0,
  onlineProducts: 0,
  totalOrders: 0,
  pendingOrders: 0,
  paidOrders: 0,
  shippedOrders: 0,
  totalRevenue: 0,
  totalUsers: 0,
  totalReviews: 0
})

const formatRevenue = computed(() => {
  const num = Number(stats.value.totalRevenue)
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
})

const statusData = computed(() => {
  const s = stats.value
  const total = s.totalOrders || 1
  return [
    { label: '待付款', count: s.pendingOrders, percent: (s.pendingOrders / total) * 100, color: '#f59e0b' },
    { label: '已支付', count: s.paidOrders, percent: (s.paidOrders / total) * 100, color: '#10b981' },
    { label: '已发货', count: s.shippedOrders, percent: (s.shippedOrders / total) * 100, color: '#3b82f6' },
    { label: '已完成/其他', count: total - s.pendingOrders - s.paidOrders - s.shippedOrders, percent: ((total - s.pendingOrders - s.paidOrders - s.shippedOrders) / total) * 100, color: '#6b7280' }
  ]
})

async function fetchStats() {
  loading.value = true
  try {
    stats.value = await getDashboardStats()
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  color: var(--text-primary, #333);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  font-size: 36px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.card-products .stat-icon { background: #fef3c7; }
.card-orders .stat-icon { background: #dbeafe; }
.card-revenue .stat-icon { background: #d1fae5; }
.card-users .stat-icon { background: #ede9fe; }
.card-reviews .stat-icon { background: #fce7f3; }
.card-pending .stat-icon { background: #fff7ed; }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.stat-sub {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.section h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: var(--text-primary, #333);
}

.status-bar {
  display: flex;
  height: 24px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;
}

.status-segment {
  transition: width 0.5s ease;
}

.status-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

[data-theme="dark"] .stat-card,
[data-theme="dark"] .section {
  background: #1e1e1e;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

[data-theme="dark"] .stat-value {
  color: #e0e0e0;
}

[data-theme="dark"] .stat-label {
  color: #999;
}

@media (max-width: 900px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
