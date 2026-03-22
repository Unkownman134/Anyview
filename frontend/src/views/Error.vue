<template>
  <div class="error-container" :class="{ 'dark-mode': isDarkMode }">
    <div class="error-content">
      <div class="logo">
        <h1>Anyview<span class="platform-name">{{ t('login.title') }}</span></h1>
        <div class="version">{{ version }}</div>
      </div>
      
      <div class="error-icon">
        <el-icon :size="80" color="#f56c6c">
          <Warning />
        </el-icon>
      </div>
      <h1 class="error-title">{{ errorTitle }}</h1>
      <p class="error-message">{{ errorMessage }}</p>
      <div class="error-details" v-if="errorDetails">
        <el-collapse>
          <el-collapse-item :title="t('error.showDetails')" name="1">
            <div class="error-stack">{{ errorDetails }}</div>
          </el-collapse-item>
        </el-collapse>
      </div>
      <div class="error-actions">
        <el-button type="primary" @click="goBack" size="large" class="action-button">
          <el-icon><Back /></el-icon>
          {{ t('error.goBack') }}
        </el-button>
        <el-button @click="goHome" size="large" class="action-button">
          <el-icon><HomeFilled /></el-icon>
          {{ t('error.goHome') }}
        </el-button>
      </div>
    </div>
    
    <div class="footer">
      Anyview {{ t('login.title') }} - 智能化编程学习解决方案
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Warning, Back, HomeFilled } from '@element-plus/icons-vue'
import packageInfo from '../../package.json'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const isDarkMode = ref(false)
const version = ref(`v${packageInfo.version}`)
const errorTitle = ref(t('error.defaultTitle'))
const errorMessage = ref(t('error.defaultMessage'))
const errorDetails = ref('')

onMounted(() => {
  const errorCode = route.query.code as string
  const errorMsg = route.query.message as string
  const errorStack = route.query.stack as string

  if (errorCode) {
    errorTitle.value = `${t('error.error')} ${errorCode}`
  }

  if (errorMsg) {
    errorMessage.value = errorMsg
  }

  if (errorStack) {
    errorDetails.value = errorStack
  }
  
  // 检查系统主题设置
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    toggleDarkMode()
  }
})

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const goHome = () => {
  router.push('/')
}

const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  document.documentElement.classList.toggle('dark-mode', isDarkMode.value)
}
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

.el-collapse-item__content {
  background-color: var(--card-background) !important;
}
</style>

<style scoped>
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: var(--background-color);
  transition: background-color 0.3s;
  padding: 20px;
}

.error-content {
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
  margin-top: 10px;
}

.dark-mode .version {
  color: #999999;
}

.error-icon {
  margin-bottom: 30px;
}

.error-title {
  font-size: 32px;
  color: var(--text-color);
  margin-bottom: 20px;
  font-weight: 600;
  text-align: center;
}

.error-message {
  font-size: 16px;
  color: #606266;
  margin-bottom: 30px;
  line-height: 1.6;
  text-align: center;
  max-width: 100%;
}

.dark-mode .error-message {
  color: #cccccc;
}

.error-details {
  margin-bottom: 30px;
  width: 100%;
  text-align: left;
}

.error-stack {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #909399;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
}

.dark-mode .error-stack {
  background: #333333;
  color: #cccccc;
}

.error-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  width: 100%;
  margin-top: 20px;
}

.action-button {
  min-width: 120px;
  height: 40px;
  font-size: 14px;
}

.action-button:first-child {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
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
  .error-content {
    width: 90%;
    padding: 20px 10px;
  }
  
  .logo h1 {
    font-size: 28px;
  }
  
  .error-title {
    font-size: 24px;
  }
  
  .error-actions {
    flex-direction: column;
  }
  
  .action-button {
    width: 100%;
  }
}
</style>