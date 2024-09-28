<template>
  <section class="search">
    <div class="search-wrapper">
      <div class="filter-container" v-if="selectedIngredients.size > 0">
        <ul class="selected-ingredients-list">
          <search-chip
            v-for="ingredient in selectedIngredients"
            :key="ingredient"
            @click="() => selectedIngredients.delete(ingredient)"
            :value="ingredient"
          />
        </ul>
      </div>
      <form id="query-form" role="search">
        <div class="search-input-wrapper">
          <input
            type="search"
            id="search-input"
            name="q"
            placeholder="z.B. Spaghetti, Tomaten, Käse etc."
            :value="searchInput"
            aria-label="Search through site content"
            @input="(event) => (searchInput = event.target.value)"
          />
          <ion-icon name="search-outline" class="search-icon hydrated"></ion-icon>
        </div>
        <div v-if="0 < searchResults.length" class="search-result-wrapper">
          <ol class="results">
            <li
              @click="addIngredient"
              v-for="result in searchResults"
              :key="result.name"
              :ingredient-name="result.name"
            >
              {{ result.name }}
            </li>
          </ol>
        </div>
      </form>
    </div>
    <div v-if="0 < foundRecipes.length" class="search-results-wrapper">
      <h2>Suchergebnisse:</h2>
      <div class="card-grid">
        <recipe-card
          v-for="element in foundRecipes"
          :key="element"
          :recipe="element"
        />
      </div>
    </div>
  </section>
</template>

<script>
import SearchChip from './SearchChip.vue';
import RecipeCard from './RecipeCard-v2.vue';
const axios = require("axios");

export default {
  components: { SearchChip, RecipeCard },
  name: "searchInputIngredients",
  data() {
    return {
      elements: [],
      selectedIngredients: new Set(),
      searchInput: "",
      searchResults: [],
      foundRecipes: [],
    };
  },
  methods: {
    sendSearchQuery() {
      axios.get("/api/v1/recipe/all").then((response) => {
        console.log(response);
      });
    },
    findRecipes() {
      let selectedIngredientsArr = Array.from(this.selectedIngredients);
      const jsonString = JSON.stringify(selectedIngredientsArr);
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
    addIngredient(event) {
      this.selectedIngredients.add(event.target.getAttribute('ingredient-name'));
      let search_input = document.querySelector('#search-input');
      this.searchInput = "";
      search_input.focus();
    }
  },
  mounted() {
    document.querySelector("#query-form").addEventListener("submit", (e) => {
      e.preventDefault();
    });
  },
  watch: {
    searchInput() {
      console.log("Current Search Input:", this.searchInput);
      if (this.searchInput.length == 0) {
        this.searchResults = [];
        return;
      }
      axios.get("api/v1/ingredient/all").then((results) => {
        console.log(results.data);
        this.searchResults = results.data.filter((ingredient) => {
          return (
            -1 <
            ingredient.name
              .toLowerCase()
              .indexOf(this.searchInput.toLowerCase())
          );
        });
      });
    },
    selectedIngredients: {
      handler() {
        if (this.selectedIngredients.size != 0) {
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

<style scoped>
.search, .search-wrapper {
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
  gap: 1rem;
}

.search-wrapper {
  min-height: 200px;
}

.search form {
  width: 100%;
  border-radius: 22px;
  box-shadow: 0px 0px 6px 0px rgba(116, 116, 116, 0.5);
  -webkit-box-shadow: 0px 0px 6px 0px rgba(116, 116, 116, 0.5);
  -moz-box-shadow: 0px 0px 6px 0px rgba(116, 116, 116, 0.5);
}

.search-button-container {
  height: 20px;
  width: 20px;
  margin-right: 10px;
}

.search-input-wrapper button {
  all: unset;
  cursor: pointer;
  display: block;
  width: 100%;
  height: 100%;
  margin-right: 10px;
}

.search svg {
  box-sizing: border-box;
  color: #fff;
  margin: 5px;
}

ol.results > li {
  cursor: pointer;
  padding: 0.25rem 0;
}

ol.results > li:hover {
  text-decoration: underline;
}

::placeholder {
  opacity: 0.4;
}

#query-form:focus-within {
  outline: 2px solid rgba(142, 142, 142, 0.142);
}

.search-result-wrapper {
  width: 100%;
  height: 0;
  overflow: hidden;
  transition: height 0.3s ease-out;
}

.search-result-wrapper ul,
.search-result-wrapper ol {
  list-style: none;
  padding: 0.5rem 1.5rem;
  border-top: 1px solid rgba(142, 142, 142, 0.674);
}

#query-form:focus-within .search-result-wrapper,
.search-result-wrapper:hover {
  height: 200px;
}

.search-input-wrapper {
  width: 100%;
  height: 44px;
  display: flex;
  justify-content: center;
  align-items: center;
}

input#search-input:focus {
  outline: none;
}

.search ion-icon.search-icon {
	padding: 0 1rem;
  cursor: pointer;
}

.search input {
  height: 100%;
  width: inherit;
  border: none;
  margin: 0 1rem;
  font-size: 1.2rem;
  text-overflow: ellipsis;
}
.search input[type="search"] {
  background-color: transparent !important;
}

.search input[type="search"]::-moz-search-decoration {
  display: none;
}
.search input[type="search"]::-webkit-search-decoration {
  display: none;
}

.filter-container {
  display: flex;
  gap: 1rem;
  justify-content: space-between;
  align-items: flex-end;
}

.selected-ingredients-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>