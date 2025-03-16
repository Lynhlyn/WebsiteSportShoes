import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/Admin/HomePage.vue'),
    },
    {
      path: '/',
      name: 'user-home',
      component: () => import('@/views/User/TrangChu/HomePage.vue'),
    },
  ],
})

export default router
