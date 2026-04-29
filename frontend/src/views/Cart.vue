<template>
  <div class="cart-page">
    <div class="cart-inner">
      <div class="page-header">
        <h1 class="page-title">购物车</h1>
        <span class="page-subtitle" v-if="cartStore.items.length">{{ cartStore.count }} 件商品</span>
      </div>

      <div v-if="cartStore.items.length > 0" class="cart-content">
        <!-- Cart Items -->
        <div class="cart-items">
          <div class="cart-item" v-for="item in cartStore.items" :key="item.id">
            <div class="item-image">
              <img :src="item.productImage || item.image || ''" :alt="item.productName || item.name" @error="onCartImgError($event, item.productName || item.name)" />
            </div>
            <div class="item-info">
              <h3 class="item-name">{{ item.productName || item.name }}</h3>
              <span class="item-category" v-if="item.category">{{ item.category }}</span>
            </div>
            <div class="item-price">¥{{ (item.price || item.productPrice)?.toFixed(2) }}</div>
            <div class="item-quantity">
              <el-input-number
                :model-value="item.quantity"
                :min="1"
                :max="item.stock || 99"
                size="small"
                controls-position="right"
                @change="(val) => handleQuantityChange(item, val)"
              />
            </div>
            <div class="item-subtotal">
              ¥{{ ((item.price || item.productPrice) * item.quantity)?.toFixed(2) }}
            </div>
            <button class="item-remove" @click="handleRemove(item)">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
        </div>

        <!-- Cart Summary -->
        <div class="cart-summary">
          <div class="summary-inner">
            <!-- Coupon -->
            <div class="coupon-section">
              <h4 class="summary-label">优惠券</h4>
              <div class="coupon-input">
                <el-input v-model="couponCode" placeholder="输入优惠券代码" size="large" class="coupon-field" />
                <el-button type="primary" @click="applyCoupon" size="large" :disabled="!couponCode">
                  使用
                </el-button>
              </div>
              <div class="coupon-info" v-if="discount > 0">
                <el-tag type="success" effect="plain">
                  优惠券已应用，优惠 ¥{{ discount.toFixed(2) }}
                </el-tag>
              </div>
            </div>

            <!-- Total -->
            <div class="total-section">
              <div class="total-row">
                <span class="total-label">商品数量</span>
                <span class="total-value">{{ cartStore.count }} 件</span>
              </div>
              <div class="total-row">
                <span class="total-label">商品小计</span>
                <span class="total-value">¥{{ cartStore.total.toFixed(2) }}</span>
              </div>
              <div class="total-row discount-row" v-if="discount > 0">
                <span class="total-label">优惠</span>
                <span class="total-value discount-value">-¥{{ discount.toFixed(2) }}</span>
              </div>
              <div class="total-row grand-total">
                <span class="total-label">应付总额</span>
                <span class="total-value final-price">¥{{ finalTotal.toFixed(2) }}</span>
              </div>
            </div>

            <el-button
              type="primary"
              size="large"
              class="checkout-btn"
              @click="showCheckoutDialog = true"
            >
              去结算
            </el-button>

            <el-button class="clear-btn" @click="handleClearCart">
              清空购物车
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="购物车是空的" :image-size="200">
        <template #description>
          <p class="empty-text">购物车还是空的，快去逛逛吧！</p>
        </template>
        <router-link to="/">
          <el-button type="primary" size="large">去逛逛</el-button>
        </router-link>
      </el-empty>
    </div>

    <!-- Checkout Dialog -->
    <el-dialog
      v-model="showCheckoutDialog"
      title="填写收货信息"
      width="520px"
      class="checkout-dialog"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="checkoutForm"
        :rules="checkoutRules"
        label-width="100px"
        size="large"
      >
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="checkoutForm.contactName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="checkoutForm.contactPhone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="收货地址" prop="contactAddress">
          <el-input
            v-model="checkoutForm.contactAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细收货地址"
          />
        </el-form-item>
        <el-form-item label="优惠券码">
          <el-input v-model="checkoutForm.couponCode" placeholder="如有优惠券请填写" />
        </el-form-item>
      </el-form>

      <div class="checkout-total">
        <span>合计：</span>
        <span class="checkout-price">¥{{ finalTotal.toFixed(2) }}</span>
      </div>

      <template #footer>
        <el-button @click="showCheckoutDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCheckout" :loading="submittingOrder">
          提交订单
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { createOrder } from '@/api/order'
import { getSessionId } from '@/utils/session'
import { Delete } from '@element-plus/icons-vue'

const router = useRouter()
const cartStore = useCartStore()

function onCartImgError(e, name) {
  e.target.src = 'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="120" height="120"><rect fill="#f0ebe5" width="120" height="120"/><text x="60" y="65" text-anchor="middle" fill="#b8860b" font-size="12">' +
    (name || '商品') +
    '</text></svg>'
  )
}

const couponCode = ref('')
const discount = ref(0)
const showCheckoutDialog = ref(false)
const submittingOrder = ref(false)
const formRef = ref(null)

const checkoutForm = ref({
  contactName: '',
  contactPhone: '',
  contactAddress: '',
  couponCode: ''
})

const checkoutRules = {
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  contactAddress: [
    { required: true, message: '请输入收货地址', trigger: 'blur' },
    { min: 5, max: 200, message: '地址长度在 5 到 200 个字符', trigger: 'blur' }
  ]
}

const finalTotal = computed(() => {
  return Math.max(0, cartStore.total - discount.value)
})

function applyCoupon() {
  if (couponCode.value === 'VIP50') {
    discount.value = 50
    ElMessage.success('优惠券应用成功，立减 ¥50！')
  } else if (couponCode.value === 'NEW10') {
    discount.value = cartStore.total * 0.1
    ElMessage.success('优惠券应用成功，享9折优惠！')
  } else {
    ElMessage.warning('无效的优惠券代码')
    discount.value = 0
  }
}

async function handleQuantityChange(item, newQty) {
  try {
    await cartStore.updateQty(item.id, newQty)
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
    await cartStore.removeItem(item.id)
    ElMessage.success('已移除')
  } catch {
    // Cancelled
  }
}

async function handleClearCart() {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartStore.clearCart()
    ElMessage.success('购物车已清空')
  } catch {
    // Cancelled
  }
}

async function handleCheckout() {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submittingOrder.value = true
  try {
    const items = cartStore.items.map(item => ({
      productId: item.productId || item.id,
      quantity: item.quantity
    }))

    await createOrder({
      sessionId: getSessionId(),
      items,
      contactName: checkoutForm.value.contactName,
      contactPhone: checkoutForm.value.contactPhone,
      contactAddress: checkoutForm.value.contactAddress,
      couponCode: checkoutForm.value.couponCode || undefined
    })

    ElMessage.success('订单提交成功！')
    showCheckoutDialog.value = false
    await cartStore.fetchCart()
    router.push('/order')
  } catch (e) {
    // Error handled by interceptor
  } finally {
    submittingOrder.value = false
  }
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>

<style scoped>
.cart-page {
  padding-top: 90px;
  min-height: 80vh;
}

.cart-inner {
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

.cart-content {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 32px;
  align-items: start;
}

/* Cart Items */
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  transition: all 0.3s ease;
}

.cart-item:hover {
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
}

.item-price,
.item-subtotal {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
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

/* Cart Summary */
.cart-summary {
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
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px;
}

.coupon-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
}

.coupon-input {
  display: flex;
  gap: 8px;
}

.coupon-field :deep(.el-input__wrapper) {
  border-radius: 10px;
  background: var(--input-bg);
  box-shadow: none;
  border: 1px solid var(--border-color);
}

.coupon-info {
  margin-top: 8px;
}

.total-section {
  margin-bottom: 24px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.total-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.total-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.discount-value {
  color: #e74c3c;
}

.grand-total {
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
  margin-top: 8px;
}

.grand-total .total-label {
  font-size: 16px;
  font-weight: 600;
}

.final-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
}

.checkout-btn {
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

/* Checkout Dialog */
.checkout-dialog :deep(.el-dialog__body) {
  padding: 20px 30px;
}

.checkout-total {
  text-align: right;
  font-size: 16px;
  color: var(--text-secondary);
  padding: 0 30px 20px;
}

.checkout-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
}

@media (max-width: 768px) {
  .cart-inner {
    padding: 0 16px;
  }
  .cart-content {
    grid-template-columns: 1fr;
  }
  .cart-item {
    flex-wrap: wrap;
    gap: 12px;
  }
  .item-image {
    width: 80px;
    height: 80px;
  }
  .item-price,
  .item-subtotal {
    font-size: 14px;
  }
  .item-quantity :deep(.el-input-number) {
    width: 100px;
  }
}
</style>
