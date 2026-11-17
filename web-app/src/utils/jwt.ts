/** Các claim identity-service ghi vào access token */
export interface AccessTokenClaims {
  sub: string
  userId: string
  scope: string
  iss: string
  jti: string
  iat: number
  exp: number
}

/**
 * Đọc phần payload của JWT để hiển thị (username, role, hạn dùng).
 * Chỉ giải mã, KHÔNG kiểm tra chữ ký - việc xác thực luôn do backend làm.
 */
export function decodeJwt(token: string): AccessTokenClaims | null {
  try {
    const payload = token.split('.')[1]
    if (!payload) return null
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/')
    const json = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join(''),
    )
    return JSON.parse(json) as AccessTokenClaims
  } catch {
    return null
  }
}
