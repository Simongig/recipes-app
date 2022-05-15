<template>
  <main class="index-page">
    <div class="search-wrapper">
      <ul class="selected-ingredients-list">
        <!-- <li
          class="selected-ingredient chip"

        >
          {{ ingredient }}
        </li> -->
        <search-chip v-for="(ingredient, index) in selectedIngredients" :key="index" :id="'chip-' + index" @click="(index) => selectedIngredients.splice(index,1)" :value="ingredient"/>
      </ul>
      <search-input
        @ingredientSelected="
          (text) => {
            selectedIngredients.push(text);
          }
        "
      />
    </div>
    <div v-if="0 < foundRecipes.length" class="card-grid">
      <recipe-card
        v-for="element in foundRecipes"
        :key="element"
        :recipe="element"
      />
    </div>
    <div v-else>no recipes Found</div>
  </main>
</template>

<script>
import SearchInput from "../components/SearchInput.vue";
import RecipeCard from "../components/RecipeCard-v2.vue";
import axios from "axios";
import SearchChip from '../components/SearchChip.vue';

export default {
  components: { SearchInput, RecipeCard, SearchChip },
  name: "Index",
  data() {
    return {
      selectedIngredients: [],
      foundRecipes: [],
    };
  },
  methods: {
    findRecipes() {
      const jsonString = JSON.stringify(this.selectedIngredients);
      console.log(jsonString);
      const data = new Blob([jsonString], { type: "application/json" });
      console.log(data);
      axios
        .post("/api/v1/recipe/find", data, {
          headers: {
            "Content-Type": "application/json",
          },
        })
        .then((response) => {
          console.log(this.foundRecipes)
          console.log(response.data)
          this.foundRecipes = this.foundRecipes.concat( response.data);
          console.log(this.foundRecipes)
        });
    },
  },
  watch: {
    selectedIngredients: {
      handler() {
        if(this.selectedIngredients != 0) {
          this.findRecipes();
        } else {
          this.foundRecipes.length = 0;
        }
        }, deep: true
      },
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
  min-height: 300px;
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