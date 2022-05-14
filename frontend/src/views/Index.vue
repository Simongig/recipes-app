<template>
  <main class="index-page">
    <div class="search-wrapper">
      <ul class="selected-ingredients-list">
        <li
          class="selected-ingredient chip"
          v-for="ingredient in selectedIngredients"
          :key="ingredient"
        >
          {{ ingredient }}
        </li>
      </ul>
      <search-input
        @ingredientSelected="
          (text) => {
            selectedIngredients.push(text);
          }
        "
      />
    </div>
    <!-- <article v-if="0 < elements.length" class="card-grid">
      <recipe-card
        v-for="element in elements"
        :key="element.id"
        :recipe="element"
      />
    </article>
    <div v-else>No elements found</div> -->
  </main>
</template>

<script>
import SearchInput from "../components/SearchInput.vue";
// import RecipeCard from "../components/RecipeCard-v2.vue";
const axios = require("axios");

export default {
  components: { SearchInput },
  // components: { RecipeCard },
  name: "Index",
  data() {
    return {
      elements: new Array(),
      selectedIngredients: [],
    };
  },
  mounted() {
    axios.get("/api/v1/recipe/all").then((response) => {
      console.log(response.data);
      this.elements = response.data;
    });
  },
};
</script>

<style>
* {
  padding: 0;
  margin: 0;
  box-sizing: border-box;
}

main {
  width: auto;
  height: 100%;
  margin: 0 10vw;
}

.search-wrapper {
  width: 100%;
  max-width: 584px;
  margin: auto;
}

h1 {
  margin-bottom: 2rem;
}

.selected-ingredient {
  list-style: none;
  display: inline-block;
}

.selected-ingredients-list {
  margin-bottom: 1rem;
}

.chip {
  border: 1px solid rgb(225, 225, 225);
  padding: 0.3rem 0.7rem;
  border-radius: 20px;
  background-color: rgb(225, 225, 225);
}

.chip + .chip {
  margin-left: 10px;
}

a:not(nav a) {
  color: inherit;
  text-decoration: none;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
  grid-auto-rows: 460px;
  gap: 10px;
}
@media (max-width: 768px) {
}
</style>