import { createApp } from 'vue'
import { createPinia } from 'pinia'

import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import App from './App.vue'
import router from './router'
import { setSessionExpiredHandler } from './api/http'
import { useAuthStore } from './stores/auth'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// Refresh token cũng hết hạn / bị thu hồi -> xóa phiên và quay về trang đăng nhập
setSessionExpiredHandler(() => {
  useAuthStore().clear()
  router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
})

app.mount('#app')
