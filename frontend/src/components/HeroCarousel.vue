<template>
  <div class="hero-carousel">
    <el-carousel
      :interval="5000"
      height="85vh"
      arrow="always"
      indicator-position="outside"
      trigger="click"
      :autoplay="true"
      :pause-on-hover="false"
      class="hero-slider"
    >
      <el-carousel-item v-for="(slide, index) in slides" :key="index">
        <div class="slide-content" :style="{ background: slide.bg }">
          <div class="slide-bg-image" :style="{ backgroundImage: `url(${slide.image})` }"></div>
          <div class="slide-overlay"></div>
          <div class="slide-text">
            <div class="slide-subtitle" :class="{ 'animate-in': activeIndex === index }">
              {{ slide.subtitle }}
            </div>
            <h2 class="slide-title" :class="{ 'animate-in': activeIndex === index }">
              {{ slide.title }}
            </h2>
            <p class="slide-desc" :class="{ 'animate-in': activeIndex === index }">
              {{ slide.desc }}
            </p>
            <el-button
              type="primary"
              size="large"
              round
              class="slide-btn"
              :class="{ 'animate-in': activeIndex === index }"
              @click="handleAction(slide.action)"
            >
              {{ slide.btnText }}
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const activeIndex = ref(0)

const slides = [
  {
    title: '匠心工艺·自然之美',
    subtitle: '2024 春季新品',
    desc: '甄选优质木材，融合传统工艺与现代设计，打造属于您的理想家居空间',
    btnText: '立即探索',
    image: 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=1600&q=80',
    bg: 'linear-gradient(135deg, #2c1810 0%, #4a3228 50%, #2c1810 100%)',
    action: 'explore'
  },
  {
    title: '北欧风尚·简约生活',
    subtitle: '热门推荐',
    desc: '简约而不简单，每一件家具都承载着对生活的热爱与追求',
    btnText: '查看更多',
    image: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=1600&q=80',
    bg: 'linear-gradient(135deg, #1a237e 0%, #283593 50%, #1a237e 100%)',
    action: 'view_all'
  },
  {
    title: '限时特惠·品质之选',
    subtitle: '春季大促',
    desc: '精选家具低至7折，用温暖的家迎接春暖花开',
    btnText: '抢购优惠',
    image: 'https://images.unsplash.com/photo-1567016432779-094069895ea0?w=1600&q=80',
    bg: 'linear-gradient(135deg, #4a148c 0%, #6a1b9a 50%, #4a148c 100%)',
    action: 'promo'
  }
]

function handleAction(action) {
  if (action === 'explore' || action === 'view_all') {
    document.querySelector('.category-section')?.scrollIntoView({ behavior: 'smooth' })
  } else if (action === 'promo') {
    document.querySelector('.product-grid-section')?.scrollIntoView({ behavior: 'smooth' })
  }
}

let intervalId = null

function startAutoPlay() {
  intervalId = setInterval(() => {
    activeIndex.value = (activeIndex.value + 1) % slides.length
  }, 5000)
}

onMounted(() => {
  startAutoPlay()
})

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId)
})
</script>

<style scoped>
.hero-carousel {
  position: relative;
  margin-top: -70px;
}

.hero-slider {
  min-height: 500px;
}

.slide-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  height: 100%;
}

.slide-bg-image {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0.5;
  transform: scale(1.05);
  transition: transform 8s ease;
}

.el-carousel-item.is-active .slide-bg-image {
  transform: scale(1);
}

.slide-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.6) 0%,
    rgba(0, 0, 0, 0.3) 50%,
    rgba(0, 0, 0, 0.6) 100%
  );
}

.slide-text {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 700px;
  padding: 0 20px;
}

.slide-subtitle {
  font-size: 14px;
  font-weight: 500;
  color: var(--primary-color);
  text-transform: uppercase;
  letter-spacing: 4px;
  margin-bottom: 16px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.2s;
}

.slide-title {
  font-family: 'Noto Serif SC', serif;
  font-size: clamp(32px, 5vw, 56px);
  font-weight: 700;
  color: #fff;
  margin: 0 0 20px;
  line-height: 1.3;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.4s;
}

.slide-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.8;
  margin-bottom: 32px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.6s;
}

.slide-btn {
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.8s;
  font-size: 16px;
  padding: 14px 36px;
}

.animate-in {
  opacity: 1 !important;
  transform: translateY(0) !important;
}

:deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 20px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  transition: all 0.3s;
}

:deep(.el-carousel__arrow:hover) {
  background: rgba(255, 255, 255, 0.25);
}

:deep(.el-carousel__indicators) {
  bottom: 30px;
}

:deep(.el-carousel__indicator) {
  padding: 4px;
}

:deep(.el-carousel__button) {
  width: 40px;
  height: 4px;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.4);
  border: none;
  transition: all 0.3s;
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background: var(--primary-color);
  width: 60px;
}

@media (max-width: 768px) {
  .slide-btn {
    width: 100%;
  }
}
</style>
