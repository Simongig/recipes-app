import { defineStore } from 'pinia'

const useStore = defineStore('main', {
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
    },
  },
})

export default useStore
