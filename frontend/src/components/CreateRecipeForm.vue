<template>
  <form id="recipe-form" method="post" action="api/v1/recipe/add" class="boxshadow recipe-create-form"
    enctype="multipart/form-data">
    <h2>Fügen Sie Ihr Rezept hinzu</h2>
    <div class="form-field-wrapper">
      <input type="text" ref="recipeTitle" required name="title" placeholder="*not-shown*" id="" />
      <label for="title">Wie heißt das Rezept?</label>
    </div>
    <div class="form-field-wrapper">
      <input type="number" required name="duration" placeholder="*not-shown*" id="" />
      <label for="title">Wie lange dauert es?</label>
      <div style="padding: 0 1rem">Minuten</div>
    </div>
    <div class="form-field-wrapper">
      <input type="number" required name="portions" placeholder="*not-shown*" id="" />
      <label for="title">Für wie viele Personen reicht es?</label>
      <div style="padding: 0 1rem">Personen</div>
    </div>
    <fieldset class="form-section">
      <legend>Zutaten</legend>
      <div class="ingredients-grid">
        <div class="ingredient" v-for="(ingredient, index) in ingredients" :key="index">
          <input type="text" inputmode="numeric" placeholder="1" v-model="ingredients[index].quantity" min="0.000001"
            id="" />
          <select id="" v-model="ingredients[index].unit">
            <option v-for="option in unitOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
          <input type="text" v-model="ingredients[index].name" placeholder="Tomate" />
        </div>
      </div>
      <div class="add-element-wrapper">
        <Button @click="addIngredient()" variant="outline" class="add-element">
          <Plus class="size-5" />
          Zutat hinzufügen
        </Button>
      </div>
    </fieldset>
    <fieldset class="form-section">
      <legend>Zubereitungsschritte</legend>
      <div class="preparationSteps-grid">
        <div class="preparationStep" v-for="(preparationStep, index) in preparationSteps" :key="index">
          <input type="text" required placeholder="Vorbereitung" id="" v-model="preparationSteps[index].title" />
          <textarea id="" required cols="30" rows="10"
            placeholder="Wasser in einen Topf füllen und reichlich Salz dazu geben"
            v-model="preparationSteps[index].content"></textarea>
        </div>
        <div class="add-element-wrapper">
          <Button @click="addPreparationStep()" variant="outline" class="add-element"
            value="" >
            <Plus class="size-5" />
            Schritt hinzufügen
          </Button>
        </div>
      </div>
    </fieldset>
    <fieldset class="form-section custom-file-input-container">
      <legend>Bilder</legend>
      <ImageUploadPreview :images="images" />
      <p v-if="images.length > 0" class="text-sm text-muted-foreground text-center mt-4">
        Wähle mindestens ein Bild aus
      </p>
      <div class="add-element-wrapper flex gap-2 align-center justify-center">
        <Button variant="outline" type="button" class="custom-file-input-button flex flex-col px-4 py-2 h-auto" value="Eigene Bilder hinzufügen" @click="updateFilesCustom()">
          <Plus class="size-10 p-2 border-1 border-black/30 rounded-full" />
          Eigene Bilder hinzufügen
        </Button>
        <Button variant="outline" type="button" class="custom-file-input-button flex flex-col px-4 py-2 h-auto" @click="updateFilesStock()">
          <Plus class="size-10 p-2 border-1 border-black/30 rounded-full" />
          Stockbilder hinzufügen
        </Button>
        <input type="file" ref="fileInput" class="custom-file-input" multiple name="images" id="file-input" required
          @change="updateFilesArray()" />
      </div>
    </fieldset>
    <Button @click="sendRecipe()"
      type="button" class="submit-button bg-green-700 hover:bg-green-600 font-bold text-white" value="Rezept hochladen">
      Rezept hochladen
    </Button>
  </form>
</template>

<script setup>
import axios from 'axios'
import api from '../services/api'
import ImageUploadPreview from './ImageUploadPreview.vue'
import { Plus } from '@lucide/vue'
import { Button } from '@/components/ui/button'
import router from '../router'
import { ref } from 'vue'
import { useAlertStore } from '@/stores/alertStore'

const name = 'createRecipeForm'

const alertStore = useAlertStore()

const images = ref([]);
const ingredients = ref([{ name: '', unit: '', quantity: '' }]);
const preparationSteps = ref([{}]);
const selectedImageType = ref('');
const unitOptions = ref([]);
const fileInput = ref(null);
const recipeTitle = ref(null);

api.get('/api/v1/ingredient/units').then((response) => {
  unitOptions.value = response.data
  if (ingredients.value[0] && !ingredients.value[0].unit) {
    ingredients.value[0].unit = unitOptions.value[0]?.value ?? ''
  }
})

async function updateFilesStock() {
  selectedImageType.value = 'stock';
  const input = recipeTitle.value?.value.trim()
  if (!input) {
    alertStore.addAlert({ title: 'Fehler', message: 'Bitte geben Sie einen Rezepttitel ein, um passende Stockbilder zu finden.', variant: 'destructive' })
    return
  }
  const query = input.replace(/\s+/g, '-')
  const response = await axios
    .get(
      'https://api.unsplash.com/search/photos?client_id=ZlrYQ-virrK3j1gPYVdac_pQQ63rplNe52KDDdubxb0&query=' +
      query +
      '&collections=food-drinks',
    )
  const fetchedImages = response.data.results.map((result) => { return { url: result.urls.small, file: null, selected: false } })
  images.value = fetchedImages
}

function updateFilesCustom() {
  fileInput.value?.click()
}

function updateFilesArray() {
  selectedImageType.value = 'custom';
  images.value = []
  const addedFiles = fileInput.value?.files
  if (!addedFiles) return
  for (let i = 0; i < addedFiles.length; i++) {
    images.value.push({
      url: null,
      file: addedFiles[i],
      selected: false
    })
  }
}

function addIngredient() {
  ingredients.value.push({
    name: '',
    unit: unitOptions.value[0]?.value ?? '',
    quantity: '',
  })
}

function addPreparationStep() {
  preparationSteps.value.push({
    title: '',
    content: '',
  })
}

function getSelectedImages() {
  return images.value.filter((image) => image.selected)
}

async function sendRecipe() {
  const form = document.querySelector('form')
  const formData = new FormData(form)
  const imageURLArr = []

  if (selectedImageType.value === 'stock') {
    const selectedImages = getSelectedImages()
    if (selectedImages.length === 0) {
      alertStore.addAlert({ title: 'Fehler', message: 'Bitte wählen Sie mindestens ein Stockbild aus.', variant: 'destructive' })
      return
    }
    imageURLArr.push(...selectedImages.map((image) => image.url))
  } else if (selectedImageType.value === 'custom') {
    const selectedFiles = getSelectedImages().filter((image) => image.file)
    if (selectedFiles.length === 0) {
      alertStore.addAlert({ title: 'Fehler', message: 'Bitte wählen Sie mindestens eine Bilddatei aus.', variant: 'destructive' })
      return
    }
    formData.delete('images') // Remove existing images first
    selectedFiles.forEach((image) => {
      formData.append('images', image.file)
    })
  }

  var jsonString = JSON.stringify({
    title: formData.get('title'),
    duration: formData.get('duration'),
    portions: formData.get('portions'),
    ingredients: ingredients.value,
    preparationSteps: preparationSteps.value,
    imagePaths: imageURLArr,
  })
  const data = new Blob([jsonString], { type: 'application/json' })

  formData.append('data', data)

  console.debug('Sending recipe data: {}', formData)

  api
    .post('/api/v1/recipe/add', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    .then((response) => {
      if (200 == response.status) {
        alertStore.addAlert({ title: 'Dein Rezpet wurde erfolgreich hochgeladen!', variant: 'success' })
      } else {
        alertStore.addAlert({ title: 'Fehler', message: 'Oh nein! Irgendwas ist beim Upload schiefgelaufen :( \n Code: ' + response.status, variant: 'destructive' })
      }
      router.push({ path: '/' })
    })
    .catch((e) => {
      alertStore.addAlert({ title: 'Fehler', message: 'Oh nein! Irgendwas ist beim Upload schiefgelaufen :(', variant: 'destructive' })
      console.log(e)
    })
}
</script>

<style>
.add-element-wrapper {
  margin: 15px 10px 0 10px;
  display: flex;
  text-align: center;
  justify-content: center;
}

.form-section legend {
  padding: 0 0.7rem;
}

.form-field-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.form-field-wrapper>label {
  position: absolute;
  left: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  color: grey;
  transition: all ease-in-out 0.15s;
  font-size: small;
}

.form-field-wrapper>input:focus+label,
.form-field-wrapper>input:not(:placeholder-shown)+label {
  top: -25%;
  transform: translateY(0);
  background-color: white;
  padding: 0 0.5rem;
  font-size: x-small;
}

.form-field-wrapper>input::placeholder {
  color: transparent;
}

.form-field-wrapper>input {
  width: 100%;
  height: 100%;
}

.recipe-create-form {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  width: min(90vw, 700px);
  margin: auto auto;
  gap: 10px;
  border: 1px solid rgba(100, 100, 111, 0.2);
  padding: 20px;
  border-radius: 10px;
}

.ingredients-grid {
  display: grid;
  gap: 5px;
  grid-auto-rows: minmax(2rem, auto);
}

.ingredient {
  display: grid;
  grid-template-columns: 3rem 5rem auto;
  gap: 5px;
}

.boxshadow {
  box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px;
}

input,
textarea,
.add-element,
select {
  border-radius: 10px;
  border: 1px solid rgba(100, 100, 111, 0.2);
  padding: 0 0.5rem;
  max-height: 2rem;
}

fieldset {
  border-radius: 10px;
  border: 1px solid rgba(100, 100, 111, 0.7);
  padding: 20px;
}

.custom-file-input-container input[type='file'] {
  display: none;
}

.custom-file-input input[type='button'] {
  width: 100%;
  height: 100%;
}

.preparationSteps-grid {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  gap: 10px;
}

.preparationStep {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  gap: 5px;
}

textarea {
  padding: 0.5rem;
  max-height: inherit;
  resize: none;
}
</style>
