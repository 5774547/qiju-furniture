<template>
  <div class="product-card" @click="goToDetail">
    <div class="card-image-wrapper">
      <img
        :src="product.image || placeholderImg"
        :alt="product.name"
        class="card-image"
        loading="lazy"
        crossorigin="anonymous"
        @error="onImgError"
      />
      <div class="card-overlay">
        <el-button type="primary" size="small" plain round class="quick-view" @click.stop="goToDetail">
          <el-icon><View /></el-icon>
          查看详情
        </el-button>
      </div>
      <div class="card-tags" v-if="product.tags && product.tags.length">
        <el-tag
          v-for="tag in product.tags.slice(0, 2)"
          :key="tag"
          size="small"
          :type="tag === '新品' ? 'danger' : tag === '热卖' ? 'warning' : 'info'"
          effect="dark"
          class="product-tag"
        >
          {{ tag }}
        </el-tag>
      </div>
      <button class="like-btn" @click.stop="toggleLike" :class="{ liked: isLiked }">
        <el-icon><StarFilled v-if="isLiked" /><Star v-else /></el-icon>
      </button>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ product.name }}</h3>
      <div class="card-meta">
        <span class="card-category" v-if="product.category">{{ product.category }}</span>
        <span class="card-sales" v-if="product.sales !== undefined">已售 {{ product.sales }}</span>
      </div>
      <el-rate
        v-if="product.rating"
        :model-value="product.rating"
        disabled
        show-score
        score-template="{value}"
        size="small"
        class="card-rating"
      />
      <div class="card-price-row">
        <span class="card-price">¥{{ product.price?.toFixed(2) }}</span>
        <span class="card-original-price" v-if="product.originalPrice">
          ¥{{ product.originalPrice?.toFixed(2) }}
        </span>
      </div>
      <div class="card-actions">
        <el-checkbox
          v-model="isCompareSelected"
          @change.stop="toggleCompare"
          size="small"
          @click.stop
        >
          对比
        </el-checkbox>
        <el-button
          type="primary"
          size="small"
          :icon="ShoppingCart"
          circle
          @click.stop="addToCartHandler"
          :loading="addingToCart"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, View, Star, StarFilled } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()

// Image fallback
const placeholderImg = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect fill="#f0ebe5" width="400" height="400"/><text x="200" y="200" text-anchor="middle" fill="#b8860b" font-size="20" font-family="serif">' +
  (props.product?.name || '家具') +
  '</text></svg>'
)
function onImgError(e) {
  e.target.src = placeholderImg
}

// For compare, we'll use a simple reactive approach
const compareItems = ref(JSON.parse(localStorage.getItem('qiju_compare') || '[]'))
const isCompareSelected = computed(() => compareItems.value.some(item => item.id === props.product.id))
const addingToCart = ref(false)

const isLiked = ref(localStorage.getItem(`qiju_like_${props.product.id}`) === 'true')

function goToDetail() {
  router.push(`/product/${props.product.id}`)
}

function toggleLike() {
  isLiked.value = !isLiked.value
  localStorage.setItem(`qiju_like_${props.product.id}`, isLiked.value ? 'true' : 'false')
}

function toggleCompare() {
  const idx = compareItems.value.findIndex(item => item.id === props.product.id)
  if (idx > -1) {
    compareItems.value.splice(idx, 1)
  } else {
    if (compareItems.value.length >= 4) {
      ElMessage.warning('最多对比4件商品')
      return
    }
    compareItems.value.push({
      id: props.product.id,
      name: props.product.name,
      image: props.product.image
    })
  }
  localStorage.setItem('qiju_compare', JSON.stringify(compareItems.value))
  // Dispatch event for compare bar
  window.dispatchEvent(new CustomEvent('compare-updated', { detail: [...compareItems.value] }))
}

async function addToCartHandler() {
  addingToCart.value = true
  try {
    await cartStore.addItem(props.product.id, 1)
    ElMessage.success('已加入购物车')
  } catch (e) {
    // Error handled by interceptor
  } finally {
    addingToCart.value = false
  }
}

// Listen for compare updates from outside
if (typeof window !== 'undefined') {
  window.addEventListener('compare-updated', (e) => {
    compareItems.value = e.detail
  })
}
</script>

<style scoped>
.product-card {
  background: var(--card-bg);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 40px var(--shadow-color);
  border-color: var(--primary-color);
}

.card-image-wrapper {
  position: relative;
  overflow: hidden;
  aspect-ratio: 1;
  background: var(--bg-secondary);
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.product-card:hover .card-image {
  transform: scale(1.08);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .card-overlay {
  opacity: 1;
}

.quick-view {
  backdrop-filter: blur(4px);
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: #333;
}

.card-tags {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 6px;
}

.product-tag {
  border: none;
  font-weight: 500;
}

.like-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  transition: all 0.3s ease;
}

.like-btn:hover {
  transform: scale(1.1);
}

.like-btn.liked {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 12px;
  color: var(--text-tertiary);
}

.card-category {
  background: var(--hover-bg);
  padding: 2px 8px;
  border-radius: 4px;
}

.card-rating {
  margin-bottom: 8px;
}

.card-rating :deep(.el-rate__icon) {
  font-size: 14px;
}

.card-price-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.card-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
}

.card-original-price {
  font-size: 13px;
  color: var(--text-tertiary);
  text-decoration: line-through;
}

.card-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
