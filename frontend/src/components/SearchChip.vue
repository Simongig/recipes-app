<template>
  <li class="chip">
    <span>{{ value }}</span>
    <button class="chip--close">
      <img src="../assets/icons/x.svg" alt="" />
    </button>
  </li>
</template>

<script>
export default {
  name: "SearchChip",
  props: ["value", "index"],
};
</script>

<style>
.chip {
  cursor: pointer;
  border-radius: 20px;
  background-color: rgb(225, 225, 225);
  display: inline-flex;
  align-items: center;
  overflow: hidden;
  height: 100%;
}

.chip span {
  padding: 0.3rem 0.7rem;
  display: inline-block;
}

.chip img {
  height: 100%;
}

.chip--close {
  background-color: transparent;
  display: inline-flex;
  border: none;
  height: 100%;
  padding: 0 0.25rem 0 0;

}
</style>