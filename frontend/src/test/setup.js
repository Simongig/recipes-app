import { afterAll, afterEach, beforeAll } from 'vitest'
import { server } from '@/mocks/server'

// Fail loudly on any request the handlers don't cover, so a forgotten mock
// shows up as a test failure instead of a silent real network call.
beforeAll(() => server.listen({ onUnhandledRequest: 'error' }))
afterEach(() => server.resetHandlers())
afterAll(() => server.close())
