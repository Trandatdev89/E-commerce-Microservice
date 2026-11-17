import { request } from './http'
import type { AuthTokens, RegisterPayload, RegisterResult } from './types'

const BASE = '/identity/auth'

export const authApi = {
  register: (payload: RegisterPayload) =>
    request<RegisterResult>({ method: 'POST', url: `${BASE}/register`, data: payload }),

  verify: (userId: string, token: string) =>
    request<void>({ method: 'GET', url: `${BASE}/verify`, params: { userId, token } }),

  resendOtp: (email: string) =>
    request<void>({ method: 'POST', url: `${BASE}/resend-otp`, data: { email } }),

  login: (username: string, password: string) =>
    request<AuthTokens>({ method: 'POST', url: `${BASE}/login`, data: { username, password } }),

  refresh: (refreshToken: string) =>
    request<AuthTokens>({ method: 'POST', url: `${BASE}/refresh-token`, data: { refreshToken } }),

  logout: (accessToken: string, refreshToken: string | null) =>
    request<void>({ method: 'POST', url: `${BASE}/logout`, data: { accessToken, refreshToken } }),
}
