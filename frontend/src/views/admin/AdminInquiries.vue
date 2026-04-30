<template>
  <div class="page">
    <div class="page-header">
      <h2>📋 询价管理</h2>
      <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="fetchInquiries" style="width:140px">
        <el-option label="全部" value="" />
        <el-option label="待报价" value="PENDING" />
        <el-option label="已报价" value="QUOTED" />
        <el-option label="已确认" value="CONFIRMED" />
        <el-option label="已关闭" value="CLOSED" />
      </el-select>
    </div>

    <el-table :data="filteredInquiries" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="询价单号" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系人" width="120">
        <template #default="{ row }">{{ row.contactName }}</template>
      </el-table-column>
      <el-table-column label="电话" width="130">
        <template #default="{ row }">{{ row.contactPhone }}</template>
      </el-table-column>
      <el-table-column label="公司" min-width="120">
        <template #default="{ row }">{{ row.companyName || '无' }}</template>
      </el-table-column>
      <el-table-column label="报价金额" width="120">
        <template #default="{ row }">
          <span v-if="row.quotationAmount" style="color:var(--el-color-danger)">¥{{ row.quotationAmount }}</span>
          <span v-else style="color:#999">未报价</span>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提交时间" min-width="170" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
          <el-button
            size="small"
            type="primary"
            v-if="row.status === 'PENDING'"
            @click="showQuotationDialog(row)"
          >
            报价
          </el-button>
          <el-button
            size="small"
            type="danger"
            plain
            v-if="row.status === 'PENDING' || row.status === 'QUOTED'"
            @click="handleClose(row)"
          >
            关闭
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Inquiry Detail Dialog -->
    <el-dialog v-model="detailVisible" title="询价详情" width="700px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="询价单号">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detail.status)" size="small">{{ statusText(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.contactName }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ detail.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="公司名称">{{ detail.companyName || '无' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remarks || '无' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ detail.createdAt || '' }}</el-descriptions-item>
          <el-descriptions-item label="报价金额" v-if="detail.quotationAmount">
            <span style="color:var(--el-color-danger);font-weight:700">¥{{ detail.quotationAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="报价有效期" v-if="detail.quotationValidUntil">
            {{ detail.quotationValidUntil }}
          </el-descriptions-item>
          <el-descriptions-item label="管理员备注" :span="2" v-if="detail.adminRemarks">
            {{ detail.adminRemarks }}
          </el-descriptions-item>
        </el-descriptions>

        <h4 style="margin:20px 0 12px">商品明细</h4>
        <el-table :data="detail.items || []" border size="small">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column label="图片" width="60">
            <template #default="{ row }">
              <el-image :src="row.productImage" style="width:40px;height:40px" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column prop="price" label="参考单价" width="80">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="60" />
        </el-table>
      </template>
    </el-dialog>

    <!-- Quotation Dialog -->
    <el-dialog v-model="quotationVisible" title="回复报价" width="480px">
      <el-form
        ref="quotationFormRef"
        :model="quotationForm"
        :rules="quotationRules"
        label-width="100px"
        size="large"
      >
        <el-form-item label="报价金额" prop="amount">
          <el-input-number
            v-model="quotationForm.amount"
            :min="0"
            :precision="2"
            style="width:100%"
            placeholder="请输入报价金额"
          />
        </el-form-item>
        <el-form-item label="有效期至" prop="validUntil">
          <el-date-picker
            v-model="quotationForm.validUntil"
            type="date"
            placeholder="选择有效期"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="管理员备注" prop="remarks">
          <el-input
            v-model="quotationForm.remarks"
            type="textarea"
            :rows="3"
            placeholder="请填写备注信息（选填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="quotationVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitQuotation" :loading="submittingQuotation">
          提交报价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminInquiries, getAdminInquiryDetail, setInquiryQuotation, closeInquiry } from '@/api/admin'

const loading = ref(false)
const inquiries = ref([])
const statusFilter = ref('')
const detailVisible = ref(false)
const detail = ref(null)
const quotationVisible = ref(false)
const submittingQuotation = ref(false)
const quotationFormRef = ref(null)
const currentInquiryId = ref(null)

const quotationForm = ref({
  amount: 0,
  validUntil: null,
  remarks: ''
})

const quotationRules = {
  amount: [
    { required: true, message: '请输入报价金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '金额必须大于0', trigger: 'blur' }
  ]
}

const filteredInquiries = computed(() => {
  if (!statusFilter.value) return inquiries.value
  return inquiries.value.filter(o => o.status === statusFilter.value)
})

function statusText(status) {
  const map = { 'PENDING': '待报价', 'QUOTED': '已报价', 'CONFIRMED': '已确认', 'CLOSED': '已关闭' }
  return map[status] || status
}

function statusType(status) {
  const map = { 'PENDING': 'warning', 'QUOTED': 'primary', 'CONFIRMED': 'success', 'CLOSED': 'info' }
  return map[status] || 'info'
}

async function fetchInquiries() {
  loading.value = true
  try {
    inquiries.value = await getAdminInquiries()
  } catch {
    inquiries.value = []
  } finally {
    loading.value = false
  }
}

async function viewDetail(row) {
  try {
    detail.value = await getAdminInquiryDetail(row.id)
    detailVisible.value = true
  } catch {
    ElMessage.error('获取询价详情失败')
  }
}

function showQuotationDialog(row) {
  currentInquiryId.value = row.id
  quotationForm.value = { amount: 0, validUntil: null, remarks: '' }
  quotationVisible.value = true
}

async function handleSubmitQuotation() {
  if (!quotationFormRef.value) return
  const valid = await quotationFormRef.value.validate().catch(() => false)
  if (!valid) return

  submittingQuotation.value = true
  try {
    const params = {
      amount: quotationForm.value.amount,
      validUntil: quotationForm.value.validUntil ? quotationForm.value.validUntil.toISOString() : undefined,
      remarks: quotationForm.value.remarks || undefined
    }
    await setInquiryQuotation(currentInquiryId.value, params)
    ElMessage.success('报价已提交')
    quotationVisible.value = false
    await fetchInquiries()
  } catch {
    ElMessage.error('提交报价失败')
  } finally {
    submittingQuotation.value = false
  }
}

async function handleClose(row) {
  try {
    await closeInquiry(row.id)
    ElMessage.success('询价单已关闭')
    await fetchInquiries()
  } catch {
    ElMessage.error('关闭失败')
  }
}

onMounted(fetchInquiries)
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
