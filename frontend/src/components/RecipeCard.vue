<template>
  <router-link :to="{ name: 'recipePdp', params: { id: recipe.id } }">
    <section class="card">
      <div class="img-wrapper">
        <img
          :src="recipe.imagePaths[0]"
          loading="lazy"
          width="1080"
          alt="`Image of ${recipe.title}`"
        />
      </div>
      <div class="card-content">
        <h2>{{ recipe.title }}</h2>
        <span>Dauer: {{ recipe.duration }} min</span>
        <ul v-for="ingredient in recipe.ingredients" :key="ingredient.name">
          <li>
            {{ ingredient.quantity }} {{ ingredient.unit }}:
            {{ ingredient.name }}
          </li>
        </ul>
        <span class="recipe-author">Author <b>Simon</b></span>
      </div>
    </section>
  </router-link>
</template>

<script>
export default {
  name: 'RecipCard',
  props: ['recipe'],
}
</script>

<style>
.card {
  border: 1px solid rgba(100, 100, 111, 0.2);
  border-radius: 10px;
  overflow: hidden;
  display: grid;
  grid-template-rows: 2fr 1fr;
  max-height: 400px;
}

.card-content {
  padding: 20px;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
}

.card-content .recipe-author {
  margin-top: 1rem;
  font-size: 0.5rem;
}

.card-content h2 {
  margin: 0 0 1rem 0;
}

.card-content ul {
  list-style: none;
}

h2 {
  padding: initial;
}

.img-wrapper {
  overflow: hidden;
}

img {
  object-fit: cover;
  width: 100%;
  height: 100%;
}
</style>
