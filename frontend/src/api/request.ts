import axios, { AxiosInstance } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router/index.ts'

const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  (config: any) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response: any) => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('拒绝访问')
          router.push({
            path: '/error',
            query: {
              code: '403',
              message: '拒绝访问',
              stack: ''
            }
          })
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          router.push({
            path: '/error',
            query: {
              code: '404',
              message: '请求的资源不存在',
              stack: ''
            }
          })
          break
        case 500:
          ElMessage.error('服务器错误')
          router.push({
            path: '/error',
            query: {
              code: '500',
              message: error.response.data?.message || '服务器内部错误',
              stack: ''
            }
          })
          break
        default:
          const errorMessage = error.response.data?.message || '请求失败'
          ElMessage.error(errorMessage)
          router.push({
            path: '/error',
            query: {
              code: String(error.response.status),
              message: errorMessage,
              stack: ''
            }
          })
      }
    } else {
      ElMessage.error('网络错误')
      router.push({
        path: '/error',
        query: {
          code: 'NETWORK',
          message: '网络连接错误，请检查网络设置',
          stack: ''
        }
      })
    }
    return Promise.reject(error)
  }
)

export default request as any
