<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <header class="app-header">
    <div class="container d-flex justify-content-between align-items-center">
      <RouterLink to="/" class="brand">E-commerce</RouterLink>
      <nav v-if="auth.isAuthenticated">
        <span class="mr-3">Xin chào, <b>{{ auth.username }}</b></span>
        <RouterLink to="/account">Tài khoản</RouterLink>
      </nav>
      <nav v-else>
        <RouterLink to="/login" class="mr-3">Đăng nhập</RouterLink>
        <RouterLink to="/register">Đăng ký</RouterLink>
      </nav>
    </div>
  </header>
  <main>
    <RouterView />
  </main>
</template>

<style>
body {
  background: #f5f7fa;
}
.app-header {
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  padding: 12px 0;
}
.app-header .brand {
  font-weight: 700;
  font-size: 18px;
  color: #409eff;
}
.auth-page {
  display: flex;
  justify-content: center;
  padding: 40px 16px;
}
.auth-card {
  width: 100%;
  max-width: 480px;
}
</style>
