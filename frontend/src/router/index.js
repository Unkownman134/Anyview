import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {        path: 'classes',        name: 'Classes',        component: () => import('@/views/Classes.vue'),        meta: { requiresTeacher: true }      },      {        path: 'questions',        name: 'Questions',        component: () => import('@/views/Questions.vue'),        meta: { requiresTeacher: true }      },      {        path: 'assignments',        name: 'Assignments',        component: () => import('@/views/Assignments.vue'),        meta: { requiresTeacher: true }      },
      {
        path: 'submissions',
        name: 'Submissions',
        component: () => import('@/views/Submissions.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue'),
        meta: { requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.path === '/login' || to.path === '/register') {
    next()
  } else {
    if (token) {
      if (to.meta.requiresAdmin && userStore.user?.role !== 'ADMIN') {
        next('/dashboard')
      } else if (to.meta.requiresTeacher && userStore.user?.role !== 'TEACHER' && userStore.user?.role !== 'ADMIN') {
        next('/dashboard')
      } else {
        next()
      }
    } else {
      next('/login')
    }
  }
})

export default router
