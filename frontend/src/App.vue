<template>
  <div class="app-container">
    <NavBar />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" :key="$route.path" />
        </transition>
      </router-view>
    </main>
    <FooterBar />
    <el-backtop :right="30" :bottom="80" class="custom-backtop">
      <el-icon size="20"><ArrowUp /></el-icon>
    </el-backtop>
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'
import { ArrowUp } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { useInquiryListStore } from '@/stores/inquiryList'
import { useAuthStore } from '@/stores/auth'

const inquiryStore = useInquiryListStore()
const authStore = useAuthStore()

onMounted(() => {
  if (authStore.isLoggedIn) {
    inquiryStore.fetchInquiryList()
  }
})
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
}

.main-content {
  flex: 1;
  min-height: 80vh;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.custom-backtop {
  background: var(--primary-color) !important;
  color: #fff !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(139, 115, 85, 0.3);
  transition: all 0.3s ease;
}

.custom-backtop:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(139, 115, 85, 0.4);
}
</style>
