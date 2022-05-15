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
  },
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


.recipe-pdp-content .ingredients-wrapper h2,
.recipe-pdp-content .preparationSteps-wrapper h2 {
  margin-bottom: 1rem;
}

.ingredients-wrapper table {
  width: 100%;
  display: grid;
}

.ingredients-wrapper tr + tr,
.preparationSteps-wrapper .preparationStep + .preparationStep {
  border-top: 1px solid #0000001c;
  border-collapse: collapse;
}

.preparationSteps-wrapper .preparationStep + .preparationStep {
  margin: 1rem 0;
}

.ingredients-wrapper tr,
.preparationSteps-wrapper .preparationStep {
  padding: 0.5rem;
}

.ingredients-wrapper tr td:first-child {
  width: 150px;
  padding-left: 1rem;
}

.recipe-pdp-wrapper img {
  width: 100%;
}
</style>