<template>
  <div class="preview-image-container w-full h-auto flex flex-row flex-wrap gap-2 justify-center">
    <div v-for="(image, index) in previewImages" :key="index" class="preview-image object-cover cursor-pointer" :class="image.selected == true ? 'border-2 border-blue-500' : ''" @click="selectImage(index)">
      <img class="h-30" :key="index" :src="image.src" :id="'image_' + index">
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const name = 'ImageUploadPreview'

interface Image {
  url: string | null
  file: File | null
  selected: boolean
}

const props = defineProps({
  images: {
    type: Array<Image>,
    required: true,
  },
})

function selectImage(index: number) {
  props.images[index].selected = !props.images[index].selected
}

const previewBlobs: string[] = []

function getImageURLfromFile(image: File) {
  const src = URL.createObjectURL(image)
  previewBlobs.push(src)
  return src
}

const previewImages = computed(() => {
  previewBlobs.forEach((src) => URL.revokeObjectURL(src))
  previewBlobs.length = 0

  return props.images.map((image) => {
    if (image.url) {
      return { src: image.url, selected: image.selected }
    } 
    if (image.file) {
      const src = getImageURLfromFile(image.file)
      return { src: src, selected: image.selected }
    }
    return { src: '', selected: image.selected }
  })
})
</script>

<style>

</style>
