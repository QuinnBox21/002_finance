import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import BasicLayout from '../layouts/BasicLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: BasicLayout,
      children: [
        {
          path: '',
          redirect: '/dashboard'
        },
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('../views/DashboardView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/transactions',
          name: 'transactions',
          component: () => import('../views/TransactionsView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/budget',
          name: 'budget',
          component: () => import('../views/BudgetView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/reports',
          name: 'reports',
          component: () => import('../views/ReportsView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/advice',
          name: 'advice',
          component: () => import('../views/AdviceView.vue'),
          meta: { requiresAuth: true }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next('/login')
  } else if (!to.meta.requiresAuth && userStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router