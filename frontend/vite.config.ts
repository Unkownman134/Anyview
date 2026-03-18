import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import fs from 'fs'

export default defineConfig({
  plugins: [
    vue(),
    {
      name: 'html-fallback',
      configureServer(server) {
        server.middlewares.use((req, res, next) => {
          // 如果是API请求，跳过（只匹配/api/，不匹配/api-configs）
          if (req.url?.startsWith('/api/')) {
            next()
            return
          }
          // 如果是静态资源请求，跳过
          if (req.url?.startsWith('/@') || req.url?.includes('.')) {
            next()
            return
          }
          // 其他请求返回index.html，让Vue Router处理
          const indexPath = path.resolve(__dirname, 'index.html')
          const indexContent = fs.readFileSync(indexPath, 'utf-8')
          res.setHeader('Content-Type', 'text/html')
          res.end(indexContent)
        })
      }
    }
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
