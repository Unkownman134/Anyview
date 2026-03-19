<template>
  <div class="login-container" :class="{ 'dark-mode': isDarkMode }">
    <div class="login-content">
      <div class="logo">
        <h1>Anyview<span class="platform-name">{{ t('login.title') }}</span></h1>
        <div class="version">{{ version }}</div>
      </div>
      
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            :placeholder="t('register.username')" 
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            :placeholder="t('register.password')" 
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            :placeholder="t('register.confirmPassword')" 
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input 
            v-model="registerForm.email" 
            :placeholder="t('register.email')" 
            prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input 
            v-model="registerForm.realName" 
            :placeholder="t('register.realName')" 
            prefix-icon="Avatar"
          />
        </el-form-item>
        <el-form-item prop="role">
          <el-select 
            v-model="registerForm.role" 
            :placeholder="t('register.role')" 
            prefix-icon="UserFilled"
            class="school-select"
          >
            <el-option :label="t('register.student')" value="STUDENT" />
            <el-option :label="t('register.teacher')" value="TEACHER" />
          </el-select>
        </el-form-item>
        <el-form-item prop="schoolId">
          <el-select 
            v-model="registerForm.schoolId" 
            :placeholder="t('register.school')" 
            prefix-icon="Location"
            class="school-select"
            :loading="schoolLoading"
          >
            <el-option 
              v-for="school in schools" 
              :key="school.id" 
              :label="school.name" 
              :value="school.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" class="login-button">{{ t('common.register') }}</el-button>
        </el-form-item>
        <div class="form-footer">
          <el-button 
            :icon="isDarkMode ? 'Sunny' : 'Moon'" 
            @click="toggleDarkMode"
            class="theme-toggle"
          />
        </div>
      </el-form>
      
      <div class="register-link">
        <el-link type="primary" @click="$router.push('/login')">{{ t('register.hasAccount') }} {{ t('register.loginNow') }}</el-link>
      </div>
    </div>
    
    <div class="footer">
      Anyview {{ t('login.title') }} - 智能化编程学习解决方案
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'
import { getEnabledSchools } from '@/api/school'
import { useI18n } from 'vue-i18n'
import packageInfo from '../../package.json'

const router = useRouter()
const registerFormRef = ref()
const schools = ref([])
const schoolLoading = ref(false)
const isDarkMode = ref(false)
const version = ref(`v${packageInfo.version}`)
const { t } = useI18n()

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  realName: '',
  role: 'STUDENT',
  schoolId: null
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  schoolId: [{ required: true, message: '请选择学校', trigger: 'change' }]
}

const loadSchools = async () => {
  try {
    schoolLoading.value = true
    const res = await getEnabledSchools()
    schools.value = res.data || []
  } catch (error) {
    ElMessage.error('加载学校列表失败')
  } finally {
    schoolLoading.value = false
  }
}

const handleRegister = async () => {
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await register(registerForm)
        ElMessage.success(t('register.registerSuccess'))
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || t('register.registerFailed'))
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
  justify-content: flex-end;
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