<template>
  <main class="recipes-list-page">
    <h1>Alle Rezepte</h1>
    <article v-if="0 < elements.length" class="card-grid">
      <recipe-card v-for="element in elements" :key="element.id" :recipe="element" />
    </article>
    <div v-else>No elements found</div>
  </main>
</template>

<script>
import RecipeCard from '../components/RecipeCard-v2.vue'
import axios from 'axios'
export default {
  name: 'RecipeListPage',
  components: { RecipeCard },
  data() {
    return {
      elements: new Array(),
    }
  },
  mounted() {
    axios.get('/api/v1/recipe/all').then((response) => {
      console.log(response.data)
      this.elements = response.data
    })
  },
}
</script>

<style></style>
