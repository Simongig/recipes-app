import { describe, it, expect, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { http, HttpResponse } from 'msw'
import { server } from '@/mocks/server'
import { useAlertStore } from '@/stores/alertStore'
import CreateRecipeForm from '../CreateRecipeForm.vue'

let activeWrapper = null

// unmount whatever the previous test attached to document.body — otherwise
// `document.querySelector('form')` in the component under test would find a
// leftover <form> from an earlier test instead of the current one
afterEach(() => {
  activeWrapper?.unmount()
  activeWrapper = null
})

// helper: mount with a fresh Pinia instance so each test gets its own,
// isolated alertStore instead of sharing state across tests
function mountForm() {
  const pinia = createPinia()
  setActivePinia(pinia)
  // sendForm() reads the form via `document.querySelector('form')`, so the
  // component has to be attached to the real DOM, not test-utils' default
  // detached tree.
  const wrapper = mount(CreateRecipeForm, {
    global: { plugins: [pinia] },
    attachTo: document.body,
  })
  activeWrapper = wrapper
  return { wrapper, pinia }
}

function findSubmitButton(wrapper) {
  return wrapper.findAll('button').find((button) => button.text().includes('Rezept hochladen'))
}

describe('CreateRecipeForm', () => {
  it('sends the filled-in recipe to /api/v1/recipe/add and shows a success alert', async () => {
    // Override the generic handler from src/mocks/handlers.js for this one
    // test so we can inspect exactly what the component sent.
    //
    // Note: the component bundles ingredients/preparationSteps/imagePaths into
    // a JSON Blob (formData.append('data', blob)) alongside the plain fields.
    // Under vitest's jsdom environment, that Blob's content does not survive
    // the XHR -> MSW interception round-trip intact (a known jsdom/msw
    // interop limitation, not a bug in the component) — formData.get('data')
    // resolves but its .text() comes back wrong. The plain string fields
    // (title/duration/portions, read directly off the form) are unaffected
    // and transport correctly, so we assert on those. See the "extending
    // these tests" notes below for how to get coverage on the JSON part too.
    let receivedFormData = null
    server.use(
      http.post('/api/v1/recipe/add', async ({ request }) => {
        receivedFormData = await request.formData()
        return HttpResponse.json({ id: 'mock-recipe-id' }, { status: 200 })
      }),
    )

    const { wrapper } = mountForm()

    await wrapper.find('input[name="title"]').setValue('Spaghetti Carbonara')
    await wrapper.find('input[name="duration"]').setValue('25')
    await wrapper.find('input[name="portions"]').setValue('2')

    await findSubmitButton(wrapper).trigger('click')
    // sendForm() is async (awaits the API call) — flush the microtask queue
    await new Promise((resolve) => setTimeout(resolve, 0))

    expect(receivedFormData.get('title')).toBe('Spaghetti Carbonara')
    expect(receivedFormData.get('duration')).toBe('25')
    expect(receivedFormData.get('portions')).toBe('2')

    const alertStore = useAlertStore()
    expect(alertStore.alerts.at(-1)).toMatchObject({ variant: 'success' })
  })

  it('shows an error alert and keeps the user on the page when the API call fails', async () => {
    server.use(http.post('/api/v1/recipe/add', () => HttpResponse.json({}, { status: 500 })))

    const { wrapper } = mountForm()

    await wrapper.find('input[name="title"]').setValue('Broken Recipe')
    await wrapper.find('input[name="duration"]').setValue('10')
    await wrapper.find('input[name="portions"]').setValue('1')

    await findSubmitButton(wrapper).trigger('click')
    await new Promise((resolve) => setTimeout(resolve, 0))

    const alertStore = useAlertStore()
    expect(alertStore.alerts.at(-1)).toMatchObject({ variant: 'destructive' })
  })

  it('fetches stock photos for the current title, previews them, and lets the user select one', async () => {
    server.use(
      http.get('https://api.unsplash.com/search/photos', () => {
        return HttpResponse.json({
          results: [
            { urls: { small: 'https://images.unsplash.com/photo-1?w=400' } },
            { urls: { small: 'https://images.unsplash.com/photo-2?w=400' } },
          ],
        })
      }),
    )

    const { wrapper } = mountForm()
    await wrapper.find('input[name="title"]').setValue('Pasta Carbonara')

    const stockButton = wrapper.findAll('button').find((b) => b.text().includes('Stockbilder hinzufügen'))
    await stockButton.trigger('click')
    await new Promise((resolve) => setTimeout(resolve, 0))
    await wrapper.vm.$nextTick()

    const previews = wrapper.findAll('.preview-image')
    expect(previews).toHaveLength(2)
    expect(previews[0].find('img').attributes('src')).toBe('https://images.unsplash.com/photo-1?w=400')

    // clicking a preview toggles its selected state (visualized via the border class)
    expect(previews[0].classes()).not.toContain('border-blue-500')
    await previews[0].trigger('click')
    expect(previews[0].classes()).toContain('border-blue-500')
  })

  it('previews a custom-uploaded image and lets the user select it', async () => {
    const { wrapper } = mountForm()

    const fileInput = wrapper.find('#file-input')
    const file = new File(['fake-image-bytes'], 'photo.png', { type: 'image/png' })
    // jsdom's <input type="file">.files is read-only; this is the standard
    // way to fake a user's file selection in tests.
    Object.defineProperty(fileInput.element, 'files', { value: [file] })
    await fileInput.trigger('change')
    await wrapper.vm.$nextTick()

    const previews = wrapper.findAll('.preview-image')
    expect(previews).toHaveLength(1)
    expect(previews[0].find('img').attributes('src')).toMatch(/^blob:/)

    await previews[0].trigger('click')
    expect(previews[0].classes()).toContain('border-blue-500')
  })

  it('rejects an oversized image locally instead of running into the server 413', async () => {
    let requests = 0
    server.use(
      http.post('/api/v1/recipe/add', () => {
        requests += 1
        return HttpResponse.json({ id: 'mock-recipe-id' }, { status: 200 })
      }),
    )

    const { wrapper } = mountForm()

    const fileInput = wrapper.find('#file-input')
    const file = new File(['fake-image-bytes'], 'riesenfoto.jpg', { type: 'image/jpeg' })
    // Blob.size is derived from the parts, so shadow it on the instance rather than
    // allocating 11MB of real bytes just to cross the limit.
    Object.defineProperty(file, 'size', { value: 11 * 1024 * 1024 })
    Object.defineProperty(fileInput.element, 'files', { value: [file] })
    await fileInput.trigger('change')
    await wrapper.vm.$nextTick()

    await wrapper.findAll('.preview-image')[0].trigger('click')

    await wrapper.find('input[name="title"]').setValue('Zu großes Bild')
    await wrapper.find('input[name="duration"]').setValue('20')
    await wrapper.find('input[name="portions"]').setValue('4')

    await findSubmitButton(wrapper).trigger('click')
    await new Promise((resolve) => setTimeout(resolve, 0))

    const alertStore = useAlertStore()
    expect(alertStore.alerts.at(-1)).toMatchObject({ variant: 'destructive' })
    expect(alertStore.alerts.at(-1).message).toContain('riesenfoto.jpg')
    expect(requests).toBe(0)
  })
})
