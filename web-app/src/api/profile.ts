import { request } from './http'
import type { Profile } from './types'

export const profileApi = {
  getByUserId: (userId: string) =>
    request<Profile>({ method: 'GET', url: `/profile/external/private/${userId}` }),
}
