import { defineStore } from 'pinia'
import api from '@/services/api'
import { useAuthStore } from '@/stores/authStore'

export const useRecipeStore = defineStore('recipeStore', {
    state: () => ({
        recipes: [],
        recipesLoaded: false,
        // Login state the list was loaded with: the backend also returns the caller's own
        // private recipes, so the cached list is only valid for that state.
        loadedAsLoggedIn: false
    }),
    actions: {
        async fetchRecipes() {
            const isLoggedIn = useAuthStore().isLoggedIn
            if (this.recipesLoaded && this.loadedAsLoggedIn === isLoggedIn) return
            const { data } = await api.get('/api/v1/recipe/all')
            this.recipes = data
            this.recipesLoaded = true
            this.loadedAsLoggedIn = isLoggedIn
        }
    },
})
