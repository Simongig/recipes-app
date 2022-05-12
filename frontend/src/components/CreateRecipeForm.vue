<template>
  <form
    id="recipe-form"
    method="post"
    action="api/v1/recipe/add"
    class="boxshadow recipe-create-form"
    enctype="multipart/form-data"
  >
    <h2>Add a recipe here</h2>
    <input
      type="text"
      value="Lasagne"
      required
      name="title"
      placeholder="Titel"
      id=""
    />
    <input
      type="number"
      value="15"
      required
      name="duration"
      placeholder="Dauer"
      id=""
    />
    <input
      type="number"
      value="15"
      required
      name="portions"
      placeholder="Portionen"
      id=""
    />
    <fieldset class="form-section">
      <legend>Ingredients</legend>
      <div class="ingredients-grid">
        <div
          class="ingredient"
          v-for="(ingredient, index) in ingredients"
          :key="index"
        >
          <input
            type="text"
            inputmode="numeric"
            placeholder="1"
            v-model="ingredients[index].quantity"
            min="0.000001"
            id=""
          />
          <select id="" v-model="ingredients[index].unit">
            <option v-for="option in unitOptions" :key="option">
              {{ option }}
            </option>
          </select>
          <input
            type="text"
            v-model="ingredients[index].name"
            placeholder="Tomate"
          />
        </div>
      </div>
      <div class="add-element-wrapper">
        <input
          type="button"
          @click="addIngredient()"
          class="add-element"
          value="Zutat hinzufügen"
        />
      </div>
    </fieldset>
    <fieldset class="form-section">
      <legend>Preparation Steps</legend>
      <div class="preparationSteps-grid">
        <div
          class="preparationStep"
          v-for="(preparationStep, index) in preparationSteps"
          :key="index"
        >
          <input
            type="text"
            required
            placeholder="Preparation"
            id=""
            v-model="preparationSteps[index].title"
          />
          <textarea
            id=""
            required
            cols="30"
            rows="10"
            placeholder="Cut onion"
            v-model="preparationSteps[index].content"
          ></textarea>
        </div>
        <div class="add-element-wrapper">
          <input
            type="button"
            @click="addPreparationStep()"
            class="add-element"
            value="Zubereitungsschritt hinzufügen"
          />
        </div>
      </div>
    </fieldset>
    <div class="custom-file-input">
      <input
        type="button"
        class="custom-file-input-button"
        value="Bilder hinzufügen"
        @click="updateFiles()"
      />
      <input
        type="file"
        class="custom-file-input"
        multiple
        name="images"
        id="file-input"
        required
        @change="updateFilesArray()"
      />
    </div>
    <image-upload-preview :images="files" required />
    <input @click="sendForm()" type="submit" value="Rezept hinzufügen" />
  </form>
</template>

<script>
import axios from "axios";
import ImageUploadPreview from "./ImageUploadPreview.vue";
// import IngredientFormInput from "./IngredientFormInput.vue";
export default {
  name: "createRecipeForm",
  components: { ImageUploadPreview },
  data() {
    return {
      files: [],
      ingredients: [
        {
          quantity: 2,
          unit: "Stück",
          name: "Tomate",
        },
      ],
      preparationSteps: [
        {
          title: "Step1",
          content:
            "Lorem ipsum dolor sit amet consectetur adipisicing elit. Eum ad exercitationem alias sed! Eveniet corrupti dicta hic illo itaque illum.",
        },
      ],
      unitOptions: ["Stück", "Teelöffel", "Gramm", "Liter"],
    };
  },
  mounted() {
    document.querySelector("#recipe-form").addEventListener("submit", (e) => {
      e.preventDefault();
    });
  },
  methods: {
    updateFilesArray() {
      this.files = [];
      let files = document.getElementById("file-input").files;
      for (let i = 0; i < files.length; i++) {
        this.files.push(files[i]);
      }
    },
    addIngredient() {
      this.ingredients.push({
        name: "",
        unit: "",
        quantity: "",
      });
    },
    addPreparationStep() {
      this.preparationSteps.push({
        title: "",
        content: "",
      });
    },
    updateFiles() {
      document.getElementById("file-input").click();
    },
    async sendForm() {
      const form = document.querySelector("form");
      const formData = new FormData(form);

      const imageURL = await axios
        .get(
          "https://api.unsplash.com/search/photos?client_id=ZlrYQ-virrK3j1gPYVdac_pQQ63rplNe52KDDdubxb0&query=" +
            formData.get("title").trim().replace(" ", "-") +
            "&orientation=landscape&collections=food-drinks"
        )
        .then((value) => {
          var random = Math.round((Math.random() * 100) % 4);
          return value.data.results[random].urls.regular;
        });
      const imageURLArr = new Array();
      imageURLArr.push(imageURL);
      console.log(imageURLArr);
      var jsonString = JSON.stringify({
        title: formData.get("title"),
        duration: formData.get("duration"),
        portions: formData.get("portions"),
        ingredients: this.ingredients,
        preparationSteps: this.preparationSteps,
        imagePaths: imageURLArr,
      });
      const data = new Blob([jsonString], { type: "application/json" });

      formData.append("data", data);
      axios
        .post("/api/v1/recipe/add", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          console.log(formData.get("data"));
          console.log(response);
        })
        .catch((e) => {
          console.log(formData.get("data"));
          console.log(e);
        });
    },
  },
};
</script>

<style>
.add-element-wrapper {
  height: 2rem;
  margin: 15px 10px 0 10px;
  display: flex;
  text-align: center;
  justify-content: center;
}

.form-section legend {
  padding: 0 0.7rem;
}

.add-element {
  height: inherit;
  padding-left: 2.5rem;
  background-color: none;
  background-color: transparent;
  background: url("../assets/icons/plus.svg");
  background-size: 15px;
  background-repeat: no-repeat;
  background-position: 0.7rem center;
  text-align: center;
}

.recipe-create-form {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  max-width: 40rem;
  min-width: 320px;
  margin: auto auto;
  gap: 10px;
  border: 1px solid rgba(100, 100, 111, 0.2);
  padding: 20px;
  border-radius: 10px;
}

.ingredients-grid {
  display: grid;
  gap: 5px;
  grid-auto-rows: minmax(2rem, auto);
}

.ingredient {
  display: grid;
  grid-template-columns: 5rem 5rem auto;
  gap: 5px;
}

.boxshadow {
  box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px;
}

input,
textarea,
.add-element {
  border-radius: 10px;
  border: 1px solid rgba(100, 100, 111, 0.2);
}

fieldset {
  border-radius: 10px;
  border: 1px solid rgba(100, 100, 111, 0.7);
  padding: 20px;
}

.custom-file-input input[type="file"] {
  display: none;
}

.custom-file-input input[type="button"] {
  width: 100%;
  height: 100%;
}

.preparationSteps-grid {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  gap: 10px;
}

.preparationStep {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  gap: 5px;
}

input {
  padding: 0 0.5rem;
  max-height: 2rem;
}

input[type="submit"] {
  background-color: darkgreen;
  color: white;
}

form > h2 {
  margin: 0;
  font-size: 1.5rem;
}

textarea {
  padding: 0.5rem;
  max-height: inherit;
  resize: none;
}
</style>