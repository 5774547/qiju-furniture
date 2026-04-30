import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/views/admin/AdminLayout.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '栖居家具 - 匠心品质·自然生活' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/inquiry-list',
    name: 'InquiryList',
    component: () => import('@/views/InquiryList.vue'),
    meta: { title: '询价清单' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/auth/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/inquiries',
    name: 'InquiryListPage',
    component: () => import('@/views/InquiryListPage.vue'),
    meta: { title: '我的询价单', requiresAuth: true }
  },
  {
    path: '/inquiries/:id',
    name: 'InquiryDetail',
    component: () => import('@/views/InquiryDetail.vue'),
    meta: { title: '询价单详情', requiresAuth: true }
  },
  // Admin routes - grouped under AdminLayout
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '仪表盘 - 管理后台' }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/AdminProducts.vue'),
        meta: { title: '商品管理 - 管理后台' }
      },
      {
        path: 'inquiries',
        name: 'AdminInquiries',
        component: () => import('@/views/admin/AdminInquiries.vue'),
        meta: { title: '询价管理 - 管理后台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/AdminUsers.vue'),
        meta: { title: '用户管理 - 管理后台' }
      },
      {
        path: 'reviews',
        name: 'AdminReviews',
        component: () => import('@/views/admin/AdminReviews.vue'),
        meta: { title: '评价管理 - 管理后台' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404 - 页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '栖居家具'

  const token = localStorage.getItem('qiju_token')
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
