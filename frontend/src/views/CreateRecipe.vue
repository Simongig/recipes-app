<template>
  <main>
    <create-recipe-form />
  </main>
</template>
<script>
import CreateRecipeForm from '../components/CreateRecipeForm.vue'
// @ is an alias to /src

export default {
  name: 'CreateRecipe',
  components: {
    CreateRecipeForm,
  },
}
</script>
