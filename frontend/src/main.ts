import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router/index.ts'
import i18n from './locales'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.use(i18n)

app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误:', err)
  console.error('错误信息:', info)
  
  router.push({
    path: '/error',
    query: {
      code: '500',
      message: err instanceof Error ? err.message : '未知错误',
      stack: err instanceof Error ? err.stack : ''
    }
  })
}

window.addEventListener('unhandledrejection', (event) => {
  console.error('未处理的 Promise 拒绝:', event.reason)
  event.preventDefault()
  
  router.push({
    path: '/error',
    query: {
      code: '500',
      message: event.reason instanceof Error ? event.reason.message : '异步操作失败',
      stack: event.reason instanceof Error ? event.reason.stack : ''
    }
  })
})

window.addEventListener('error', (event) => {
  console.error('全局错误:', event.error)
  event.preventDefault()
  
  router.push({
    path: '/error',
    query: {
      code: '500',
      message: event.error?.message || '页面加载错误',
      stack: event.error?.stack || ''
    }
  })
})

app.mount('#app')
