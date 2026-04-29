<template>
  <transition name="compare-slide">
    <div class="compare-bar" v-if="items.length > 0">
      <div class="compare-inner">
        <div class="compare-label">
          <el-icon><DataAnalysis /></el-icon>
          对比列表 (<span class="compare-count">{{ items.length }}</span>)
        </div>
        <div class="compare-items">
          <div class="compare-item" v-for="item in items" :key="item.id">
            <img :src="item.image || 'https://via.placeholder.com/60x60/f5f0eb/8B7355'" :alt="item.name" class="compare-img" />
            <span class="compare-name">{{ item.name }}</span>
            <button class="compare-remove" @click="removeItem(item.id)">
              <el-icon><Close /></el-icon>
            </button>
          </div>
        </div>
        <div class="compare-actions">
          <el-button type="primary" @click="openCompareModal" :disabled="items.length < 2">
            开始对比
          </el-button>
          <el-button @click="clearAll">清空</el-button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { DataAnalysis, Close } from '@element-plus/icons-vue'

const items = ref([])

function loadItems() {
  try {
    items.value = JSON.parse(localStorage.getItem('qiju_compare') || '[]')
  } catch {
    items.value = []
  }
}

function removeItem(id) {
  items.value = items.value.filter(item => item.id !== id)
  localStorage.setItem('qiju_compare', JSON.stringify(items.value))
  window.dispatchEvent(new CustomEvent('compare-updated', { detail: [...items.value] }))
}

function clearAll() {
  items.value = []
  localStorage.setItem('qiju_compare', JSON.stringify([]))
  window.dispatchEvent(new CustomEvent('compare-updated', { detail: [] }))
}

function openCompareModal() {
  ElMessage.info('对比功能开发中，敬请期待！')
}

function handleCompareUpdate(e) {
  items.value = e.detail
}

onMounted(() => {
  loadItems()
  window.addEventListener('compare-updated', handleCompareUpdate)
})

onUnmounted(() => {
  window.removeEventListener('compare-updated', handleCompareUpdate)
})
</script>

<style scoped>
.compare-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background: var(--card-bg);
  border-top: 1px solid var(--border-color);
  box-shadow: 0 -4px 20px var(--shadow-color);
  padding: 12px 0;
}

.compare-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.compare-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
}

.compare-count {
  color: var(--primary-color);
  font-size: 16px;
}

.compare-items {
  display: flex;
  gap: 12px;
  flex: 1;
  overflow-x: auto;
}

.compare-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: var(--hover-bg);
  border-radius: 8px;
  flex-shrink: 0;
  position: relative;
}

.compare-img {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  object-fit: cover;
}

.compare-name {
  font-size: 13px;
  color: var(--text-secondary);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.compare-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all 0.2s;
}

.compare-remove:hover {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

.compare-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.compare-slide-enter-active,
.compare-slide-leave-active {
  transition: transform 0.3s ease;
}

.compare-slide-enter-from,
.compare-slide-leave-to {
  transform: translateY(100%);
}

@media (max-width: 768px) {
  .compare-inner {
    padding: 0 16px;
    flex-wrap: wrap;
  }
  .compare-label {
    width: 100%;
  }
  .compare-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
