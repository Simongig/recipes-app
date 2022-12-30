<template>
  <form
    id="recipe-form"
    method="post"
    action="api/v1/recipe/add"
    class="boxshadow recipe-create-form"
    enctype="multipart/form-data"
  >
    <h2>Fügen Sie Ihr Rezept hinzu</h2>
    <div class="form-field-wrapper">
    <input
      type="text"
      required
      name="title"
      placeholder="*not-shown*"
      id=""
    />
      <label for="title">Wie heißt das Rezept?</label>
    </div>
    <div class="form-field-wrapper">
    <input
      type="number"
      required
      name="duration"
      placeholder="*not-shown*"
      id=""
    />
      <label for="title">Wie lange dauert es?</label>
      <div style="padding: 0 1rem;">Minuten</div>
    </div>
    <div class="form-field-wrapper">
    <input
      type="number"
      required
      name="portions"
      placeholder="*not-shown*"
      id=""
    />
      <label for="title">Für wie viele Personen reicht es?</label>
      <div style="padding: 0 1rem;">Personen</div>
    </div>
    <fieldset class="form-section">
      <legend>Zutaten</legend>
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
      <legend>Zubereitungsschritte</legend>
      <div class="preparationSteps-grid">
        <div
          class="preparationStep"
          v-for="(preparationStep, index) in preparationSteps"
          :key="index"
        >
          <input
            type="text"
            required
            placeholder="Vorbereitung"
            id=""
            v-model="preparationSteps[index].title"
          />
          <textarea
            id=""
            required
            cols="30"
            rows="10"
            placeholder="Wasser in einen Topf füllen und reichlich Salz dazu geben"
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
    <div class="custom-file-input" v-if="false">
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
    <image-upload-preview v-if="false" :images="files" />
    <input @click="sendForm()" type="submit" value="Rezept hinzufügen" />
  </form>
</template>

<script>
import axios from "axios";
import ImageUploadPreview from "./ImageUploadPreview.vue";
import router from '../router'
// import IngredientFormInput from "./IngredientFormInput.vue";
export default {
  name: "createRecipeForm",
  components: { ImageUploadPreview },
  data() {
    return {
      files: [],
      ingredients: [
        {
        },
      ],
      preparationSteps: [
        {
        },
      ],
      unitOptions: ["Stück", "Teelöffel", "Esslöffel", "Gramm", "Kilo", "Liter", "Prise", "Milliliter", "Bündel"],
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
    getAccessToken() {
      let user = JSON.parse(localStorage.getItem("user"));
      if (user && user.access_token) {
        // for Node.js Express back-end
        console.log(user.access_token)
        return user.access_token ;
      } else {
        return {};
      }
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
            "&collections=food-drinks"
        )
        .then((value) => {
          if(value.data.results == null) return "";
          return value.data.results[0].urls.regular;
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
            "Authorization": "Bearer " + this.getAccessToken()
          },
        })
        .then((response) => {
            if(200 == response.status) {
              alert('Dein Rezpet wurde erfolgreich hochgeladen!')
            } else {
              alert('Oh nein! Irgendwas ist beim Upload schiefgelaufen :( \n Code: ' + response.status)
            }
            router.push({ path: '/' })
        })
        .catch((e) => {
          alert('Oh nein! Irgendwas ist beim Upload schiefgelaufen :(');
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

.form-field-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.form-field-wrapper > label {
  position: absolute;
  left: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  color: grey;
  transition: all ease-in-out 0.15s;
  font-size: small;
}

.form-field-wrapper > input:focus + label,
.form-field-wrapper > input:not(:placeholder-shown) + label {
	top: -25%;
	transform: translateY(0);
	background-color: white;
	padding: 0 0.5rem;
  font-size: x-small;
}

.form-field-wrapper > input::placeholder {
  color: transparent;
}

.form-field-wrapper > input {
  width: 100%;
  height: 100%;
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
  grid-template-columns: 3rem 5rem auto;
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