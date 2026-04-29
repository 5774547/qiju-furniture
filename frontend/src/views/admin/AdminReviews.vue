<template>
  <div class="page">
    <div class="page-header">
      <h2>⭐ 评价管理</h2>
    </div>

    <el-table :data="reviews" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="productId" label="商品ID" width="80" />
      <el-table-column prop="reviewerName" label="评价人" width="120" />
      <el-table-column label="评分" width="120">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="createTime" label="评价时间" width="170" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminReviews, deleteAdminReview } from '@/api/admin'

const loading = ref(false)
const reviews = ref([])

async function fetchReviews() {
  loading.value = true
  try {
    reviews.value = await getAdminReviews()
  } catch {
    reviews.value = []
  } finally {
    loading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定删除「${row.reviewerName}」的这条评价？`,
      '提示',
      { type: 'warning' }
    )
    await deleteAdminReview(row.id)
    ElMessage.success('已删除')
    await fetchReviews()
  } catch {}
}

onMounted(fetchReviews)
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
