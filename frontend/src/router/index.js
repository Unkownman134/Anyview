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
      {
        path: 'classes',
        name: 'Classes',
        component: () => import('@/views/Classes.vue'),
        meta: { requiresTeacher: true }
      },
      {
        path: 'questions',
        name: 'Questions',
        component: () => import('@/views/Questions.vue'),
        meta: { requiresTeacher: true }
      },
      {
        path: 'assignments',
        name: 'Assignments',
        component: () => import('@/views/Assignments.vue')
      },
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
      },
      {
        path: 'schools',
        name: 'Schools',
        component: () => import('@/views/Schools.vue'),
        meta: { requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 如果访问登录或注册页面，直接放行
  if (to.path === '/login' || to.path === '/register') {
    // 如果已登录，访问登录页时重定向到主页
    if (token && userStore.user) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }

  // 检查是否已登录
  if (!token) {
    next('/login')
    return
  }

  // 如果有token但没有用户信息，尝试从localStorage恢复
  if (!userStore.user) {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      try {
        userStore.setUser(JSON.parse(savedUser))
      } catch (error) {
        console.error('恢复用户信息失败:', error)
        userStore.logout()
        next('/login')
        return
      }
    } else {
      userStore.logout()
      next('/login')
      return
    }
  }

  // 检查权限
  if (to.meta.requiresAdmin && userStore.user?.role !== 'ADMIN') {
    next('/dashboard')
  } else if (to.meta.requiresTeacher && userStore.user?.role !== 'TEACHER' && userStore.user?.role !== 'ADMIN') {
    next('/dashboard')
  } else {
    next()
  }
})

export default router