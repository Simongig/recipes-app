<template>
  <main class="index-page">
    <section class="home-search-wrapper">
      <h1>Was möchtest du heute kochen?</h1>
      <div class="search-type-toggle">
        Suche nach
        <button class="search-type-button" @click="search_type = 'recipes'">
          <label
            :class="search_type == 'recipes' ? 'selected' : ''"
            class="search-type-toggle-item cursor-pointer"
            for="recipes"
            >Rezepten</label
          >
          <input v-model="search_type" name="search-type" value="recipes" type="radio" />
        </button>
        oder
        <button disabled
          class="search-type-button search-type-ingredients"
          title="Suche nach Zutaten ist noch nicht verfügbar"
          @click="search_type = 'ingredients'"
        >
          <label
            :class="search_type == 'ingredients' ? 'selected' : ''"
            class="search-type-toggle-item cursor-not-allowed text-gray-400"
            for="ingredients"
            >Zutaten</label
          >
          <input
            checked
            v-model="search_type"
            value="ingredients"
            name="search-type"
            type="radio"
          />
        </button>
      </div>
      <search-input-ingredients v-if="search_type === 'ingredients'"></search-input-ingredients>
      <search-input-recipes v-else></search-input-recipes>
    </section>
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
import SearchInputIngredients from '../components/SearchInputIngredients.vue'
import SearchInputRecipes from '../components/SearchInputRecipes.vue'
import RecipeCard from '../components/RecipeCard-v2.vue'
import { useRecipeStore } from '@/stores/recipeStore'
import { register } from 'swiper/element/bundle'

register()
// import Categories from "../components/Categories.vue";


import 'swiper/swiper.css'

export default {
  components: {
    RecipeCard,
    SearchInputIngredients,
    SearchInputRecipes,
  },
  name: 'Index',
  setup() {
    const recipeStore = useRecipeStore()
    return { recipeStore }
  },
  computed: {
    suggestedRecipes() {
      return this.recipeStore.recipes.slice(-5)
    },
  },
  data() {
    return {
      searchType: null,
      swiper: null,
      search_type: 'recipes',
    }
  },
  methods: {
  },
  mounted() {
    this.recipeStore.fetchRecipes()
  },
}
</script>

<style scoped>
.index-page > section + section {
  margin-top: 4rem;
}

.swiper {
  width: 100%;
}

.home-search-wrapper {
  width: 100%;
  max-width: 584px;
  min-height: 60vh;
}

.home-search-wrapper h1 {
  font-size: 3rem;
}

h1 {
  margin-bottom: 2rem;
}

h2 {
  margin-bottom: 1rem;
}

.search-type-ingredients {
  position: relative;
}

.search-type-toggle {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.search-type-toggle-item {
  line-height: 1.6;
  text-underline-offset: 0.6ch;
  text-decoration: underline dotted;
}

.search-type-toggle-item.selected {
  text-decoration: underline;
  text-decoration-color: var(--secondary-color);
  font-weight: bold;
}

.search-type-toggle input {
  display: none;
}

a:not(nav a) {
  color: inherit;
  text-decoration: none;
}

@media (max-width: 768px) {
  .search-wrapper h1 {
    font-size: 2.2rem;
    line-height: 3rem;
  }
}
</style>
