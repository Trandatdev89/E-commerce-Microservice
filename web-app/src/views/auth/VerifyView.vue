<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { showError } from '@/utils/notify'

const route = useRoute()
const userId = computed(() => String(route.query.userId ?? ''))
const email = ref(String(route.query.email ?? ''))
const otp = ref(String(route.query.token ?? ''))

const verifying = ref(false)
const verified = ref(false)
const resending = ref(false)
const cooldown = ref(0)
let timer: ReturnType<typeof setInterval> | undefined

async function verify() {
  if (!userId.value) {
    ElMessage.warning('Thiếu userId, hãy mở link trong email hoặc đăng ký lại')
    return
  }
  if (!/^\d{6}$/.test(otp.value)) {
    ElMessage.warning('Mã xác thực gồm 6 chữ số')
    return
  }
  verifying.value = true
  try {
    await authApi.verify(userId.value, otp.value)
    verified.value = true
  } catch (e) {
    showError(e)
  } finally {
    verifying.value = false
  }
}

/** Backend chặn gửi lại trong 60 giây, FE đếm ngược tương ứng */
async function resend() {
  if (!email.value) {
    ElMessage.warning('Nhập email đã đăng ký')
    return
  }
  resending.value = true
  try {
    await authApi.resendOtp(email.value)
    ElMessage.success('Đã gửi lại mã, kiểm tra email của bạn')
    startCooldown()
  } catch (e) {
    showError(e)
  } finally {
    resending.value = false
  }
}

function startCooldown() {
  cooldown.value = 60
  clearInterval(timer)
  timer = setInterval(() => {
    cooldown.value -= 1
    if (cooldown.value <= 0) clearInterval(timer)
  }, 1000)
}

onMounted(() => {
  // Mở từ link trong email (có sẵn userId + token) thì xác thực luôn
  if (userId.value && route.query.token) verify()
  // Vừa đăng ký xong, mã vừa được gửi -> đếm ngược luôn
  else if (route.query.email) startCooldown()
})
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <el-result v-if="verified" icon="success" title="Xác thực thành công"
        sub-title="Tài khoản đã được kích hoạt, hồ sơ được tạo qua Kafka (profile-service)">
        <template #extra>
          <router-link to="/login"><el-button type="primary">Đăng nhập</el-button></router-link>
        </template>
      </el-result>

      <template v-else>
        <h4>Xác thực tài khoản</h4>
        <p class="text-muted">
          Mã gồm 6 chữ số đã được gửi tới <b>{{ email || 'email của bạn' }}</b>, có hiệu lực 10 phút.
          Nhập sai 5 lần thì mã bị hủy.
        </p>
        <el-form label-position="top" @submit.prevent="verify">
          <el-form-item label="Mã xác thực (OTP)">
            <el-input v-model="otp" maxlength="6" placeholder="123456" size="large" class="otp-input" />
          </el-form-item>
          <el-button type="primary" native-type="submit" :loading="verifying" class="w-100">Xác thực</el-button>
        </el-form>

        <el-divider>Không nhận được mã?</el-divider>
        <el-input v-model="email" placeholder="Email đã đăng ký" class="mb-2" />
        <el-button :disabled="cooldown > 0" :loading="resending" class="w-100" @click="resend">
          {{ cooldown > 0 ? `Gửi lại sau ${cooldown}s` : 'Gửi lại mã' }}
        </el-button>
      </template>
    </el-card>
  </div>
</template>

<style scoped>
.otp-input :deep(input) {
  letter-spacing: 8px;
  font-size: 20px;
  text-align: center;
}
</style>
