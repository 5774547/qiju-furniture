<template>
  <div class="inquiry-list-page">
    <div class="inquiry-inner">
      <div class="page-header">
        <h1 class="page-title">询价清单</h1>
        <span class="page-subtitle" v-if="inquiryStore.items.length">{{ inquiryStore.count }} 件商品</span>
      </div>

      <div v-if="inquiryStore.items.length > 0" class="inquiry-content">
        <!-- Inquiry Items -->
        <div class="inquiry-items">
          <div class="inquiry-item" v-for="item in inquiryStore.items" :key="item.id">
            <div class="item-image">
              <img :src="item.productImage || item.image || ''" :alt="item.productName || item.name" @error="onImgError($event, item.productName || item.name)" />
            </div>
            <div class="item-info">
              <h3 class="item-name">{{ item.productName || item.name }}</h3>
              <span class="item-category" v-if="item.category">{{ item.category }}</span>
              <div class="item-price-row">
                <span class="item-wholesale-price">批发价: ¥{{ (item.wholesalePrice || item.wholesalePrice === 0 ? item.wholesalePrice : item.price || 0)?.toFixed(2) }}</span>
                <span class="item-retail-price">零售价: ¥{{ (item.price || 0)?.toFixed(2) }}</span>
              </div>
              <span class="item-unit" v-if="item.unit">单位: {{ item.unit }}</span>
            </div>
            <div class="item-quantity">
              <el-input-number
                :model-value="item.quantity"
                :min="1"
                :max="item.stock || 9999"
                size="small"
                controls-position="right"
                @change="(val) => handleQuantityChange(item, val)"
              />
            </div>
            <button class="item-remove" @click="handleRemove(item)">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
        </div>

        <!-- Inquiry Summary -->
        <div class="inquiry-summary">
          <div class="summary-inner">
            <h4 class="summary-label">询价清单</h4>
            <div class="summary-info">
              <p class="summary-count">共 {{ inquiryStore.count }} 件商品</p>
              <p class="summary-hint">提交询价后，我们将尽快为您提供报价</p>
            </div>

            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              @click="showInquiryDialog = true"
              :disabled="inquiryStore.items.length === 0"
            >
              提交询价
            </el-button>

            <el-button class="clear-btn" @click="handleClearList">
              清空清单
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="询价清单是空的" :image-size="200">
        <template #description>
          <p class="empty-text">询价清单还是空的，去产品页面看看吧！</p>
        </template>
        <router-link to="/">
          <el-button type="primary" size="large">去逛逛</el-button>
        </router-link>
      </el-empty>
    </div>

    <!-- Inquiry Dialog -->
    <el-dialog
      v-model="showInquiryDialog"
      title="提交询价"
      width="520px"
      class="inquiry-dialog"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="inquiryForm"
        :rules="inquiryRules"
        label-width="100px"
        size="large"
      >
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="inquiryForm.contactName" placeholder="请输入您的姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="inquiryForm.contactPhone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="inquiryForm.companyName" placeholder="请输入公司名称（选填）" />
        </el-form-item>
        <el-form-item label="备注信息" prop="remarks">
          <el-input
            v-model="inquiryForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请输入其他需求或备注（选填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showInquiryDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitInquiry" :loading="submitting">
          提交询价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useInquiryListStore } from '@/stores/inquiryList'
import { createInquiry } from '@/api/inquiry'
import { getSessionId } from '@/utils/session'
import { Delete } from '@element-plus/icons-vue'

const router = useRouter()
const inquiryStore = useInquiryListStore()

function onImgError(e, name) {
  e.target.src = 'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="120" height="120"><rect fill="#f0ebe5" width="120" height="120"/><text x="60" y="65" text-anchor="middle" fill="#b8860b" font-size="12">' +
    (name || '商品') +
    '</text></svg>'
  )
}

const showInquiryDialog = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const inquiryForm = ref({
  contactName: '',
  contactPhone: '',
  companyName: '',
  remarks: ''
})

const inquiryRules = {
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ]
}

async function handleQuantityChange(item, newQty) {
  try {
    await inquiryStore.updateQty(item.id, newQty)
  } catch (e) {
    // Error handled by interceptor
  }
}

async function handleRemove(item) {
  try {
    await ElMessageBox.confirm('确定要移除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await inquiryStore.removeItem(item.id)
    ElMessage.success('已移除')
  } catch {
    // Cancelled
  }
}

async function handleClearList() {
  try {
    await ElMessageBox.confirm('确定要清空询价清单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await inquiryStore.clearList()
    ElMessage.success('询价清单已清空')
  } catch {
    // Cancelled
  }
}

async function handleSubmitInquiry() {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const items = inquiryStore.items.map(item => ({
      productId: item.productId || item.id,
      quantity: item.quantity
    }))

    await createInquiry({
      sessionId: getSessionId(),
      items,
      contactName: inquiryForm.value.contactName,
      contactPhone: inquiryForm.value.contactPhone,
      companyName: inquiryForm.value.companyName || undefined,
      remarks: inquiryForm.value.remarks || undefined
    })

    ElMessage.success('询价提交成功！我们会尽快联系您。')
    showInquiryDialog.value = false
    await inquiryStore.fetchInquiryList()
    router.push('/inquiries')
  } catch (e) {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  inquiryStore.fetchInquiryList()
})
</script>

<style scoped>
.inquiry-list-page {
  padding-top: 90px;
  min-height: 80vh;
}

.inquiry-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px 60px;
}

.page-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 20px 0;
}

.page-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.page-subtitle {
  font-size: 15px;
  color: var(--text-tertiary);
}

.inquiry-content {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 32px;
  align-items: start;
}

/* Inquiry Items */
.inquiry-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.inquiry-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  transition: all 0.3s ease;
}

.inquiry-item:hover {
  box-shadow: 0 4px 12px var(--shadow-color);
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-secondary);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-category {
  font-size: 13px;
  color: var(--text-tertiary);
  background: var(--hover-bg);
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 6px;
}

.item-price-row {
  display: flex;
  gap: 16px;
  margin-bottom: 4px;
}

.item-wholesale-price {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-color);
}

.item-retail-price {
  font-size: 14px;
  color: var(--text-tertiary);
  text-decoration: line-through;
}

.item-unit {
  font-size: 12px;
  color: var(--text-tertiary);
}

.item-quantity :deep(.el-input-number) {
  width: 120px;
}

.item-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.item-remove:hover {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

/* Inquiry Summary */
.inquiry-summary {
  position: sticky;
  top: 90px;
}

.summary-inner {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
}

.summary-label {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px;
}

.summary-info {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
}

.summary-count {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.summary-hint {
  font-size: 13px;
  color: var(--text-tertiary);
  margin: 0;
  line-height: 1.5;
}

.submit-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  border-radius: 12px;
  margin-bottom: 12px;
}

.clear-btn {
  width: 100%;
  border-radius: 12px;
}

.empty-text {
  color: var(--text-tertiary);
  margin-bottom: 20px;
}

/* Inquiry Dialog */
.inquiry-dialog :deep(.el-dialog__body) {
  padding: 20px 30px;
}

@media (max-width: 768px) {
  .inquiry-inner {
    padding: 0 16px;
  }
  .inquiry-content {
    grid-template-columns: 1fr;
  }
  .inquiry-item {
    flex-wrap: wrap;
    gap: 12px;
  }
  .item-image {
    width: 80px;
    height: 80px;
  }
  .item-price-row {
    flex-direction: column;
    gap: 4px;
  }
  .item-quantity :deep(.el-input-number) {
    width: 100px;
  }
}
</style>
