<template>
  <button class="btn {{type}}">
    {{text}}
  </button>
</template>

<script>
export default {

  name: "Button",
  props: ["text", "type"],
}
</script>

<style>
    .btn {
        min-height: 2rem;
        padding: 0.5rem;
        font-size: 1rem;
    }

    .btn-fullwidth {
        width: 100%;
    }
</style>