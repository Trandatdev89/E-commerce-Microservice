import axios, { AxiosError, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import type { AuthTokens, ResponseData, SubError } from './types'
import { tokenStorage } from '@/utils/tokenStorage'

/** Lỗi đã chuẩn hóa từ ResponseData, có kèm traceId để tra log / Zipkin */
export class ApiError extends Error {
  constructor(
    message: string,
    public readonly status: number,
    public readonly code: string | null,
    public readonly traceId: string | null,
    public readonly fieldErrors: SubError[] = [],
  ) {
    super(message)
  }
}

/** Gọi qua proxy của Vite: /api -> http://localhost:8222/api (gateway) */
export const http = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken()
  if (token && !config.headers.Authorization) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ---- Tự refresh token khi gặp 401 ----

/** Các API auth không cần (và không được) tự refresh khi lỗi 401 */
const AUTH_PATHS = ['/identity/auth/login', '/identity/auth/refresh-token', '/identity/auth/logout']

let refreshPromise: Promise<string> | null = null
let onSessionExpired: () => void = () => {}

/** Store đăng ký hàm này để biết khi nào phiên hết hạn hẳn (refresh cũng thất bại) */
export function setSessionExpiredHandler(handler: () => void) {
  onSessionExpired = handler
}

/**
 * Nhiều request cùng gặp 401 một lúc chỉ gọi refresh 1 lần (refresh token bị rotation,
 * gọi 2 lần song song thì lần sau dùng token đã thu hồi -> backend coi là bị đánh cắp).
 */
function refreshAccessToken(): Promise<string> {
  if (!refreshPromise) {
    const refreshToken = tokenStorage.getRefreshToken()
    refreshPromise = (
      refreshToken
        ? axios
            .post<ResponseData<AuthTokens>>('/api/v1/identity/auth/refresh-token', { refreshToken })
            .then((res) => {
              tokenStorage.save(res.data.data)
              return res.data.data.accessToken
            })
        : Promise.reject(new Error('No refresh token'))
    ).finally(() => {
      refreshPromise = null
    })
  }
  return refreshPromise
}

http.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ResponseData<unknown>>) => {
    const original = error.config as (InternalAxiosRequestConfig & { _retried?: boolean }) | undefined
    const isAuthCall = AUTH_PATHS.some((path) => original?.url?.startsWith(path))

    if (error.response?.status === 401 && original && !original._retried && !isAuthCall) {
      original._retried = true
      try {
        const newToken = await refreshAccessToken()
        original.headers.Authorization = `Bearer ${newToken}`
        return http(original)
      } catch {
        tokenStorage.clear()
        onSessionExpired()
      }
    }
    return Promise.reject(toApiError(error))
  },
)

function toApiError(error: AxiosError<ResponseData<unknown>>): ApiError {
  const body = error.response?.data
  if (!error.response) {
    return new ApiError('Không kết nối được server (gateway đã chạy chưa?)', 0, null, null)
  }
  const fieldErrors = Array.isArray(body?.data) ? (body.data as SubError[]) : []
  const message =
    fieldErrors.length > 0
      ? fieldErrors.map((e) => e.message).join('; ')
      : body?.soaErrorDesc || body?.error || error.message
  return new ApiError(message, error.response.status, body?.soaErrorCode ?? null,
    body?.transactionId ?? error.response.headers['x-trace-id'] ?? null, fieldErrors)
}

/** Gọi API và trả thẳng phần data trong ResponseData */
export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const res = await http.request<ResponseData<T>>(config)
  return res.data.data
}
