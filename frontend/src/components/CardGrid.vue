<template>
  <div class="card-grid">
    <slot />
  </div>
</template>

<script>
export default {
  name: 'CardGrid'
}
</script>

<style>
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  grid-auto-rows: 460px;
  gap: 10px;
}

.card-wrapper {
  max-width: 460px;
  min-width: 230px;
  height: 460px;
  width: 100%;
  display: inline-block;
}
</style>