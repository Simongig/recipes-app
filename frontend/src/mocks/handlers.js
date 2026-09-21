import { http, HttpResponse } from 'msw'

// Central place for the fake backend responses used in tests (and, if you wire
// up src/mocks/browser.js, in the dev server too). Add one handler per
// endpoint you need to control here, and override individual handlers per
// test with `server.use(...)` when a specific test needs different behavior.
export const handlers = [
  http.get('/api/v1/ingredient/units', () => {
    return HttpResponse.json([
      { value: 'g', label: 'Gramm' },
      { value: 'ml', label: 'Milliliter' },
      { value: 'stk', label: 'Stück' },
    ])
  }),

  http.post('/api/v1/recipe/add', () => {
    return HttpResponse.json({ id: 'mock-recipe-id' }, { status: 200 })
  }),
]
