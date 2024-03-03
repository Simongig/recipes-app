<template>
  <button class="btn {{type}}">
    <ion-icon v-if="icon_placement == 'before'" name="{{icon_name}}"></ion-icon>
    {{text}}
    <ion-icon v-else name="{{icon_name}}"></ion-icon>
  </button>
</template>

<script>
export default {

  name: "Button",
  props: ["text", "type", "icon_name", "icon_placement"],
}
</script>

<style>

</style>