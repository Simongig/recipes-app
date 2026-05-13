import { defineStore } from 'pinia'

export const useAuthStore = defineStore('authStore', {
  state() {
    return {
      isLoggedIn: false,
    }
  },
  actions: {
    setToLoggedIn() {
      this.isLoggedIn = true
    },
    setToLoggedOut() {
      this.isLoggedIn = false
    }
  },
})
