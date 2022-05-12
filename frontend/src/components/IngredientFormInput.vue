<template>
  <div class="ingredient">
    <input
      type="number"
      placeholder="1"
      :value="ingredient.quantity"
      name="quantity"
      id=""
    />
    <select name="unit" id="">
      <option v-for="option in unitOptions" :key="option" :value="option">
        {{ option }}
      </option>
    </select>
    <input
      type="text"
      :value="ingredient.name"
      placeholder="Tomate"
    />
  </div>
</template>

<script>
export default {
  name: "IngredientFormInput",
  props: ["ingredient"],
  data() {
    return {
      unitOptions: ["pieces", "tabelspoon", "gramm", "liter"],
    };
  },
};
</script>

<style>
</style>