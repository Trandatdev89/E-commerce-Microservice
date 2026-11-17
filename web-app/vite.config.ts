import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5173,
    // Trình duyệt gọi /api/... cùng origin với FE, Vite chuyển tiếp sang gateway -> không bị lỗi CORS khi dev
    proxy: {
      '/api': {
        target: 'http://localhost:8222',
        changeOrigin: true,
      },
    },
  },
})
