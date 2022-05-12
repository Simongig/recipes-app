<template>
  <div v-if="0 < images.length" class="preview-image-container">
    <span
      class="preview-image"
      v-for="image in images"
      :style="{ 'background-image': 'url(' + getImageURLfromFile(image) + ')' }"
      :id="getImageIDfromURL(image)"
      :key="image"
    ></span>
  </div>
</template>

<script>
export default {
  name: "ImageUploadPreview",
  props: ["images"],
  methods: {
    getImageURLfromFile(image) {
      window.imageURL = URL.createObjectURL(image);
      return URL.createObjectURL(image);
    },
    getImageIDfromURL(image) {
      var url = this.getImageURLfromFile(image);
      return url.replace(
        "blob:" + document.location.origin + "/",
        ""
      );
    },
  },
};
</script>

<style>
.preview-image-container {
  display: flex;
  justify-items: start;
  height: auto;
}

.preview-image {
  width: auto;
  max-width: 50px;
  max-height: 50px;
  aspect-ratio: 1/1;
  background-size: cover;
  background-position: center;
  border-radius: 100%;
}

.preview-image + .preview-image {
  margin-left: 5px;
}
</style>