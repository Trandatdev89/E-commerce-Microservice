/** Cấu trúc response chung của mọi service (ResponseData trong common-core) */
export interface ResponseData<T> {
  status: number
  error: string | null
  path: string | null
  processDate: string
  transactionId: string | null
  clientMessageId: string | null
  soaErrorCode: string | null
  soaErrorDesc: string | null
  data: T
}

export interface SubError {
  field: string
  rejectedValue: unknown
  message: string
}

export interface RegisterPayload {
  username: string
  password: string
  firstName: string
  lastName: string
  email: string
  phone?: string
  address?: string
}

export interface RegisterResult {
  userId: string
  email: string
}

export interface AuthTokens {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  authenticated: boolean
}

export interface Profile {
  id: string
  userid: string
  firstName: string
  lastName: string
  email: string
  phone: string | null
  address: string | null
}

export type NotificationType = 'VERIFY_ACCOUNT' | 'ORDER_CONFIRMATION' | 'PAYMENT_CONFIRMATION' | 'NEW_DEVICE_LOGIN'
export type NotificationStatus = 'PENDING' | 'SENT' | 'FAILED'

export interface AppNotification {
  id: string
  type: NotificationType
  channel: string
  subject: string
  referenceId: string | null
  metadata: Record<string, unknown> | null
  status: NotificationStatus
  read: boolean
  createdAt: string
  sentAt: string | null
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

/** Một phiên đăng nhập (thiết bị) đang hoạt động - GET /identity/sessions */
export interface LoginSession {
  sessionId: number
  browser: string
  os: string
  device: string
  ipAddress: string | null
  lastActiveAt: string
  expiresAt: string
  current: boolean
}
