import { request } from './http'
import type { LoginSession } from './types'

const BASE = '/identity/sessions'

export const sessionApi = {
  list: () => request<LoginSession[]>({ method: 'GET', url: BASE }),

  revoke: (sessionId: number) => request<void>({ method: 'DELETE', url: `${BASE}/${sessionId}` }),
}
