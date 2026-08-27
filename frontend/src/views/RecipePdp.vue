<template>
  <main class="recipe-pdp-wrapper" v-if="recipe != null">
    <h1 class="mb-0 pb-0 text-4xl md:text-5xl md:max-w-[70%]">{{ recipe.title }}</h1>
    <article class="grid grid-cols-12 gap-6">
      <section class="recipe-info-wrapper col-span-12 flex justify-end">
        <div>
          Dauer:<br>
          <span v-if="recipe.duration <= 60" class="font-bold">{{ recipe.duration }} min</span>
          <span v-else class="font-bold">{{ Math.floor(recipe.duration / 60) }} h {{ recipe.duration % 60 }} min</span>
        </div>
      </section>
      <section class="img-wrapper col-span-12 max-h-[300px] md:max-h-[400px] lg:max-h-[500px]">
        <img class="max-h-[300px] md:max-h-[400px] lg:max-h-[500px]" :src="recipe.imagePaths[0]" alt="" />
      </section>
      <section class="ingredients-wrapper col-span-12 md:col-span-6 lg:col-span-4">
        <div>
          <h2 class="flex items-center">Zutaten
            <button title="Zutaten kopieren" ref="copyIngredientsButton" id="copy_ingredients"
              class="ml-3 outline p-[2px_4px] outline rounded" @click="copyToClipboard">
              <CopyIcon class="w-4 h-4" />
            </button>
          </h2>
        </div>
        <p>für {{ recipe.portions }} Personen</p>
        <table ref="ingredientsTable" class="grid grid-cols-2 grid-cols-[auto_auto] gap-x-3">
          <tr class="grid grid-cols-subgrid col-span-2" v-for="ingredient in recipe.ingredients" :key="ingredient.name">
            <td>{{ formatIngredientQuantity(ingredient) }}</td>
            <td>{{ ingredient.name }}</td>
          </tr>
        </table>
      </section>
      <section class="content-wrapper col-span-12 md:col-span-6 lg:col-span-8">
        <div class="preparationSteps-wrapper" v-if="recipe.preparationSteps">
          <h2>Zubereitung</h2>
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
    <section class="ingredients-wrapper-sticky dropdown-closed" ref="ingredientsListSticky">
      <div @click="toggleIngredientsDropdown">
        <h2 class="flex items-center ml-[1.4rem] mb-0" >
          Zutaten
          <ion-icon class="chevron-up-outline hydrated" name="chevron-up-outline"></ion-icon>
        </h2>
      </div>
      <table class="grid grid-cols-2 overflow-y-scroll max-h-[75vh] mt-3">
        <tr class="grid grid-cols-subgrid col-span-2" v-for="ingredient in recipe.ingredients" :key="ingredient.name">
          <td>{{ formatIngredientQuantity(ingredient) }}</td>
          <td>{{ ingredient.name }}</td>
        </tr>
      </table>
    </section>
    <div class="alerts-container">
      <Alert ref="alert" class="border-green-700 bg-green-100 fixed top-[15vh] left-1/2 -translate-x-1/2 w-fit z-50"
        v-if="alertTitle">
        <CheckCircle2Icon />
        <AlertTitle>{{ alertTitle }}</AlertTitle>
      </Alert>
    </div>
  </main>
  <div v-else>Couldn't find this recipe</div>
</template>

<script>
// import Button from '../components/Button.vue';
import axios from 'axios'
import { formatIngredientQuantity } from '../utils/ingredients'
import { CheckCircle2Icon, CopyIcon } from '@lucide/vue'
import { Alert, AlertTitle } from "@/components/ui/alert"
export default {
  components: { Alert, AlertTitle, CheckCircle2Icon, CopyIcon },
  data() {
    return {
      recipe: null,
      alertTitle: '',
    }
  },
  methods: {
    formatIngredientQuantity,
    toggleIngredientsDropdown() {
      const ingredients_sticky_el = this.$refs.ingredientsListSticky;
      if (!ingredients_sticky_el) return;
      ingredients_sticky_el.classList.toggle('dropdown-closed')
    },
    makeIngredientsSticky() {
      let ingredients_elem = document.querySelector('.ingredients-wrapper')
      let ingredients_elem_sticky = this.$refs.ingredientsListSticky

      if (null == ingredients_elem || null == ingredients_elem_sticky) {
        return
      }
      let scrolldepth_lower_than_element =
        window.scrollY > ingredients_elem.offsetTop + ingredients_elem.offsetHeight
      if (scrolldepth_lower_than_element) {
        ingredients_elem_sticky.classList.add('visible')
        document.body.classList.add('ingredients-sticky-visible')
        ingredients_elem_sticky.classList.add('dropdown-closed')
      } else {
        document.body.classList.remove('ingredients-sticky-visible')
        ingredients_elem_sticky.classList.remove('visible')
      }
    },
    copyToClipboard() {
      let ingredients_elem = this.$refs.ingredientsTable
      if (null == ingredients_elem) {
        return
      }
      let ingredients_text = ''
      for (let i = 0; i < ingredients_elem.rows.length; i++) {
        let row = ingredients_elem.rows[i]
        let quantity = row.cells[0].innerText
        let name = row.cells[1].innerText
        ingredients_text += `${quantity} ${name}\n`
      }
      navigator.clipboard.writeText(ingredients_text).then(
        () => {
          this.alertTitle = 'Zutatenliste kopiert'
          setTimeout(() => {
            this.alertTitle = ''
            this.alertDescription = ''
          }, 5000)
        },
        (err) => {
          console.error('Could not copy text: ', err)
          this.alertTitle = 'Fehler beim Kopieren'
          setTimeout(() => {
            this.alertTitle = ''
            this.alertDescription = ''

          }, 5000)
        }
      )
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

:global(.ingredients-sticky-visible) {
	padding-bottom: 4rem;
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
