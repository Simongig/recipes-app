<template>
  <main class="recipes-list-page">
    <h1>Alle Rezepte</h1>
    <card-grid>
      <recipe-card v-for="element in allRecipes" :key="element.id" :recipe="element" />
    </card-grid>
    <div v-if="0 == allRecipes.length">No elements found</div>
  </main>
</template>

<script>
import RecipeCard from '../components/RecipeCard-v2.vue'
import CardGrid from '../components/CardGrid.vue'
import { useRecipeStore } from '@/stores/recipeStore'

export default {
  name: 'RecipeListPage',
  components: { RecipeCard, CardGrid },
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

<style>
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  grid-auto-rows: 460px;
  gap: 10px;
}

.card-wrapper {
  max-width: 460px;
  min-width: 230px;
  height: 460px;
  width: 100%;
  display: inline-block;
}
</style>
