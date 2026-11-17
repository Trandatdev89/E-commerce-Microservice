<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { showError } from '@/utils/notify'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules: FormRules = {
  username: [{ required: true, message: 'Nhập username hoặc email', trigger: 'blur' }],
  password: [{ required: true, message: 'Nhập mật khẩu', trigger: 'blur' }],
}

async function submit() {
  if (!(await formRef.value?.validate().catch(() => false))) return
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    router.push(typeof route.query.redirect === 'string' ? route.query.redirect : '/account')
  } catch (e) {
    showError(e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <template #header><h4 class="m-0">Đăng nhập</h4></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
        <el-form-item label="Username hoặc email" prop="username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="Mật khẩu" prop="password">
          <el-input v-model="form.password" type="password" show-password autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="loading" class="w-100">Đăng nhập</el-button>
      </el-form>
      <p class="mt-3 mb-0 text-center">
        Chưa có tài khoản? <router-link to="/register">Đăng ký</router-link>
        · <router-link to="/verify">Nhập mã xác thực</router-link>
      </p>
    </el-card>
  </div>
</template>
