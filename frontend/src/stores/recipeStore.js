import { defineStore } from 'pinia'
import axios from 'axios'

export const useRecipeStore = defineStore('recipeStore', {
    state: () => ({
        recipes: [],
        recipesLoaded: false
    }),
    actions: {
        async fetchRecipes() {
            if (this.recipesLoaded) return
            const { data } = await axios.get('/api/v1/recipe/all')
            this.recipes = data
            this.recipesLoaded = true
        }
    },
})
