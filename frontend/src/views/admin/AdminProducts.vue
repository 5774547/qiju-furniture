<template>
  <div class="page">
    <div class="page-header">
      <h2>📦 商品管理</h2>
      <el-button type="primary" @click="showDialog = true; isEdit = false; form = {}">
        + 新增商品
      </el-button>
    </div>

    <el-table :data="products" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image :src="row.image || row.productImage" style="width:50px;height:50px" fit="cover" :preview-src-list="[row.image || row.productImage]" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="140" />
      <el-table-column prop="category" label="分类" width="80" />
      <el-table-column label="价格" width="120">
        <template #default="{ row }">
          <span style="color:var(--el-color-danger)">¥{{ row.price }}</span>
          <del v-if="row.originalPrice" style="color:#999;margin-left:6px;font-size:12px">¥{{ row.originalPrice }}</del>
        </template>
      </el-table-column>
      <el-table-column prop="stockCount" label="库存" width="70" />
      <el-table-column prop="status" label="状态" width="70">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="editProduct(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Edit Dialog -->
    <el-dialog v-model="showDialog" :title="isEdit ? '编辑商品' : '新增商品'" width="600px">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="沙发" value="沙发" />
            <el-option label="桌子" value="桌子" />
            <el-option label="椅子" value="椅子" />
            <el-option label="床" value="床" />
            <el-option label="柜子" value="柜子" />
            <el-option label="灯饰" value="灯饰" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stockCount" :min="0" style="width:200px" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.image" placeholder="http://localhost:9000/qiju-furniture/products/..." />
          <div v-if="form.image" style="margin-top:8px">
            <el-image :src="form.image" style="width:80px;height:80px" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tag" clearable style="width:200px">
            <el-option label="新品" value="new" />
            <el-option label="热销" value="hot" />
            <el-option label="特价" value="sale" />
          </el-select>
        </el-form-item>
        <el-form-item label="详情">
          <el-input v-model="form.detail" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProduct" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminProducts, createProduct, updateProduct, deleteProduct } from '@/api/admin'

const products = ref([])
const loading = ref(true)
const showDialog = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref(null)
const form = ref({})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

async function fetchProducts() {
  loading.value = true
  try {
    const res = await getAdminProducts()
    products.value = res || []
  } catch {
    products.value = []
  } finally {
    loading.value = false
  }
}

function editProduct(row) {
  isEdit.value = true
  form.value = { ...row }
  showDialog.value = true
}

async function saveProduct() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateProduct(form.value.id, form.value)
      ElMessage.success('修改成功')
    } else {
      await createProduct(form.value)
      ElMessage.success('新增成功')
    }
    showDialog.value = false
    await fetchProducts()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', { type: 'warning' })
    await deleteProduct(row.id)
    ElMessage.success('已删除')
    await fetchProducts()
  } catch {}
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateProduct(row.id, { status: newStatus })
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    await fetchProducts()
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(fetchProducts)
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
