<template>
  <div class="home-page">
    <!-- Hero Carousel -->
    <HeroCarousel />

    <!-- Category Tabs Section -->
    <section class="category-section" ref="categorySection">
      <div class="section-inner">
        <ScrollReveal>
          <div class="section-header">
            <h2 class="section-title">产品分类</h2>
            <p class="section-desc">工厂直供·源头价格·定制批发</p>
          </div>
        </ScrollReveal>

        <ScrollReveal :delay="100">
          <div class="category-tabs">
            <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
              <el-tab-pane label="全部" name="all"></el-tab-pane>
              <el-tab-pane
                v-for="cat in displayCategories"
                :key="cat.name"
                :label="cat.icon + ' ' + cat.name + '(' + (categories.find(c => (c.name||c) === cat.name)?.count || 0) + ')'"
                :name="cat.name"
              ></el-tab-pane>
            </el-tabs>
          </div>
        </ScrollReveal>

        <ScrollReveal :delay="150">
          <div class="category-grid">
            <div
              v-for="cat in displayCategories"
              :key="cat.name"
              class="category-card"
              :class="{ active: activeCategory === cat.name }"
              @click="handleCategoryClick(cat.name)"
            >
              <div class="cat-icon-wrapper">
                <span class="cat-icon">{{ cat.icon }}</span>
              </div>
              <span class="cat-name">{{ cat.name }}</span>
            </div>
          </div>
        </ScrollReveal>
      </div>
    </section>

    <!-- Promo Banner -->
    <section class="promo-section">
      <ScrollReveal>
        <div class="promo-banner">
          <div class="promo-content">
            <span class="promo-tag">工厂直供</span>
            <h3 class="promo-title">源头工厂·品质保证·批发价格</h3>
            <p class="promo-desc">多件组合优惠，详询在线客服</p>
            <el-button type="warning" size="large" round @click="scrollToProducts">
              查看产品
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="promo-image">
            <img src="https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=600&q=80" alt="促销" />
          </div>
        </div>
      </ScrollReveal>
    </section>

    <!-- Product Grid Section -->
    <section class="product-grid-section" ref="productSection">
      <div class="section-inner">
        <ScrollReveal>
          <div class="section-header">
            <h2 class="section-title">商品列表</h2>
            <p class="section-desc">工厂直供产品，欢迎询价</p>
          </div>
        </ScrollReveal>

        <!-- Search and Filter Bar -->
        <ScrollReveal :delay="100">
          <div class="filter-bar">
            <div class="search-box">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索家具、分类..."
                prefix-icon="Search"
                size="large"
                clearable
                @clear="handleSearch"
                @keyup.enter="handleSearch"
                class="search-input"
              >
                <template #append>
                  <el-button @click="handleSearch" class="search-btn">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>

            <div class="filter-controls">
              <div class="price-filter">
                <span class="filter-label">价格区间</span>
                <el-input
                  v-model="priceMin"
                  placeholder="最低价"
                  size="small"
                  class="price-input"
                  @change="handleFilter"
                />
                <span class="price-separator">—</span>
                <el-input
                  v-model="priceMax"
                  placeholder="最高价"
                  size="small"
                  class="price-input"
                  @change="handleFilter"
                />
              </div>

              <div class="sort-control">
                <el-select v-model="sortBy" placeholder="排序方式" size="large" @change="handleSort" class="sort-select">
                  <el-option label="默认排序" value="" />
                  <el-option label="价格从低到高" value="price_asc" />
                  <el-option label="价格从高到低" value="price_desc" />
                  <el-option label="销量优先" value="sales" />
                  <el-option label="最新上架" value="newest" />
                  <el-option label="评分最高" value="rating" />
                </el-select>
              </div>
            </div>
          </div>
        </ScrollReveal>

        <!-- Product Grid -->
        <div class="product-grid" v-loading="productStore.loading">
          <template v-if="productStore.loading && productStore.products.length === 0">
            <SkeletonCard v-for="i in 8" :key="'skel-' + i" />
          </template>
          <template v-else>
            <ScrollReveal
              v-for="(product, index) in productStore.products"
              :key="product.id"
              :delay="index * 50"
            >
              <ProductCard :product="product" />
            </ScrollReveal>
          </template>

          <el-empty
            v-if="!productStore.loading && productStore.products.length === 0"
            description="暂无商品"
            :image-size="200"
          />
        </div>

        <!-- View more -->
        <div class="view-more" v-if="productStore.products.length > 0">
          <el-button type="primary" plain round size="large" @click="loadMore" :loading="loadingMore">
            加载更多
          </el-button>
        </div>
      </div>
    </section>

    <!-- Recently Viewed -->
    <section class="recently-viewed" v-if="recentlyViewed.length > 0">
      <div class="section-inner">
        <ScrollReveal>
          <div class="section-header">
            <h2 class="section-title">最近浏览</h2>
            <p class="section-desc">您最近看过的商品</p>
          </div>
        </ScrollReveal>

        <div class="recent-grid">
          <ScrollReveal v-for="(item, index) in recentlyViewed" :key="item.id" :delay="index * 50">
            <div class="recent-card" @click="goToProduct(item.id)">
              <img :src="item.image || 'https://via.placeholder.com/200x200/f5f0eb/8B7355'" :alt="item.name" class="recent-img" />
              <div class="recent-info">
                <span class="recent-name">{{ item.name }}</span>
                <span class="recent-price">¥{{ item.price?.toFixed(2) }}</span>
              </div>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="section-inner">
        <ScrollReveal>
          <div class="features-grid">
            <div class="feature-card">
              <div class="feature-icon">
                <el-icon :size="36"><Shop /></el-icon>
              </div>
              <h4 class="feature-title">工厂直供</h4>
              <p class="feature-desc">源头工厂，无中间商差价</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <el-icon :size="36"><ChatDotSquare /></el-icon>
              </div>
              <h4 class="feature-title">在线询价</h4>
              <p class="feature-desc">一键询价，快速获取报价</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <el-icon :size="36"><Headset /></el-icon>
              </div>
              <h4 class="feature-title">专属客服</h4>
              <p class="feature-desc">一对一客服，专业选品建议</p>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <el-icon :size="36"><Checked /></el-icon>
              </div>
              <h4 class="feature-title">品质保证</h4>
              <p class="feature-desc">严选材料，匠心工艺品质保障</p>
            </div>
          </div>
        </ScrollReveal>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useProductStore } from '@/stores/product'
import { Search, ArrowRight, Shop, ChatDotSquare, Headset, Checked } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import HeroCarousel from '@/components/HeroCarousel.vue'
import ProductCard from '@/components/ProductCard.vue'
import SkeletonCard from '@/components/SkeletonCard.vue'
import ScrollReveal from '@/components/ScrollReveal.vue'

const router = useRouter()
const productStore = useProductStore()

const activeCategory = ref('all')
const searchKeyword = ref('')
const sortBy = ref('')
const priceMin = ref('')
const priceMax = ref('')
const loadingMore = ref(false)

const categories = ref([])

const categoryIcons = {
  '沙发': '🛋️',
  '桌子': '🪑',
  '椅子': '💺',
  '床': '🛏️',
  '柜子': '🗄️',
  '灯饰': '💡',
  '客厅': '🛋️',
  '卧室': '🛏️',
  '餐厅': '🍽️',
  '书房': '📚',
  '儿童': '🧸',
  '户外': '🌿',
  '灯具': '💡',
  '装饰': '🖼️',
  '办公': '💼',
  '储物': '📦'
}

const displayCategories = computed(() => {
  return categories.value.map(cat => ({
    name: cat.name || cat,
    icon: categoryIcons[cat.name || cat] || '🏠'
  }))
})

// Recently viewed
const recentlyViewed = ref([])

function loadRecentlyViewed() {
  try {
    const stored = JSON.parse(localStorage.getItem('qiju_recently_viewed') || '[]')
    recentlyViewed.value = stored.slice(0, 8)
  } catch {
    recentlyViewed.value = []
  }
}

function goToProduct(id) {
  router.push(`/product/${id}`)
}

async function fetchProducts() {
  const params = {}
  if (activeCategory.value !== 'all') params.category = activeCategory.value
  if (searchKeyword.value) params.keyword = searchKeyword.value
  if (priceMin.value) params.priceMin = priceMin.value
  if (priceMax.value) params.priceMax = priceMax.value
  if (sortBy.value) params.sortBy = sortBy.value
  await productStore.fetchProducts(params)
}

function handleCategoryChange() {
  fetchProducts()
}

function handleCategoryClick(catName) {
  activeCategory.value = catName === activeCategory.value ? 'all' : catName
  fetchProducts()
}

function handleSearch() {
  fetchProducts()
}

function handleFilter() {
  fetchProducts()
}

function handleSort() {
  fetchProducts()
}

async function loadMore() {
  loadingMore.value = true
  // Simulate loading more - in real app, paginate
  await new Promise(r => setTimeout(r, 500))
  ElMessage.info('已加载全部商品')
  loadingMore.value = false
}

function scrollToProducts() {
  productSection.value?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(async () => {
  await productStore.fetchCategories()
  categories.value = productStore.categories
  await fetchProducts()
  loadRecentlyViewed()
})
</script>

<style scoped>
.home-page {
  padding-top: 0;
}

.section-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
  margin-top: 60px;
}

.section-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.section-desc {
  font-size: 16px;
  color: var(--text-tertiary);
  margin: 0;
}

/* Category Section */
.category-section {
  padding-top: 40px;
}

.category-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.category-tabs :deep(.el-tabs__header) {
  margin: 0;
  border-bottom: none;
}

.category-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
  color: var(--text-secondary);
}

.category-tabs :deep(.el-tabs__item.is-active) {
  color: var(--primary-color);
  font-weight: 600;
}

.category-tabs :deep(.el-tabs__active-bar) {
  background: var(--primary-color);
  height: 3px;
  border-radius: 2px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
  margin-bottom: 40px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-card:hover {
  border-color: var(--primary-color);
  transform: translateY(-3px);
  box-shadow: 0 8px 24px var(--shadow-color);
}

.category-card.active {
  border-color: var(--primary-color);
  background: var(--primary-light);
}

.cat-icon-wrapper {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--hover-bg);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.category-card:hover .cat-icon-wrapper {
  background: var(--primary-light);
}

.cat-icon {
  font-size: 24px;
}

.cat-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

/* Promo Banner */
.promo-section {
  margin: 60px 0;
  padding: 0 40px;
}

.promo-banner {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 24px;
  overflow: hidden;
  min-height: 300px;
}

.promo-content {
  flex: 1;
  padding: 50px;
}

.promo-tag {
  display: inline-block;
  padding: 4px 16px;
  background: rgba(231, 76, 60, 0.2);
  color: #e74c3c;
  font-size: 13px;
  font-weight: 600;
  border-radius: 20px;
  margin-bottom: 16px;
}

.promo-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
}

.promo-desc {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 24px;
  line-height: 1.6;
}

.promo-image {
  width: 350px;
  height: 300px;
  flex-shrink: 0;
}

.promo-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Filter Bar */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  min-width: 280px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: none;
  border: 1px solid var(--border-color);
  background: var(--input-bg);
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px var(--primary-light);
}

.search-btn {
  border-radius: 0 12px 12px 0;
}

.filter-controls {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.price-filter {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.price-input {
  width: 100px;
}

.price-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  background: var(--input-bg);
  box-shadow: none;
  border: 1px solid var(--border-color);
}

.price-separator {
  color: var(--text-tertiary);
}

.sort-select {
  width: 160px;
}

/* Product Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
  min-height: 200px;
}

.view-more {
  text-align: center;
  margin: 40px 0;
}

/* Recently Viewed */
.recently-viewed {
  background: var(--bg-secondary);
  padding: 20px 0 40px;
}

.recent-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.recent-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--card-bg);
  border-radius: 12px;
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s ease;
}

.recent-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color);
}

.recent-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.recent-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.recent-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recent-price {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-color);
}

/* Features Section */
.features-section {
  padding: 60px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  text-align: center;
  padding: 36px 24px;
  background: var(--card-bg);
  border-radius: 16px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px var(--shadow-color);
  border-color: var(--primary-color);
}

.feature-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-light);
  border-radius: 16px;
  color: var(--primary-color);
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.feature-desc {
  font-size: 14px;
  color: var(--text-tertiary);
  margin: 0;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .section-inner {
    padding: 0 16px;
  }
  .promo-section {
    padding: 0 16px;
  }
  .promo-banner {
    flex-direction: column;
  }
  .promo-content {
    padding: 30px;
  }
  .promo-image {
    width: 100%;
    height: 200px;
  }
  .filter-bar {
    flex-direction: column;
  }
  .search-box {
    width: 100%;
  }
  .filter-controls {
    width: 100%;
    justify-content: space-between;
  }
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  .category-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
