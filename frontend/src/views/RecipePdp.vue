<template>
  <main class="recipe-pdp-wrapper" v-if="recipe != null">
    <div class="flex items-baseline justify-between mb-3 gap-3">
      <h1 class="mb-0 pb-0 text-4xl md:text-5xl md:max-w-[70%]">{{ recipe.title }}</h1>
      <button><Heart @click="toggleAddToFavorites" :class="isFavorite ? 'fill-red-500 text-red-500' : 'text-gray-500'" class="w-7 h-7 mb-3 cursor-pointer" /></button>    </div>
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
        <h2 class="flex items-center ml-[1.4rem] mb-0">
          Zutaten
          <ChevronUpIcon class="chevron-up-outline w-8 h-8" />
        </h2>
      </div>
      <table class="grid grid-cols-2 overflow-y-scroll max-h-[75vh] mt-3">
        <tr class="grid grid-cols-subgrid col-span-2" v-for="ingredient in recipe.ingredients" :key="ingredient.name">
          <td>{{ formatIngredientQuantity(ingredient) }}</td>
          <td>{{ ingredient.name }}</td>
        </tr>
      </table>
    </section>
  </main>
  <div v-else>Couldn't find this recipe</div>
</template>

<script setup>
// import Button from '../components/Button.vue';
import axios from 'axios'
import api from '@/services/api'
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { formatIngredientQuantity } from '../utils/ingredients'
import { CheckCircle2Icon, CopyIcon, Heart, ChevronUpIcon } from '@lucide/vue'
import { Alert, AlertTitle } from "@/components/ui/alert"
import { useAlertStore } from '@/stores/alertStore'
import { useAuthStore } from '@/stores/authStore'
import { useUserStore } from '@/stores/userStore'
import { useRoute } from "vue-router";

const recipe = ref(null)
const ingredientsTable = ref(null)
const alertStore = useAlertStore()
const authStore = useAuthStore()
const userStore = useUserStore()
const ingredientsListSticky = ref(null)
const isFavorite = ref(false)
  
formatIngredientQuantity

const route = useRoute()
if (authStore.isLoggedIn) {
  userStore.fetchUser().then(() => {
    isFavorite.value = userStore.user?.recipes?.includes(route.params.id)
  })
}
const user = ref(userStore.user)

function setMetaTag(attr, key, content) {
  if (!content) return
  let el = document.querySelector(`meta[${attr}="${key}"]`)
  if (!el) {
    el = document.createElement('meta')
    el.setAttribute(attr, key)
    document.head.appendChild(el)
  }
  el.setAttribute('content', content)
}

onMounted(() => {
  axios
    .get('/api/v1/recipe/id/' + route.params.id)
    .then((response) => {
      recipe.value = response.data
      const title = 'kochbuch.io - ' + recipe.value.title
      document.title = title
      setMetaTag('property', 'og:title', title)
      setMetaTag('name', 'twitter:title', title)
      const image = recipe.value.imagePaths?.[0]
      setMetaTag('property', 'og:image', image)
      setMetaTag('name', 'twitter:image', image)
      setMetaTag('property', 'og:url', window.location.href)
    })
    .catch((reason) => {
      console.error(reason)
    })
  document.addEventListener('scroll', makeIngredientsSticky)
})

onBeforeUnmount(() => {
  document.removeEventListener('scroll', makeIngredientsSticky)
  document.title = 'kochbuch.io'
  setMetaTag('property', 'og:title', 'kochbuch.io')
  setMetaTag('name', 'twitter:title', 'kochbuch.io')
  setMetaTag('property', 'og:image', '')
  setMetaTag('name', 'twitter:image', '')
  setMetaTag('property', 'og:url', window.location.href)
})

function toggleIngredientsDropdown() {
  const ingredients_sticky_el = ingredientsListSticky.value;
  if (!ingredients_sticky_el) return;
  ingredients_sticky_el.classList.toggle('dropdown-closed')
}

function makeIngredientsSticky() {
  let ingredients_elem = document.querySelector('.ingredients-wrapper')
  let ingredients_elem_sticky = ingredientsListSticky.value

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
}

function toggleAddToFavorites() {
  if (!authStore.isLoggedIn) {
    alertStore.addAlert({ title: 'Bitte melden Sie sich an', variant: 'destructive' })
    return
  }
  api.patch('/api/v1/user/updateRecipe', { recipeId: recipe.value.id, add: !isFavorite.value })
    .then((response) => {
      if (200 != response.status) {
        alertStore.addAlert({ title: 'Fehler', message: 'Oh nein! Irgendwas ist beim Hinzufügen zu den Favoriten schiefgelaufen :( \n Code: ' + response.status, variant: 'destructive' })
        return;
      }
      userStore.setUser(response.data) // TODO: Validate UserDTO structure
      isFavorite.value = !isFavorite.value
      })
    .catch((e) => {
      alertStore.addAlert({ title: 'Fehler', message: 'Oh nein! Irgendwas ist beim Hinzufügen zu den Favoriten schiefgelaufen :(', variant: 'destructive' })
      console.error(e)
    })
}

function copyToClipboard() {
  let ingredients_elem = ingredientsTable.value
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
      alertStore.addAlert({ title: 'Zutaten kopiert', duration: 1.5, variant: 'success' })
    },
    (err) => {
      console.error('Could not copy text: ', err)
      alertStore.addAlert({ title: 'Konnte Zutaten nicht kopieren', variant: 'destructive' })
    }
  )
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
