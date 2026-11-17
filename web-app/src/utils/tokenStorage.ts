import type { AuthTokens } from '@/api/types'

const ACCESS_TOKEN = 'accessToken'
const REFRESH_TOKEN = 'refreshToken'

/**
 * Lưu token ở localStorage cho đơn giản khi học / dev.
 * Production nên để refresh token trong cookie HttpOnly để JS (và mã độc XSS) không đọc được.
 */
export const tokenStorage = {
  getAccessToken: (): string | null => localStorage.getItem(ACCESS_TOKEN),
  getRefreshToken: (): string | null => localStorage.getItem(REFRESH_TOKEN),
  save(tokens: Pick<AuthTokens, 'accessToken' | 'refreshToken'>) {
    localStorage.setItem(ACCESS_TOKEN, tokens.accessToken)
    localStorage.setItem(REFRESH_TOKEN, tokens.refreshToken)
  },
  clear() {
    localStorage.removeItem(ACCESS_TOKEN)
    localStorage.removeItem(REFRESH_TOKEN)
  },
}
