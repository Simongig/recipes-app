<template>
  <div>
    <p>{{ props.label }}: {{ count }} (doubled: {{ doubled }})</p>
    <button @click="increment">+1</button>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'

const name = 'HelloTs'

interface Props {
  label: string
}

const props = defineProps<Props>()

const count = ref(0)
const doubled = computed(() => count.value * 2)

function increment() {
  count.value++
}
</script>

<style scoped>
button {
  color: var(--secondary-color);
}
</style>
