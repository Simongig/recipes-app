<template>
  <main class="recipe-pdp-wrapper" v-if="recipe != null">
    <h1 class="site-title">{{ recipe.title }}</h1>
    <article class="grid grid-cols-12 gap-6">
      <section class="recipe-info-wrapper col-span-12 flex justify-end">
        <div>
          Dauer:<br> 
          <span v-if="recipe.duration < 60" class="font-bold">{{ recipe.duration }} min</span>
          <span v-else class="font-bold">{{ Math.floor(recipe.duration / 60) }} h {{ recipe.duration % 60 }} min</span>
        </div>
      </section>
      <section class="img-wrapper col-span-12 md:max-h-[400px] lg:max-h-[500px]">
        <img class="md:max-h-[400px] lg:max-h-[500px]" :src="recipe.imagePaths[0]" alt="" />
      </section>
      <section class="ingredients-wrapper col-span-12 md:col-span-6 lg:col-span-4">
        <h2>Zutaten</h2>
        <p>für {{ recipe.portions }} Personen</p>
        <table>
          <tr v-for="ingredient in recipe.ingredients" :key="ingredient.name">
            <td>{{ formatIngredientQuantity(ingredient) }}</td>
            <td>{{ ingredient.name }}</td>
          </tr>
        </table>
      </section>
      <section class="content-wrapper col-span-12 md:col-span-6 lg:col-span-8">
        <div class="preparationSteps-wrapper" v-if="recipe.preparationSteps">
          <h2 @click="toggleIngredientsDropDown">Zubereitung</h2>
          <table>
            <div class="preparationStep" v-for="(step, index) in recipe.preparationSteps" :key="step.title"
              :id="'step-' + index">
              <tr>
                <th>
                  <h3 class="text-lg font-semibold">{{ step.title }}</h3>
                </th>
              </tr>
              <tr>
                <td>{{ step.content }}</td>
              </tr>
            </div>
          </table>
        </div>
      </section>
    </article>
    <section class="ingredients-wrapper-sticky dropdown-closed">
      <h2>
        Zutaten
        <ion-icon class="chevron-up-outline hydrated" name="chevron-up-outline"></ion-icon>
      </h2>
      <table>
        <tr v-for="ingredient in recipe.ingredients" :key="ingredient.name">
          <td>{{ formatIngredientQuantity(ingredient) }}</td>
          <td>{{ ingredient.name }}</td>
        </tr>
      </table>
    </section>
    <!-- <Button :text="test"></Button> -->
  </main>
  <div v-else>Couldn't find this recipe</div>
</template>

<script>
// import Button from '../components/Button.vue';
import axios from 'axios'
import { formatIngredientQuantity } from '../utils/ingredients'
export default {
  // components: { Button },
  data() {
    return {
      recipe: null,
    }
  },
  methods: {
    formatIngredientQuantity,
    toggleIngredientsDropDown() {
      const sticky_ingredients_el = document.querySelector('.ingredients-wrapper-sticky')
      if (!sticky_ingredients_el) return;
      sticky_ingredients_el.classList.toggle('dropdown-closed')
    },
    makeIngredientsSticky() {
      let ingredients_elem = document.querySelector('.ingredients-wrapper')
      let ingredients_elem_sticky = document.querySelector('.ingredients-wrapper-sticky')

      if (null == ingredients_elem || null == ingredients_elem_sticky) {
        return
      }
      let scrolldepth_lower_than_element =
        window.scrollY > ingredients_elem.offsetTop + ingredients_elem.offsetHeight
      console.log('scrolldepth_lower_than_element:', scrolldepth_lower_than_element)
      if (scrolldepth_lower_than_element) {
        ingredients_elem_sticky.classList.add('visible')
        ingredients_elem_sticky.classList.add('dropdown-closed')
      } else {
        ingredients_elem_sticky.classList.remove('visible')
      }
    },
  },
  mounted() {
    axios
      .get('/api/v1/recipe/id/' + this.$route.params.id)
      .then((response) => {
        this.recipe = response.data
        document.title = 'kochbuch.io - ' + this.recipe.title
      })
      .catch((reason) => {
        console.error(reason)
      })
    document.addEventListener('scroll', this.makeIngredientsSticky)
  },
  beforeUnmount() {
    document.removeEventListener('scroll', this.makeIngredientsSticky)
  },
}
</script>

<style scoped>
.preparationSteps-wrapper {
  width: 100%;
}

.ingredients-wrapper table,
.ingredients-wrapper-sticky table {
  width: 100%;
  display: grid;
  max-height: 50vh;
  overflow-y: auto;
}

.ingredients-wrapper tr+tr,
.ingredients-wrapper-sticky tr+tr,
.preparationSteps-wrapper .preparationStep+.preparationStep {
  border-top: 1px solid #0000001c;
  border-collapse: collapse;
}

.preparationSteps-wrapper .preparationStep+.preparationStep {
  margin: 1rem 0;
  padding-top: 1rem;
}

.ingredients-wrapper tr,
.ingredients-wrapper-sticky tr {
  padding: 0.5rem;
}

.ingredients-wrapper tr td:first-child,
.ingredients-wrapper-sticky tr td:first-child {
  width: 150px;
  padding-left: 1rem;
}

.img-wrapper {
  width: 100%;
  height: 100%;
}

.img-wrapper>img {
  object-fit: cover;
  object-position: center;
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

.ingredients-wrapper-sticky>h2 {
  margin-left: 2rem;
  display: flex;
  align-items: center;
}

.ingredients-wrapper-sticky.dropdown-closed>h2 {
  margin-bottom: 0;
}

.ingredients-wrapper-sticky:not(.dropdown-closed) .chevron-up-outline {
  transform: rotate(180deg);
}

.ingredients-wrapper-sticky .chevron-up-outline {
  margin-left: 0.5rem;
}

.ingredients-wrapper-sticky.dropdown-closed>table {
  display: none;
}
</style>
