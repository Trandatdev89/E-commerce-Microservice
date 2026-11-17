import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import { tokenStorage } from '@/utils/tokenStorage'
import { decodeJwt } from '@/utils/jwt'
import type { AuthTokens } from '@/api/types'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(tokenStorage.getAccessToken())
  const refreshToken = ref<string | null>(tokenStorage.getRefreshToken())

  const claims = computed(() => (accessToken.value ? decodeJwt(accessToken.value) : null))
  const isAuthenticated = computed(() => !!accessToken.value && !!claims.value)
  const username = computed(() => claims.value?.sub ?? '')
  const userId = computed(() => claims.value?.userId ?? '')
  const roles = computed(() => (claims.value?.scope ? claims.value.scope.split(' ') : []))
  /** Thời điểm access token hết hạn (ms) */
  const expiresAt = computed(() => (claims.value ? claims.value.exp * 1000 : 0))

  function setTokens(tokens: Pick<AuthTokens, 'accessToken' | 'refreshToken'>) {
    tokenStorage.save(tokens)
    accessToken.value = tokens.accessToken
    refreshToken.value = tokens.refreshToken
  }

  /** Đồng bộ lại từ localStorage (interceptor axios có thể vừa tự refresh token) */
  function syncFromStorage() {
    accessToken.value = tokenStorage.getAccessToken()
    refreshToken.value = tokenStorage.getRefreshToken()
  }

  function clear() {
    tokenStorage.clear()
    accessToken.value = null
    refreshToken.value = null
  }

  async function login(login: string, password: string) {
    setTokens(await authApi.login(login, password))
  }

  async function refresh() {
    if (!refreshToken.value) throw new Error('Không có refresh token')
    setTokens(await authApi.refresh(refreshToken.value))
  }

  async function logout() {
    try {
      if (accessToken.value) await authApi.logout(accessToken.value, refreshToken.value)
    } finally {
      clear()
    }
  }

  return {
    accessToken, refreshToken, claims, isAuthenticated, username, userId, roles, expiresAt,
    setTokens, syncFromStorage, clear, login, refresh, logout,
  }
})
