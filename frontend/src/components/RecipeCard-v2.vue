<template>
  <router-link class="card-wrapper" :to="{name: 'recipePdp', params: { id: recipe.id }}">
    <section
      class="card"
      :style="`background-image: url(${recipe.imagePaths[0]})`"
    >
      <div class="card-content">
        <h2>{{ recipe.title }}</h2>
        <span>Dauer: {{ recipe.duration }} min</span>
        <span class="recipe-author">Author <b>Simon</b></span>
      </div>
    </section>
  </router-link>
</template>

<script>
export default {
  name: "RecipCard",
  props: ["recipe"],
};
</script>

<style>
.card {
  border-radius: 10px;
  overflow: hidden;
  display: grid;
  height: 100%;
  background-position: center;
  background-size: cover;
  align-items: flex-end;
}

.card * {
  color: #ebebeb;
}

.card-content {
  padding: 20px;
  max-height: 50%;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
background: rgb(1,1,1);
background: linear-gradient(0deg, rgba(0, 0, 0, 0.89) 50%, rgba(1,1,1,0) 100%);
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
  font-size: 2rem;
}
</style>