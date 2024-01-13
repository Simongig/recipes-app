<template>
  <main class="index-page">
    <section class="search-wrapper">
      <h1>Was möchtest du heute kochen?</h1>
      <div class="filter-container">
        <ul class="selected-ingredients-list">
          <search-chip
            v-for="(ingredient, index) in selectedIngredients"
            :key="index"
            :id="'chip-' + index"
            @click="(index) => selectedIngredients.splice(index, 1)"
            :value="ingredient"
          />
        </ul>
        <div class="search-type-toggle">
          <span v-if="search_type==='ingredients'">Nach Zutaten suchen</span><span v-else>Nach Rezepten suchen</span><input checked v-model="search_type" value="ingredients" name="search-type" type="radio"><input v-model="search_type" name="search-type" value="recipes" type="radio">
        </div>
      </div>
      <search-input-ingredients v-if="search_type === 'ingredients'" @ingredientSelected="(text) => {selectedIngredients.push(text);}"></search-input-ingredients>
      <search-input-recipes v-else></search-input-recipes>
    </section>
    <section v-if="0 < foundRecipes.length" class="search-results-wrapper">
      <h2>Suchergebnisse:</h2>
      <div class="card-grid">
        <recipe-card
          v-for="element in foundRecipes"
          :key="element"
          :recipe="element"
        />
      </div>
    </section>
    <categories></categories>
    <section class="suggestions-slider">
      <h2>Neue Rezepte</h2>
      <swiper-container
        class="swiper"
        :slides-per-view="1.2"
        :spaceBetween="20"
        :pagination="{
          hideOnClick: true,
        }"
        :breakpoints="{
          // when window width is >= 320px
          480: {
            slidesPerView: 1.3,
          },
          // when window width is >= 480px
          768: {
            slidesPerView: 2.3,
          },
          // when window width is >= 640px
          1200: {
            slidesPerView: 3.5,
          },
          1440: {
            slidesPerView: 5,
          },
        }"
        :navigation="{
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev',
        }"
        @progress="onProgress"
        @slidechange="onSlideChange"
      >
        <swiper-slide v-for="element in suggestedRecipes" :key="element">
          <recipe-card :recipe="element" />
        </swiper-slide>
      </swiper-container>
    </section>
  </main>
</template>

<script>
import SearchInputIngredients from "../components/SearchInputIngredients.vue";
import SearchInputRecipes from "../components/SearchInputRecipes.vue";
import RecipeCard from "../components/RecipeCard-v2.vue";
import axios from "axios";
import SearchChip from "../components/SearchChip.vue";
import { register } from "swiper/element/swiper-element.js";
register();
// import Categories from "../components/Categories.vue";

import "swiper/swiper.css";

export default {
  components: {
    RecipeCard,
    SearchChip,
    SearchInputIngredients,
    SearchInputRecipes
  },
  name: "Index",
  data() {
    return {
      selectedIngredients: [],
      foundRecipes: [],
      suggestedRecipes: [],
      swiper: null,
      search_type: 'ingredients',
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
          console.log(this.foundRecipes);
          console.log(response.data);
          this.foundRecipes = response.data;
          console.log(this.foundRecipes);
        });
    }
  },
  mounted() {
    axios.get("/api/v1/recipe/all").then((response) => {
      console.log(response.data);
      this.suggestedRecipes = response.data.reverse().slice(0, 5);
    });
    // this.swiper = new window.Swiper('.swiper')
  },
  watch: {
    selectedIngredients: {
      handler() {
        if (this.selectedIngredients != 0) {
          this.findRecipes();
        } else {
          this.foundRecipes.length = 0;
        }
      },
      deep: true,
    },
  },
};
</script>

<style>
.index-page > section + section {
  margin-top: 4rem;
}

.swiper {
  width: 100%;
}

.search-wrapper {
  width: 100%;
  max-width: 584px;
  min-height: 60vh;
}

.search-wrapper h1 {
  font-size: 3rem;
}

h1 {
  margin-bottom: 2rem;
}

h2 {
  margin-bottom: 1rem;
}

.filter-container {
  display: flex;
  gap: 1rem;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 1rem;
}

.search-type-toggle {
  display: flex;
  align-items: center;
  gap: 10px;
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
@media (max-width: 768px) {
  .search-wrapper h1 {
    font-size: 2.2rem;
    line-height: 3rem;
  }
}
</style>