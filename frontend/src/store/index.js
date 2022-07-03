import { createStore } from 'vuex'

// Create a new store instance.
const store = createStore({
  state () {
    return {
      count: 0,
      isLoggedIn: false
    }
  },
  mutations: {
    increment (state) {
      state.count++
    },
    setToLoggedIn(state) {
        state.isLoggedIn = true;
    },
    setToLoggedOut(state) {
        state.isLoggedIn = false;
    }
  }
})

export default store;
