<template>
  <main class="index-page">
    <section class="search-wrapper">
      <h1>Was möchtest du heute kochen?</h1>
      <div class="search-filters">
        <div class="search-switch"> 
          <div class="switch-option-wrapper" :class="[searchType === 'ingredients' ? 'active' : '']">
            <label for="search-type">Zutaten</label>
            <input type="radio" v-model="searchType" value="ingredients" name="search-type" id="">
          </div>
          <div class="switch-option-wrapper" :class="[searchType === 'recipes' ? 'active' : '']" >
            <input type="radio" checked v-model="searchType" value="recipes" name="search-type" id="">
            <label for="search-type">Rezepte</label>
          </div>
        </div>
        <ul v-if="searchType === 'ingredients'" class="selected-ingredients-list">
          <search-chip
            v-for="(ingredient, index) in selectedIngredients"
            :key="index"
            :id="'chip-' + index"
            @click="(index) => selectedIngredients.splice(index, 1)"
            :value="ingredient"
          />
        </ul>
      </div>
      <search-input-ingredients v-if="searchType  === 'ingredients'"
        @ingredientSelected="
          (text) => {
            selectedIngredients.push(text);
          }
        "
      />
      <search-input-recipes v-else ></search-input-recipes>
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
import RecipeCard from "../components/RecipeCard-v2.vue";
import axios from "axios";
import SearchChip from "../components/SearchChip.vue";
import { register } from "swiper/element/swiper-element.js";
register();
// import Categories from "../components/Categories.vue";

import "swiper/swiper.css";
import SearchInputRecipes from '../components/SearchInputRecipes.vue';

export default {
  components: {
    SearchInputIngredients,
    RecipeCard,
    SearchChip,
    SearchInputRecipes,
  },
  name: "Index",
  data() {
    return {
      selectedIngredients: [],
      foundRecipes: [],
      suggestedRecipes: [],
      searchType: null,
      swiper: null,
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
    },
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

.selected-ingredient {
  list-style: none;
  display: inline-block;
}

.search-filters {
  margin-bottom: 1rem;
}

.search-switch {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background-color: var(--light-grey);
}

.switch-option-wrapper {
  padding: 0.5rem 0.75rem;
}

.search-switch,
.switch-option-wrapper.active {
  border-radius: 2rem;
}

.switch-option-wrapper.active {
  background-color: var(--secondary-color);
}

.switch-option-wrapper > *:nth-child(2) {
  margin-left: 0.5rem;
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