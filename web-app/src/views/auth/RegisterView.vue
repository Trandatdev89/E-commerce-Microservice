<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { authApi } from '@/api/auth'
import type { RegisterPayload } from '@/api/types'
import { showError } from '@/utils/notify'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<RegisterPayload & { confirmPassword: string }>({
  username: '',
  password: '',
  confirmPassword: '',
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  address: '',
})

// Kiểm tra giống các ràng buộc @Valid ở backend (RegisterDTO) để báo lỗi sớm
const rules: FormRules = {
  username: [
    { required: true, message: 'Nhập tên đăng nhập', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9._]{4,50}$/, message: '4-50 ký tự: chữ, số, dấu chấm, gạch dưới', trigger: 'blur' },
  ],
  email: [
    { required: true, message: 'Nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không đúng định dạng', trigger: 'blur' },
  ],
  firstName: [{ required: true, message: 'Nhập họ', trigger: 'blur' }],
  lastName: [{ required: true, message: 'Nhập tên', trigger: 'blur' }],
  phone: [{ pattern: /^(\+84|0)\d{9,10}$/, message: 'Số điện thoại không hợp lệ', trigger: 'blur' }],
  password: [
    { required: true, message: 'Nhập mật khẩu', trigger: 'blur' },
    { min: 8, message: 'Mật khẩu tối thiểu 8 ký tự', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: 'Nhập lại mật khẩu', trigger: 'blur' },
    {
      validator: (_rule, value, callback) =>
        value === form.password ? callback() : callback(new Error('Mật khẩu nhập lại không khớp')),
      trigger: 'blur',
    },
  ],
}

async function submit() {
  if (!(await formRef.value?.validate().catch(() => false))) return
  loading.value = true
  try {
    const { confirmPassword: _, ...payload } = form
    const result = await authApi.register({ ...payload, phone: payload.phone || undefined })
    router.push({ name: 'verify', query: { userId: result.userId, email: result.email } })
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
      <template #header>
        <h4 class="m-0">Đăng ký tài khoản</h4>
        <small class="text-muted">Tài khoản mới có vai trò Khách hàng (CLIENT)</small>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="Họ" prop="firstName"><el-input v-model="form.firstName" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Tên" prop="lastName"><el-input v-model="form.lastName" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Tên đăng nhập" prop="username"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="Email" prop="email"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="Số điện thoại" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="Địa chỉ" prop="address"><el-input v-model="form.address" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="Mật khẩu" prop="password">
              <el-input v-model="form.password" type="password" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Nhập lại mật khẩu" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-button type="primary" native-type="submit" :loading="loading" class="w-100">Đăng ký</el-button>
      </el-form>
      <p class="mt-3 mb-0 text-center">
        Đã có tài khoản? <router-link to="/login">Đăng nhập</router-link>
      </p>
    </el-card>
  </div>
</template>
