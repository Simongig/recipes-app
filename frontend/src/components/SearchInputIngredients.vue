<template>
  <section class="search">
    <form id="query-form" role="search">
      <div class="search-input-wrapper">
        <input
          type="search"
          id="search-input"
          name="q"
          placeholder="Suche nach Zutaten"
          :value="searchInput"
          aria-label="Search through site content"
          @input="(event) => (searchInput = event.target.value)"
        />
        <ion-icon name="search-outline" class="search-icon hydrated"></ion-icon>
      </div>
      <div class="search-result-wrapper">
        <ol class="results" v-if="searchResults.length > 0">
          <li
            @click="
              (event) =>
                $emit(
                  'ingredientSelected',
                  event.target.attributes['ingredient-name'].value
                )
            "
            v-for="result in searchResults"
            :key="result.name"
            :ingredient-name="result.name"
          >
            {{ result.name }}
          </li>
        </ol>
        <ul class="no-results" v-else>
          <li>keine Ergebnisse gefunden</li>
        </ul>
      </div>
    </form>
  </section>
</template>

<script>
const axios = require("axios");

export default {
  name: "searchInput",
  data() {
    return {
      elements: new Array(),
      searchInput: "",
      searchResults: [],
    };
  },
  methods: {
    sendSearchQuery() {
      axios.get("/api/v1/recipe/all").then((response) => {
        console.log(response);
      });
    },
  },
  mounted() {
    document.querySelector("#query-form").addEventListener("submit", (e) => {
      e.preventDefault();
    });
  },
  watch: {
    searchInput() {
      console.log(this.searchInput);
      if (this.searchInput.length != 0) {
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
      } else {
        this.searchResults = [];
      }
    },
  },
};
</script>

<style>
.search {
  display: flex;
  justify-content: center;
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

.search button {
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
  margin: 0.5rem 1.5rem;
}

#query-form:focus-within .search-result-wrapper,
.search-result-wrapper:hover {
  height: auto;
  max-height: 200px;
}

.search-input-wrapper {
  width: 100%;
  height: 44px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.search-input-wrapper:focus-within {
  border-bottom: 1px solid rgba(142, 142, 142, 0.674);
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
</style>