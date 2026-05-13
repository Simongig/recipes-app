<template>
  <main class="recipes-list-page">
    <h1>Alle Rezepte</h1>
    <article v-if="0 < allRecipes.length" class="card-grid">
      <recipe-card v-for="element in allRecipes" :key="element.id" :recipe="element" />
    </article>
    <div v-else>No elements found</div>
  </main>
</template>

<script>
import RecipeCard from '../components/RecipeCard-v2.vue'
import { useRecipeStore } from '@/stores/recipeStore'

export default {
  name: 'RecipeListPage',
  components: { RecipeCard },
  setup() {
    const recipeStore = useRecipeStore()
    return { recipeStore }
  },
  computed: {
    allRecipes() { return this.recipeStore.recipes }
  },
  mounted() {
    this.recipeStore.fetchRecipes()
  },
}
</script>

<style></style>
