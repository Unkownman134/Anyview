<template>
  <div class="login-container" :class="{ 'dark-mode': isDarkMode }">
    <div class="login-content">
      <div class="logo">
        <h1>Anyview<span class="platform-name">编程实训平台</span></h1>
        <div class="client-type-container">
          <div class="client-type" :class="{ 'scrolling': isScrolling }">
            {{ currentRole }}
          </div>
        </div>
        <div class="version">{{ version }}</div>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入账号" 
            prefix-icon="User"
            @input="handleUsernameInput"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item v-if="showSchoolSelect">
          <el-select 
            v-model="loginForm.school" 
            placeholder="选择学校" 
            prefix-icon="Location"
            class="school-select"
            :loading="schoolLoading"
          >
            <el-option 
              v-for="school in schools" 
              :key="school.value" 
              :label="school.label" 
              :value="school.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-button">登录</el-button>
        </el-form-item>
        <div class="form-footer">
          <el-link type="info" @click="handleForgotPassword">忘记密码</el-link>
          <el-button 
            :icon="isDarkMode ? 'Sunny' : 'Moon'" 
            @click="toggleDarkMode"
            class="theme-toggle"
          />
        </div>
      </el-form>
      
      <div class="register-link">
        <el-link type="primary" @click="$router.push('/register')">还没有账号？立即注册</el-link>
      </div>
    </div>
    
    <div class="footer">
      Anyview 编程实训平台 - 智能化编程学习解决方案
    </div>
    
    <el-dialog v-model="showForgotPasswordDialog" title="忘记密码" width="400px">
      <el-form :model="forgotPasswordForm" :rules="forgotPasswordRules" ref="forgotPasswordFormRef">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="forgotPasswordForm.email" placeholder="请输入注册邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForgotPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleForgotPasswordSubmit" :loading="forgotPasswordLoading">
          发送重置链接
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { login } from '@/api/auth'
import { forgotPassword } from '@/api/password'
import { getEnabledSchools } from '@/api/school'
import { useUserStore } from '@/store/user'
import packageInfo from '../../package.json'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const isDarkMode = ref(false)
const isScrolling = ref(false)
const showForgotPasswordDialog = ref(false)
const forgotPasswordFormRef = ref()
const forgotPasswordLoading = ref(false)
const schoolLoading = ref(false)
const version = ref(`v${packageInfo.version}`)

const loginForm = reactive({
  username: '',
  password: '',
  school: null
})

const forgotPasswordForm = reactive({
  email: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const forgotPasswordRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const schools = ref([])
const currentRole = ref('学生端')

const showSchoolSelect = computed(() => {
  return loginForm.username !== 'admin'
})

const loadSchools = async () => {
  try {
    schoolLoading.value = true
    const res = await getEnabledSchools()
    schools.value = res.data?.map(school => ({
      label: school.name,
      value: school.id
    })) || []
    if (schools.value.length > 0) {
      loginForm.school = schools.value[0].value
    }
  } catch (error) {
    ElMessage.error('加载学校列表失败')
  } finally {
    schoolLoading.value = false
  }
}

const handleUsernameInput = () => {
  if (loginForm.username === 'admin') {
    currentRole.value = '管理员端'
  } else if (loginForm.username.includes('teacher')) {
    currentRole.value = '教师端'
  } else {
    currentRole.value = '学生端'
  }
  
  isScrolling.value = true
  setTimeout(() => {
    isScrolling.value = false
  }, 500)
}

const handleLogin = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const loginData = {
          username: loginForm.username,
          password: loginForm.password
        }
        const res = await login(loginData)
        userStore.setToken(res.data.token)
        userStore.setUser(res.data)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      }
    }
  })
}

const handleForgotPassword = () => {
  showForgotPasswordDialog.value = true
}

const handleForgotPasswordSubmit = async () => {
  await forgotPasswordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        forgotPasswordLoading.value = true
        await forgotPassword(forgotPasswordForm.email)
        ElMessage.success('重置密码链接已发送到您的邮箱')
        showForgotPasswordDialog.value = false
        forgotPasswordForm.email = ''
      } catch (error) {
        ElMessage.error(error.message || '发送失败')
      } finally {
        forgotPasswordLoading.value = false
      }
    }
  })
}

const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  document.documentElement.classList.toggle('dark-mode', isDarkMode.value)
}

onMounted(() => {
  loadSchools()
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    toggleDarkMode()
  }
})
</script>

<style>
/* 全局暗黑模式样式 */
:root {
  --primary-color: #409EFF;
  --background-color: #ffffff;
  --text-color: #333333;
  --border-color: #dcdfe6;
  --card-background: #ffffff;
  --footer-background: #409EFF;
  --footer-text: #ffffff;
}

.dark-mode {
  --primary-color: #409EFF;
  --background-color: #1a1a1a;
  --text-color: #ffffff;
  --border-color: #333333;
  --card-background: #2a2a2a;
  --footer-background: #2c3e50;
  --footer-text: #ffffff;
}

body {
  background-color: var(--background-color);
  color: var(--text-color);
  transition: background-color 0.3s, color 0.3s;
}

/* 调整 Element Plus 组件样式 */
.el-input__wrapper {
  background-color: var(--card-background) !important;
  border-color: var(--border-color) !important;
}

.el-input__inner {
  color: var(--text-color) !important;
}

.el-select__wrapper {
  background-color: var(--card-background) !important;
  border-color: var(--border-color) !important;
}

.el-select__placeholder {
  color: #909399 !important;
}

.el-select__input {
  color: var(--text-color) !important;
}

.el-option {
  background-color: var(--card-background) !important;
  color: var(--text-color) !important;
}

.el-option:hover {
  background-color: #f5f7fa !important;
}

.dark-mode .el-option:hover {
  background-color: #3a3a3a !important;
}
</style>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: var(--background-color);
  transition: background-color 0.3s;
}

.login-content {
  width: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
}

.logo {
  text-align: center;
  margin-bottom: 40px;
}

.logo h1 {
  font-size: 36px;
  font-weight: bold;
  margin: 0;
  color: var(--primary-color);
  white-space: nowrap;
}

.logo .platform-name {
  color: #333;
  margin-left: 5px;
}

.dark-mode .logo .platform-name {
  color: #ffffff;
}

.client-type-container {
  height: 30px;
  overflow: hidden;
}

.client-type {
  font-size: 14px;
  color: #666;
  margin: 10px 0;
  transition: all 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.client-type.scrolling {
  animation: slideUp 0.5s ease;
}

@keyframes slideUp {
  0% {
    transform: translateY(20px);
    opacity: 0;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
}

.dark-mode .client-type {
  color: #cccccc;
}

.version {
  font-size: 14px;
  color: #999;
}

.dark-mode .version {
  color: #999999;
}

.login-form {
  width: 100%;
  margin-bottom: 20px;
}

.school-select {
  width: 100%;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.theme-toggle {
  margin-left: auto;
  border-radius: 8px;
  width: 36px;
  height: 36px;
  padding: 0;
}

.register-link {
  margin-top: 20px;
}

.footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  text-align: center;
  padding: 15px 0;
  background-color: var(--footer-background);
  color: var(--footer-text);
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-content {
    width: 90%;
    padding: 20px 10px;
  }
  
  .logo h1 {
    font-size: 28px;
  }
}
</style>
