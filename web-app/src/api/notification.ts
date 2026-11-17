import { request } from './http'
import type { AppNotification, Page } from './types'

const BASE = '/notification/me'

export const notificationApi = {
  list: (page = 0, size = 20) =>
    request<Page<AppNotification>>({ method: 'GET', url: BASE, params: { page, size } }),

  unreadCount: () => request<number>({ method: 'GET', url: `${BASE}/unread-count` }),

  markAsRead: (id: string) => request<void>({ method: 'PUT', url: `${BASE}/${id}/read` }),
}
