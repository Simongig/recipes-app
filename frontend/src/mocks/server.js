import { setupServer } from 'msw/node'
import { handlers } from './handlers'

// Used by src/test/setup.js to intercept every request the tests fire —
// nothing reaches the real backend (or the dev database) while tests run.
export const server = setupServer(...handlers)
