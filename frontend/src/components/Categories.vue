<template>
  <section class="categories">
    <article class="category" v-for="(icon_name, name) in categories" :key="icon_name">
      {{ name }} <ion-icon :name="icon_name"></ion-icon>
    </article>
  </section>
</template>

<script>
export default {
  name: 'categories',
  data() {
    return {
      categories: {
        Schnell: 'hourglass-outline',
        Gesund: 'hourglass-outline',
        Nudelgerichte: 'hourglass-outline',
        Vegetarisch: 'hourglass-outline',
        Italienisch: 'hourglass-outline',
      },
    }
  },
}
</script>

<style>
.categories {
  display: flex;
  align-items: center;
  overflow-x: scroll;
  scroll-snap-type: x mandatory;
  /* scrollbar-width: none; */
}

.categories .category {
  border: 1px solid grey;
  padding: 0.5rem;
  border-radius: 5px;
  display: flex;
  align-items: center;
  scroll-snap-align: center;
}

.category + .category {
  margin-left: 0.5rem;
}

.category ion-icon {
  margin-left: 0.5rem;
}
</style>
