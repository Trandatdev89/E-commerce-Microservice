import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/account' },
    { path: '/login', name: 'login', component: () => import('@/views/auth/LoginView.vue'), meta: { guestOnly: true } },
    { path: '/register', name: 'register', component: () => import('@/views/auth/RegisterView.vue'), meta: { guestOnly: true } },
    // Link xác thực trong email trỏ về đây: /verify?userId=...&token=...
    { path: '/verify', name: 'verify', component: () => import('@/views/auth/VerifyView.vue') },
    { path: '/account', name: 'account', component: () => import('@/views/account/AccountView.vue'), meta: { requiresAuth: true } },
    { path: '/chat', name: 'chat', component: () => import('@/views/Home.vue'), meta: { requiresAuth: true } },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guestOnly && auth.isAuthenticated) {
    return { name: 'account' }
  }
})

export default router
