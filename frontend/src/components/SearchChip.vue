<template>
  <li class="selected-ingredient chip">
    {{ value }}
  </li>
</template>

<script>
export default {
  name: "searchChip",
  props: ["value", "index"],
};
</script>

<style>
.chip {
    cursor: pointer;
}
/* .chip:hover::after {
    color: var(--main-color);
  content: "x";
  position: absolute;
  right: -5px;
  top: -5px;
  background-color: red;
  width: 1rem;
  height: 1rem;
  text-align: center;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
} */

.chip:hover::after {
    content: "delete";
    background-color: red;
    margin-left: 0.5rem;
    padding: 0.2rem 0.2rem;
}
</style>