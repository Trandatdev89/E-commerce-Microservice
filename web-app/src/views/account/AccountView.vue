<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { profileApi } from '@/api/profile'
import { notificationApi } from '@/api/notification'
import { sessionApi } from '@/api/session'
import type { AppNotification, LoginSession, Profile } from '@/api/types'
import { showError } from '@/utils/notify'

const auth = useAuthStore()
const router = useRouter()

const profile = ref<Profile | null>(null)
const profileError = ref('')
const notifications = ref<AppNotification[]>([])
const sessions = ref<LoginSession[]>([])
const loadingSessions = ref(false)
const unread = ref(0)
const loadingNotifications = ref(false)
const refreshing = ref(false)
const now = ref(Date.now())
let clock: ReturnType<typeof setInterval> | undefined

/** Số giây access token còn hiệu lực, cập nhật mỗi giây */
const secondsLeft = computed(() => Math.max(0, Math.floor((auth.expiresAt - now.value) / 1000)))
const countdown = computed(() => {
  const m = Math.floor(secondsLeft.value / 60)
  const s = secondsLeft.value % 60
  return `${m}:${String(s).padStart(2, '0')}`
})

const shorten = (token: string | null) => (token ? `${token.slice(0, 18)}…${token.slice(-10)}` : '—')
const formatTime = (value: string | null) => (value ? new Date(value).toLocaleString('vi-VN') : '—')

const statusTag: Record<string, 'success' | 'warning' | 'danger'> = { SENT: 'success', PENDING: 'warning', FAILED: 'danger' }
const typeLabel: Record<string, string> = {
  VERIFY_ACCOUNT: 'Xác thực tài khoản',
  ORDER_CONFIRMATION: 'Đặt hàng',
  PAYMENT_CONFIRMATION: 'Thanh toán',
  NEW_DEVICE_LOGIN: 'Đăng nhập thiết bị mới',
}

async function loadProfile() {
  profileError.value = ''
  try {
    profile.value = await profileApi.getByUserId(auth.userId)
  } catch (e) {
    // Hồ sơ được tạo bất đồng bộ qua Kafka, có thể chậm vài trăm ms sau khi xác thực
    profileError.value = e instanceof Error ? e.message : String(e)
  } finally {
    auth.syncFromStorage()
  }
}

async function loadNotifications() {
  loadingNotifications.value = true
  try {
    const [page, count] = await Promise.all([notificationApi.list(0, 20), notificationApi.unreadCount()])
    notifications.value = page.content
    unread.value = count
  } catch (e) {
    showError(e)
  } finally {
    loadingNotifications.value = false
    auth.syncFromStorage()
  }
}

const rowClassName = ({ row }: { row: AppNotification }) => (row.read ? '' : 'unread-row')

async function markAsRead(item: AppNotification) {
  if (item.read) return
  try {
    await notificationApi.markAsRead(item.id)
    item.read = true
    unread.value = Math.max(0, unread.value - 1)
  } catch (e) {
    showError(e)
  }
}

async function loadSessions() {
  loadingSessions.value = true
  try {
    sessions.value = await sessionApi.list()
  } catch (e) {
    showError(e)
  } finally {
    loadingSessions.value = false
    auth.syncFromStorage()
  }
}

async function revokeSession(session: LoginSession) {
  try {
    await ElMessageBox.confirm(
      session.current
        ? 'Đây là thiết bị bạn đang dùng, đăng xuất phiên này sẽ đăng xuất bạn. Tiếp tục?'
        : `Đăng xuất ${session.device} (IP ${session.ipAddress ?? 'không rõ'})?`,
      'Đăng xuất thiết bị',
      { type: 'warning', confirmButtonText: 'Đăng xuất', cancelButtonText: 'Hủy' },
    )
  } catch {
    return
  }
  try {
    await sessionApi.revoke(session.sessionId)
    if (session.current) {
      await logout()
      return
    }
    ElMessage.success('Đã đăng xuất thiết bị. Thiết bị đó sẽ bị văng ra khi access token hết hạn')
    loadSessions()
  } catch (e) {
    showError(e)
  }
}

async function refreshToken() {
  refreshing.value = true
  try {
    await auth.refresh()
    ElMessage.success('Đã đổi sang cặp token mới (refresh token cũ bị thu hồi)')
    loadSessions()
  } catch (e) {
    showError(e)
  } finally {
    refreshing.value = false
  }
}

async function logout() {
  await auth.logout().catch(showError)
  ElMessage.success('Đã đăng xuất')
  router.push('/login')
}

onMounted(() => {
  clock = setInterval(() => (now.value = Date.now()), 1000)
  loadProfile()
  loadSessions()
  loadNotifications()
})
onUnmounted(() => clearInterval(clock))
</script>

<template>
  <div class="container py-4">
    <el-row :gutter="16">
      <el-col :md="12" :xs="24" class="mb-3">
        <el-card>
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <b>Tài khoản (từ access token)</b>
              <el-button type="danger" plain size="small" @click="logout">Đăng xuất</el-button>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="Username">{{ auth.username }}</el-descriptions-item>
            <el-descriptions-item label="User ID"><code>{{ auth.userId }}</code></el-descriptions-item>
            <el-descriptions-item label="Vai trò">
              <el-tag v-for="role in auth.roles" :key="role" class="mr-1">{{ role }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Access token hết hạn sau">
              <el-tag :type="secondsLeft > 60 ? 'success' : 'danger'">{{ countdown }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Access token"><code class="small">{{ shorten(auth.accessToken) }}</code></el-descriptions-item>
            <el-descriptions-item label="Refresh token"><code class="small">{{ shorten(auth.refreshToken) }}</code></el-descriptions-item>
          </el-descriptions>
          <el-button class="mt-3" :loading="refreshing" @click="refreshToken">Làm mới token (refresh)</el-button>
        </el-card>
      </el-col>

      <el-col :md="12" :xs="24" class="mb-3">
        <el-card>
          <template #header>
            <div class="d-flex justify-content-between align-items-center">
              <b>Hồ sơ (profile-service)</b>
              <el-button size="small" @click="loadProfile">Tải lại</el-button>
            </div>
          </template>
          <el-descriptions v-if="profile" :column="1" border>
            <el-descriptions-item label="Họ tên">{{ profile.firstName }} {{ profile.lastName }}</el-descriptions-item>
            <el-descriptions-item label="Email">{{ profile.email }}</el-descriptions-item>
            <el-descriptions-item label="Số điện thoại">{{ profile.phone || '—' }}</el-descriptions-item>
            <el-descriptions-item label="Địa chỉ">{{ profile.address || '—' }}</el-descriptions-item>
          </el-descriptions>
          <el-alert v-else-if="profileError" type="warning" :closable="false" show-icon
            :title="`Chưa đọc được hồ sơ: ${profileError}`"
            description="Hồ sơ được tạo qua Kafka sau khi xác thực. Bấm Tải lại sau vài giây." />
          <el-skeleton v-else :rows="3" animated />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mb-3">
      <template #header>
        <div class="d-flex justify-content-between align-items-center">
          <b>Thiết bị đang đăng nhập (identity-service)</b>
          <el-button size="small" :loading="loadingSessions" @click="loadSessions">Tải lại</el-button>
        </div>
      </template>
      <el-table :data="sessions" v-loading="loadingSessions" empty-text="Không có phiên nào">
        <el-table-column label="Thiết bị" min-width="200">
          <template #default="{ row }">
            {{ row.device }}
            <el-tag v-if="row.current" type="success" size="small" class="ml-1">Thiết bị này</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP" width="150" />
        <el-table-column label="Hoạt động gần nhất" width="180">
          <template #default="{ row }">{{ formatTime(row.lastActiveAt) }}</template>
        </el-table-column>
        <el-table-column label="Hết hạn" width="180">
          <template #default="{ row }">{{ formatTime(row.expiresAt) }}</template>
        </el-table-column>
        <el-table-column width="120">
          <template #default="{ row }">
            <el-button type="danger" link @click="revokeSession(row)">Đăng xuất</el-button>
          </template>
        </el-table-column>
      </el-table>
      <small class="text-muted">
        Mỗi lần làm mới token, phiên được cấp id mới. Đăng nhập từ trình duyệt / hệ điều hành chưa từng dùng sẽ có email cảnh báo.
      </small>
    </el-card>

    <el-card>
      <template #header>
        <div class="d-flex justify-content-between align-items-center">
          <b>
            Thông báo (notification-service)
            <el-badge v-if="unread > 0" :value="unread" class="ml-2" />
          </b>
          <el-button size="small" :loading="loadingNotifications" @click="loadNotifications">Tải lại</el-button>
        </div>
      </template>
      <el-table :data="notifications" v-loading="loadingNotifications" empty-text="Chưa có thông báo"
        :row-class-name="rowClassName"
        @row-click="markAsRead">
        <el-table-column label="Loại" width="170">
          <template #default="{ row }">{{ typeLabel[row.type] ?? row.type }}</template>
        </el-table-column>
        <el-table-column prop="subject" label="Tiêu đề" min-width="220" />
        <el-table-column label="Trạng thái gửi" width="130">
          <template #default="{ row }">
            <el-tag :type="statusTag[row.status]" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Thời gian" width="180">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="Đã đọc" width="90">
          <template #default="{ row }">{{ row.read ? '✓' : '' }}</template>
        </el-table-column>
      </el-table>
      <small class="text-muted">Bấm vào một dòng để đánh dấu đã đọc.</small>
    </el-card>
  </div>
</template>

<style scoped>
:deep(.unread-row) {
  font-weight: 600;
  cursor: pointer;
}
</style>
