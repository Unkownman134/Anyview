<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">AnyView</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item v-if="isTeacher || isAdmin" index="/classes">
          <el-icon><School /></el-icon>
          <span>班级管理</span>
        </el-menu-item>
        <el-menu-item v-if="isTeacher || isAdmin" index="/questions">
          <el-icon><Document /></el-icon>
          <span>题库管理</span>
        </el-menu-item>
        <el-menu-item v-if="isTeacher || isAdmin" index="/assignments">
          <el-icon><Edit /></el-icon>
          <span>作业管理</span>
        </el-menu-item>
        <el-menu-item v-if="isStudent" index="/assignments">
          <el-icon><Edit /></el-icon>
          <span>作业列表</span>
        </el-menu-item>
        <el-menu-item index="/submissions">
          <el-icon><Tickets /></el-icon>
          <span>提交记录</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/api-configs">
          <el-icon><Setting /></el-icon>
          <span>API管理</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/schools">
          <el-icon><School /></el-icon>
          <span>学校管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-content">
          <span>{{ userStore.user?.realName }} ({{ userStore.user?.role }})</span>
          <el-button @click="toggleDarkMode" circle>
            <el-icon v-if="!isDark"><Moon /></el-icon>
            <el-icon v-else><Sunny /></el-icon>
          </el-button>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-icon><Setting /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 暗黑模式状态
const isDark = ref(false)

// 切换暗黑模式
const toggleDarkMode = () => {
  isDark.value = !isDark.value
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}

// 初始化主题
onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    isDark.value = true
    document.documentElement.classList.add('dark')
  } else {
    isDark.value = false
    document.documentElement.classList.remove('dark')
  }
})

const activeMenu = computed(() => route.path)
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')
const isTeacher = computed(() => userStore.user?.role === 'TEACHER')
const isStudent = computed(() => userStore.user?.role === 'STUDENT')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  background-color: #2b3a4a;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  font-size: 20px;
}

/* 暗黑模式样式 */
html.dark .el-header {
  background-color: #1a1a1a;
  border-bottom-color: #333;
  color: #e0e0e0;
}

html.dark .el-main {
  background-color: #2a2a2a;
  color: #e0e0e0;
}

html.dark .el-aside {
  background-color: #1f2d3d;
}

html.dark .logo {
  background-color: #1a2332;
}

html.dark .el-menu {
  background-color: #1f2d3d !important;
  text-color: #e0e0e0 !important;
}

html.dark .el-dropdown-link {
  color: #409EFF;
}
</style>
