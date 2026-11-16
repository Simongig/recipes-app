import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { http, HttpResponse } from 'msw'
import { server } from '@/mocks/server'
import api from '@/services/api'
import { useAuthStore } from '@/stores/authStore'

// Fake backend: like Spring's resource server, it answers 401 to a bad token even on the
// public endpoint, and only accepts the token "fresh".
function backend({ refreshWorks }) {
  const seen = []
  const guard = (request, isPublic) => {
    const auth = request.headers.get('authorization')
    seen.push(auth)
    if (auth === 'Bearer fresh') return HttpResponse.json({ as: 'user' })
    if (!auth && isPublic) return HttpResponse.json({ as: 'guest' })
    return new HttpResponse(null, { status: 401 })
  }
  server.use(
    http.get('/api/v1/recipe/all', ({ request }) => guard(request, true)),
    http.get('/api/v1/user/me', ({ request }) => guard(request, false)),
    http.post('/api/v1/auth/token/refresh', () =>
      refreshWorks
        ? HttpResponse.json({ access_token: 'fresh', refresh_token: 'fresh-refresh' })
        : new HttpResponse(null, { status: 403 }),
    ),
  )
  return seen
}

describe('api 401 handling', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    useAuthStore().setToLoggedIn()
    localStorage.setItem('access_token', 'expired')
    localStorage.setItem('refresh_token', 'old-refresh')
  })

  it('refreshes an expired access token and replays the request', async () => {
    const seen = backend({ refreshWorks: true })

    const { data } = await api.get('/api/v1/recipe/all')

    expect(data).toEqual({ as: 'user' })
    expect(seen).toEqual(['Bearer expired', 'Bearer fresh'])
    expect(localStorage.getItem('access_token')).toBe('fresh')
    expect(useAuthStore().isLoggedIn).toBe(true)
  })

  it('falls back to a guest request on a public endpoint when the session is over', async () => {
    const seen = backend({ refreshWorks: false })

    const { data } = await api.get('/api/v1/recipe/all')

    expect(data).toEqual({ as: 'guest' })
    expect(seen).toEqual(['Bearer expired', null])
    expect(localStorage.getItem('access_token')).toBeNull()
    expect(localStorage.getItem('refresh_token')).toBeNull()
    expect(useAuthStore().isLoggedIn).toBe(false)
  })

  it('rejects (and logs out) when a protected endpoint also refuses the guest request', async () => {
    const seen = backend({ refreshWorks: false })

    await expect(api.get('/api/v1/user/me')).rejects.toMatchObject({ response: { status: 401 } })

    expect(seen).toEqual(['Bearer expired', null]) // no further retries
    expect(useAuthStore().isLoggedIn).toBe(false)
  })

  it('sends no token for guests', async () => {
    localStorage.clear()
    const seen = backend({ refreshWorks: false })

    const { data } = await api.get('/api/v1/recipe/all')

    expect(data).toEqual({ as: 'guest' })
    expect(seen).toEqual([null])
  })
})
