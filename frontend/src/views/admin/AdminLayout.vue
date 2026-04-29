<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px" class="admin-sidebar">
        <div class="sidebar-header">
          <router-link to="/admin/dashboard" class="sidebar-logo">⚙️ 管理后台</router-link>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          router
          :collapse="false"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/reviews">
            <el-icon><ChatDotSquare /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
          <el-divider class="sidebar-divider" />
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>返回前台</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  DataAnalysis, Goods, List, User, ChatDotSquare, HomeFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background: #f5f5f5;
}

.admin-sidebar {
  background: #fff;
  border-right: 1px solid #e8e3dc;
  overflow-y: auto;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e8e3dc;
}

.sidebar-logo {
  font-size: 18px;
  font-weight: 700;
  color: #8B7355;
  text-decoration: none;
}

.sidebar-menu {
  border-right: none;
  margin-top: 8px;
}

:deep(.el-menu-item) {
  margin: 2px 8px;
  border-radius: 8px;
  font-size: 14px;
}

:deep(.el-menu-item.is-active) {
  background: #f0ebe4;
  color: #8B7355;
  font-weight: 600;
}

:deep(.el-menu-item:hover) {
  background: #f5f0ea;
}

.sidebar-divider {
  margin: 8px 16px;
  border-top-color: #e8e3dc;
}

.admin-main {
  padding: 24px;
  background: #f5f5f5;
  overflow-y: auto;
}

[data-theme="dark"] .admin-layout {
  background: #1a1a1a;
}

[data-theme="dark"] .admin-sidebar {
  background: #1e1e1e;
  border-right-color: #333;
}

[data-theme="dark"] .sidebar-header {
  border-bottom-color: #333;
}

[data-theme="dark"] .sidebar-logo {
  color: #a89273;
}

[data-theme="dark"] :deep(.el-menu-item.is-active) {
  background: #2a2520;
  color: #a89273;
}

[data-theme="dark"] :deep(.el-menu-item:hover) {
  background: #2a2a2a;
}

[data-theme="dark"] .sidebar-divider {
  border-top-color: #333;
}

[data-theme="dark"] .admin-main {
  background: #1a1a1a;
}
</style>
