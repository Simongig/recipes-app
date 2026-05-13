<template>
  <section class="search">
    <div class="search-wrapper">
      <form id="query-form" role="search">
        <div class="search-input-wrapper">
          <input
            type="search"
            id="search-input"
            name="q"
            placeholder="z.B. Pfannkuchen, Lasagne etc."
            :value="searchInput"
            autocomplete="off"
            aria-label="Search through site content"
            @input="(event) => (searchInput = event.target.value)"
          />
          <ion-icon name="search-outline" class="search-icon hydrated"></ion-icon>
        </div>
        <div v-if="0 < searchResults.length" class="search-result-wrapper">
          <ol class="results">
            <li v-for="result in searchResults" :key="result.id">
              <router-link :to="{ name: 'recipePdp', params: { id: result.id } }">
                {{ result.title }}
              </router-link>
            </li>
          </ol>
        </div>
      </form>
    </div>
  </section>
</template>

<script>
import { watchDebounced } from '@vueuse/core'
import { ref } from 'vue';

export default {
  name: 'searchInputRecipes',
  setup() {
    const searchInput = ref('')
    const searchResults = ref([])

    watchDebounced(
      searchInput,
      () => {
        if (!searchInput.value) {
          searchResults.value = []
          return
        }
        fetch('/api/v1/recipe/search', {
          method: 'POST',
          body: searchInput.value.trim(),
        })
          .then((response) => response.json())
          .then((results) => {
            searchResults.value = results
          })
      },
      { debounce: 500, maxWait: 3000 }
    )

    return { searchInput, searchResults }
  },
  data() {
    return {
      elements: new Array(),
      foundRecipes: [],
    }
  },
  methods: {
  },
  mounted() {
    document.querySelector('#query-form').addEventListener('submit', (e) => {
      e.preventDefault()
    })
  }
}
</script>

<style scoped>
.search,
.search-wrapper {
  display: flex;
  justify-content: center;
  flex-direction: column;
  gap: 1rem;
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
.search input[type='search'] {
  background-color: transparent !important;
}

.search input[type='search']::-moz-search-decoration {
  display: none;
}
.search input[type='search']::-webkit-search-decoration {
  display: none;
}
</style>
