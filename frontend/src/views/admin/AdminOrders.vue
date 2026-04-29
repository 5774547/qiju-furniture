<template>
  <div class="page">
    <div class="page-header">
      <h2>📋 订单管理</h2>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="fetchOrders" style="width:140px">
        <el-option label="全部" value="" />
        <el-option label="待支付" :value="0" />
        <el-option label="已支付" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
    </div>

    <el-table :data="filteredOrders" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系人" width="120">
        <template #default="{ row }">{{ row.customerName }}</template>
      </el-table-column>
      <el-table-column label="电话" width="130">
        <template #default="{ row }">{{ row.customerPhone }}</template>
      </el-table-column>
      <el-table-column label="总金额" width="100">
        <template #default="{ row }">
          <span style="color:var(--el-color-danger)">¥{{ row.finalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" min-width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
          <el-dropdown v-if="row.status < 3" @command="(v) => updateStatus(row.id, v)">
            <el-button size="small" type="primary">
              更新状态<el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="row.status === 0" :command="1">标记已支付</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 1" :command="2">标记已发货</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 2" :command="3">标记已完成</el-dropdown-item>
                <el-dropdown-item v-if="row.status <= 1" :command="4" divided>取消订单</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- Order Detail Dialog -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detail.status)" size="small">{{ statusText(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.customerName }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ detail.customerPhone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ detail.customerEmail }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ detail.address }}</el-descriptions-item>
          <el-descriptions-item label="商品总额">¥{{ detail.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="优惠">-¥{{ detail.discountAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="实付">
            <span style="color:var(--el-color-danger);font-weight:700">¥{{ detail.finalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="优惠券">{{ detail.couponCode || '无' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.notes || '无' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ detail.createTime }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin:20px 0 12px">商品明细</h4>
        <el-table :data="detail.items" border size="small">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column label="图片" width="60">
            <template #default="{ row }">
              <el-image :src="row.productImage" style="width:40px;height:40px" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="80">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="60" />
          <el-table-column prop="subtotal" label="小计" width="80">
            <template #default="{ row }">¥{{ row.subtotal }}</template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAdminOrders, getAdminOrderDetail, updateOrderStatus } from '@/api/admin'

const loading = ref(false)
const orders = ref([])
const statusFilter = ref('')
const detailVisible = ref(false)
const detail = ref(null)

const filteredOrders = computed(() => {
  if (statusFilter.value === '') return orders.value
  return orders.value.filter(o => o.status === Number(statusFilter.value))
})

function statusText(status) {
  const map = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

function statusType(status) {
  const map = { 0: 'warning', 1: 'success', 2: 'primary', 3: '', 4: 'info' }
  return map[status] || 'info'
}

async function fetchOrders() {
  loading.value = true
  try {
    orders.value = await getAdminOrders()
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

async function viewDetail(row) {
  try {
    detail.value = await getAdminOrderDetail(row.id)
    detailVisible.value = true
  } catch {
    ElMessage.error('获取订单详情失败')
  }
}

async function updateStatus(id, status) {
  try {
    await updateOrderStatus(id, status)
    ElMessage.success('状态已更新')
    await fetchOrders()
  } catch {
    ElMessage.error('更新失败')
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}
</style>
