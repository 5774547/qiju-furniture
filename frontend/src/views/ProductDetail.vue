<template>
  <div class="product-detail" v-loading="loading">
    <div class="detail-inner" v-if="product">
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span class="separator">/</span>
        <span v-if="product.category">{{ product.category }}</span>
        <span class="separator">/</span>
        <span class="current">{{ product.name }}</span>
      </div>

      <div class="detail-main">
        <!-- Image Gallery -->
        <div class="gallery-section">
          <div class="main-image">
            <img
              :src="mainImage"
              :alt="product.name"
              @click="showImagePreview"
            />
            <div class="image-tags" v-if="product.tags">
              <el-tag
                v-for="tag in product.tags"
                :key="tag"
                :type="tag === '新品' ? 'danger' : tag === '热卖' ? 'warning' : 'info'"
                effect="dark"
                class="tag"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
          <div class="thumbnails" v-if="product.images && product.images.length > 1">
            <div
              v-for="(img, idx) in product.images"
              :key="idx"
              class="thumb"
              :class="{ active: selectedImage === idx }"
              @click="selectImage(idx)"
            >
              <img :src="img" :alt="`${product.name} ${idx + 1}`" />
            </div>
          </div>
        </div>

        <!-- Product Info -->
        <div class="info-section">
          <h1 class="product-name">{{ product.name }}</h1>

          <div class="rating-row">
            <el-rate :model-value="product.rating || 0" disabled show-score score-template="{value}" />
            <span class="rating-count" v-if="product.reviewCount">({{ product.reviewCount }} 条评价)</span>
            <span class="sales-count" v-if="product.sales !== undefined">已售 {{ product.sales }} 件</span>
          </div>

          <div class="price-box">
            <div class="current-price">
              <span class="currency">¥</span>
              <span class="price-value">{{ (product.wholesalePrice || product.price)?.toFixed(2) }}</span>
              <span class="price-label" v-if="product.wholesalePrice">批发价</span>
            </div>
            <div class="original-price" v-if="product.wholesalePrice">
              零售价 ¥{{ product.price?.toFixed(2) }}
            </div>
            <div class="unit-info" v-if="product.unit">
              单位: {{ product.unit }}
            </div>
          </div>

          <!-- Specs -->
          <div class="specs-section" v-if="product.specs">
            <h3 class="specs-title">商品规格</h3>
            <div class="specs-grid">
              <div class="spec-item" v-for="(value, key) in product.specs" :key="key">
                <span class="spec-label">{{ key }}</span>
                <span class="spec-value">{{ value }}</span>
              </div>
            </div>
          </div>

          <!-- Stock Status -->
          <div class="stock-status">
            <span class="stock-label">库存状态：</span>
            <el-tag :type="stockTagType" effect="plain" size="large">
              {{ stockText }}
            </el-tag>
          </div>

          <!-- Quantity -->
          <div class="quantity-row">
            <span class="quantity-label">数量：</span>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock || 99"
              size="large"
              controls-position="right"
            />
          </div>

          <!-- Actions -->
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              class="add-cart-btn"
              @click="handleAddToInquiry"
              :loading="addingToInquiry"
              :disabled="!product.stock || product.stock <= 0"
            >
              <el-icon><ChatDotSquare /></el-icon>
              加入询价清单
            </el-button>
            <el-button
              size="large"
              class="like-btn"
              :type="isFavorited ? 'danger' : 'default'"
              @click="toggleFavorite"
              plain
            >
              <el-icon><StarFilled v-if="isFavorited" /><Star v-else /></el-icon>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>

          <!-- Service Assurance -->
          <div class="service-assurance">
            <div class="service-item">
              <el-icon><Factory /></el-icon>
              <span>工厂直供</span>
            </div>
            <div class="service-item">
              <el-icon><ChatDotSquare /></el-icon>
              <span>在线询价</span>
            </div>
            <div class="service-item">
              <el-icon><Headset /></el-icon>
              <span>专属客服</span>
            </div>
            <div class="service-item">
              <el-icon><Checked /></el-icon>
              <span>品质保证</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Product Description -->
      <div class="detail-extras">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="商品详情" name="detail">
            <div class="description-content" v-html="product.description || '暂无详细描述'"></div>
          </el-tab-pane>
          <el-tab-pane :label="`用户评价 (${reviews.length})`" name="reviews">
            <div class="reviews-section">
              <!-- Review Form -->
              <div class="review-form">
                <h3 class="review-form-title">发表评价</h3>
                <div class="form-row">
                  <span class="form-label">评分：</span>
                  <el-rate v-model="newReview.stars" :colors="['#f56c6c', '#e6a23c', '#f7c948']" />
                </div>
                <div class="form-row">
                  <span class="form-label">昵称：</span>
                  <el-input v-model="newReview.author" placeholder="您的昵称" size="large" class="review-input" />
                </div>
                <div class="form-row">
                  <span class="form-label">评价：</span>
                  <el-input
                    v-model="newReview.content"
                    type="textarea"
                    :rows="4"
                    placeholder="分享您的使用体验..."
                    maxlength="500"
                    show-word-limit
                    size="large"
                  />
                </div>
                <el-button type="primary" @click="submitReview" :loading="submittingReview">
                  提交评价
                </el-button>
              </div>

              <!-- Reviews List -->
              <div class="reviews-list" v-if="reviews.length > 0">
                <div class="review-card" v-for="review in reviews" :key="review.id">
                  <div class="review-header">
                    <div class="reviewer-avatar">{{ review.author?.charAt(0) || '匿' }}</div>
                    <div class="reviewer-info">
                      <span class="reviewer-name">{{ review.author || '匿名用户' }}</span>
                      <el-rate :model-value="review.stars" disabled size="small" />
                    </div>
                    <span class="review-date">{{ formatDate(review.createdAt) }}</span>
                  </div>
                  <p class="review-content">{{ review.content }}</p>
                </div>
              </div>
              <el-empty v-else description="暂无评价，快来发表第一条评价吧！" :image-size="120" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- Related Products -->
      <div class="related-section" v-if="relatedProducts.length > 0">
        <h3 class="related-title">相关推荐</h3>
        <div class="related-grid">
          <ProductCard v-for="item in relatedProducts" :key="item.id" :product="item" />
        </div>
      </div>
    </div>

    <el-empty v-else-if="!loading" description="商品未找到" :image-size="200" />

    <!-- Image Preview Dialog -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="imageList"
      :initial-index="selectedImage"
      @close="previewVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useProductStore } from '@/stores/product'
import { useInquiryListStore } from '@/stores/inquiryList'
import { getReviews, submitReview as apiSubmitReview } from '@/api/review'
import { ChatDotSquare, Star, StarFilled, Headset, Checked } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()
const productStore = useProductStore()
const inquiryStore = useInquiryListStore()

const product = ref(null)
const loading = ref(true)
const selectedImage = ref(0)
const mainImage = computed(() => {
  if (product.value?.images && product.value.images.length > 0) {
    return product.value.images[selectedImage.value]
  }
  return product.value?.image || 'https://via.placeholder.com/600x600/f5f0eb/8B7355?text=家具'
})

const imageList = computed(() => {
  return product.value?.images?.length ? product.value.images : [product.value?.image]
})

const previewVisible = ref(false)
const quantity = ref(1)
const addingToInquiry = ref(false)
const activeTab = ref('detail')
const reviews = ref([])
const submittingReview = ref(false)

const isFavorited = ref(false)

const newReview = ref({
  stars: 5,
  author: '',
  content: ''
})

const relatedProducts = ref([])

// Stock
const stockTagType = computed(() => {
  if (!product.value?.stock) return 'danger'
  if (product.value.stock > 20) return 'success'
  if (product.value.stock > 5) return 'warning'
  return 'danger'
})

const stockText = computed(() => {
  if (!product.value?.stock || product.value.stock <= 0) return '暂时缺货'
  if (product.value.stock > 20) return '库存充足'
  if (product.value.stock > 5) return `仅剩 ${product.value.stock} 件`
  return '即将售罄'
})

function selectImage(idx) {
  selectedImage.value = idx
}

function showImagePreview() {
  previewVisible.value = true
}

function formatDate(date) {
  return date ? dayjs(date).format('YYYY-MM-DD') : ''
}

async function handleAddToInquiry() {
  if (!product.value?.stock || product.value.stock <= 0) {
    ElMessage.warning('该商品暂时缺货')
    return
  }
  addingToInquiry.value = true
  try {
    await inquiryStore.addItem(product.value.id, quantity.value)
    ElMessage.success('已加入询价清单')
  } catch (e) {
    // Error handled by interceptor
  } finally {
    addingToInquiry.value = false
  }
}

function toggleFavorite() {
  isFavorited.value = !isFavorited.value
  if (isFavorited.value) {
    ElMessage.success('已收藏')
  }
}

async function submitReview() {
  if (!newReview.value.content) {
    ElMessage.warning('请输入评价内容')
    return
  }
  submittingReview.value = true
  try {
    await apiSubmitReview({
      productId: route.params.id,
      author: newReview.value.author || '匿名用户',
      stars: newReview.value.stars,
      content: newReview.value.content
    })
    ElMessage.success('评价提交成功！')
    newReview.value = { stars: 5, author: '', content: '' }
    await loadReviews()
  } catch (e) {
    // Error handled by interceptor
  } finally {
    submittingReview.value = false
  }
}

async function loadReviews() {
  try {
    const res = await getReviews(route.params.id)
    reviews.value = res || []
  } catch {
    reviews.value = []
  }
}

async function loadProduct() {
  loading.value = true
  const id = route.params.id
  const res = await productStore.fetchProduct(id)
  product.value = res
  // Add to recently viewed
  if (res) {
    try {
      const viewed = JSON.parse(localStorage.getItem('qiju_recently_viewed') || '[]')
      const filtered = viewed.filter(item => item.id !== res.id)
      filtered.unshift({ id: res.id, name: res.name, price: res.price, image: res.image || res.images?.[0] })
      localStorage.setItem('qiju_recently_viewed', JSON.stringify(filtered.slice(0, 20)))
    } catch {}
  }
  loading.value = false
}

onMounted(async () => {
  await loadProduct()
  await loadReviews()

  // Get related products (same category)
  if (product.value?.category) {
    try {
      const prods = await productStore.fetchProducts({ category: product.value.category })
      relatedProducts.value = (prods || []).filter(p => p.id !== product.value.id).slice(0, 4)
    } catch {
      relatedProducts.value = []
    }
  }
})

watch(() => route.params.id, () => {
  loadProduct()
  loadReviews()
})
</script>

<style scoped>
.product-detail {
  padding-top: 90px;
  min-height: 80vh;
}

.detail-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px 60px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 0;
  font-size: 14px;
  color: var(--text-tertiary);
}

.breadcrumb a {
  color: var(--text-tertiary);
  text-decoration: none;
  transition: color 0.3s;
}

.breadcrumb a:hover {
  color: var(--primary-color);
}

.separator {
  color: var(--border-color);
}

.current {
  color: var(--text-primary);
  font-weight: 500;
}

.detail-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  margin-bottom: 48px;
}

/* Gallery */
.gallery-section {
  position: sticky;
  top: 90px;
  align-self: start;
}

.main-image {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  cursor: zoom-in;
}

.main-image img {
  width: 100%;
  height: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  transition: transform 0.3s;
}

.main-image:hover img {
  transform: scale(1.02);
}

.image-tags {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
}

.tag {
  border: none;
}

.thumbnails {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.thumb {
  width: 70px;
  height: 70px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  background: var(--bg-secondary);
}

.thumb.active {
  border-color: var(--primary-color);
}

.thumb:hover {
  border-color: var(--primary-color);
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Info */
.info-section {
  padding: 20px 0;
}

.product-name {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 16px;
  line-height: 1.4;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.rating-count,
.sales-count {
  font-size: 14px;
  color: var(--text-tertiary);
}

.price-box {
  display: flex;
  align-items: baseline;
  gap: 16px;
  padding: 20px;
  background: var(--bg-secondary);
  border-radius: 12px;
  margin-bottom: 24px;
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.currency {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}

.price-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--primary-color);
}

.original-price {
  font-size: 16px;
  color: var(--text-tertiary);
  text-decoration: line-through;
}

/* Specs */
.specs-section {
  margin-bottom: 24px;
}

.specs-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px;
}

.specs-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.spec-item {
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  background: var(--hover-bg);
  border-radius: 8px;
  font-size: 14px;
}

.spec-label {
  color: var(--text-tertiary);
  flex-shrink: 0;
}

.spec-value {
  color: var(--text-primary);
  font-weight: 500;
}

/* Stock */
.stock-status {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.stock-label {
  font-size: 14px;
  color: var(--text-secondary);
}

/* Quantity */
.quantity-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.quantity-label {
  font-size: 14px;
  color: var(--text-secondary);
}

/* Actions */
.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.add-cart-btn {
  flex: 1;
  height: 52px;
  font-size: 16px;
  border-radius: 12px;
}

.like-btn {
  height: 52px;
  width: 120px;
  border-radius: 12px;
}

/* Service */
.service-assurance {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: 12px;
}

.service-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

/* Detail Tabs */
.detail-tabs {
  margin-bottom: 40px;
}

.detail-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 24px;
}

.detail-tabs :deep(.el-tabs__active-bar) {
  background: var(--primary-color);
}

.description-content {
  padding: 24px;
  line-height: 2;
  font-size: 15px;
  color: var(--text-secondary);
}

/* Reviews */
.reviews-section {
  max-width: 800px;
}

.review-form {
  background: var(--bg-secondary);
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 32px;
}

.review-form-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px;
}

.form-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.form-label {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 40px;
  flex-shrink: 0;
  width: 50px;
}

.review-input {
  flex: 1;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 20px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--primary-light);
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
}

.reviewer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.reviewer-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.review-date {
  font-size: 13px;
  color: var(--text-tertiary);
}

.review-content {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.8;
  margin: 0;
}

/* Related Products */
.related-section {
  margin-top: 48px;
}

.related-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 24px;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

@media (max-width: 768px) {
  .detail-inner {
    padding: 0 16px;
  }
  .detail-main {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  .gallery-section {
    position: static;
  }
  .related-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .service-assurance {
    grid-template-columns: repeat(2, 1fr);
  }
  .specs-grid {
    grid-template-columns: 1fr;
  }
}
</style>
