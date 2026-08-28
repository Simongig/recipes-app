import { defineStore } from 'pinia'
import api from '@/services/api'

export const useUserStore = defineStore('userStore', {
    state: () => ({
        user: null as any | null,
    }),
    actions: {
        async fetchUser() {
            const { data } = await api.get('/api/v1/user/me')
            this.user = data
        },
        setUser(user: any) {
            this.user = user
        }
    },
})
