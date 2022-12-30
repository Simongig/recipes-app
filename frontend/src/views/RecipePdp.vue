<template>
  <main class="recipe-pdp-wrapper" v-if="recipe != null">
    <h1 class="site-title">Rezept für {{ recipe.title }}</h1>
    <article class="recipe-pdp-content">
      <div class="left-column">
        <div class="img-wrapper">
          <img :src="recipe.imagePaths[0]" alt="" />
        </div>
        <section class="ingredients-wrapper">
          <h2>Zutaten</h2>
          <table>
            <tr v-for="ingredient in recipe.ingredients" :key="ingredient.name">
              <td>{{ ingredient.quantity }} {{ ingredient.unit }}</td>
              <td>{{ ingredient.name }}</td>
            </tr>
          </table>
        </section>
      </div>
      <div class="content-wrapper">
        <section
          class="preparationSteps-wrapper"
          v-if="recipe.preparationSteps"
        >
          <h2>Zubereitung</h2>
          <table>
            <div
              class="preparationStep"
              v-for="step in recipe.preparationSteps"
              :key="step.title"
              :id="'step-' + step.id"
            >
              <tr>
                <th>{{ step.title }}</th>
              </tr>
              <tr>
                <td>{{ step.content }}</td>
              </tr>
            </div>
          </table>
        </section>
      </div>
    </article>
      <section class="ingredients-wrapper-sticky dropdown-closed">
          <h2>Zutaten</h2>
          <table>
            <tr v-for="ingredient in recipe.ingredients" :key="ingredient.name">
              <td>{{ ingredient.quantity }} {{ ingredient.unit }}</td>
              <td>{{ ingredient.name }}</td>
            </tr>
          </table>
        </section>
  </main>
  <div v-else>Couldn't find this recipe</div>
</template>

<script>
const axios = require("axios");
export default {
  data() {
    return {
      recipe: null,
    };
  },
  methods: {
    toggleIngredientsDropDown() {
      document
        .querySelector(".ingredients-wrapper-sticky")
        .classList.toggle("dropdown-closed");
    },
    makeIngredientsSticky() {
      let ingredients_elem = document.querySelector(".ingredients-wrapper");
      let ingredients_elem_sticky = document.querySelector('.ingredients-wrapper-sticky');

      if(null == ingredients_elem || null == ingredients_elem_sticky) {
        return;
      }
      let scrolldepth_lower_than_element =
        window.scrollY >
        ingredients_elem.offsetTop + ingredients_elem.offsetHeight;
      console.log('scrolldepth_lower_than_element:', scrolldepth_lower_than_element);
      if (scrolldepth_lower_than_element) {
        ingredients_elem_sticky.classList.add("visible");
        ingredients_elem_sticky.classList.add("dropdown-closed");
        ingredients_elem_sticky
          .querySelector("h2")
          .addEventListener("click", this.toggleIngredientsDropDown);
      } else {
        ingredients_elem_sticky.classList.remove("visible");
      }
    },
  },
  mounted() {
    const urlPath = document.location.pathname;
    axios
      .get("/api/v1" + urlPath)
      .then((response) => {
        console.log("/api/v1" + urlPath);
        this.recipe = response.data;
      })
      .catch((reason) => {
        console.error(reason);
      });
    document.addEventListener("scroll", this.makeIngredientsSticky);
  },
  beforeUnmount() {
    document.removeEventListener("scroll", this.makeIngredientsSticky);
  }
};
</script>

<style>
.recipe-pdp-content {
  width: auto;
  height: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
  gap: 2rem;
}

.ingredients-wrapper table, .ingredients-wrapper-sticky table {
  width: 100%;
  display: grid;
  max-height: 50vh;
  overflow-y: scroll;
}

.ingredients-wrapper tr + tr,
.ingredients-wrapper-sticky tr + tr,
.preparationSteps-wrapper .preparationStep + .preparationStep {
  border-top: 1px solid #0000001c;
  border-collapse: collapse;
}

.preparationSteps-wrapper .preparationStep + .preparationStep {
  margin: 1rem 0;
}

.ingredients-wrapper tr,
.ingredients-wrapper-sticky tr,
.preparationSteps-wrapper .preparationStep {
  padding: 0.5rem;
}

.ingredients-wrapper tr td:first-child,
.ingredients-wrapper-sticky tr td:first-child {
  width: 150px;
  padding-left: 1rem;
}

.img-wrapper > img {
  aspect-ratio: 5/4;
  object-fit: cover;
  object-position: center;
}

.recipe-pdp-wrapper img {
  width: 100%;
}

.ingredients-wrapper-sticky {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 10px;
  box-shadow: 0px 0px 22px -2px #cecece;
  cursor: pointer;
}

.ingredients-wrapper-sticky.visible {
  display: block;
}

.ingredients-wrapper-sticky > h2 {
  margin-left: 2rem;
}
.ingredients-wrapper-sticky > h2::after {
  content: "\25BC";
  position: absolute;
  padding: 0 5%;
  transform: scaleY(50%) rotate(180deg);
  opacity: 33%;
}

.ingredients-wrapper-sticky.dropdown-closed > h2::after {
  transform: scaleY(50%);
}

.ingredients-wrapper-sticky.dropdown-closed > table {
  display: none;
}
</style>