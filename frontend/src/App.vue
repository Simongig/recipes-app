<template>
  <div class="app-container">
    <div>
      <navbar></navbar>
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import Navbar from "./components/Navbar.vue";
// const axios = require("axios");

export default {
  name: "App",
  data() {
    return {
      testCookie: -1 < document.cookie.indexOf("testCookie=true"),
    };
  },
  mounted() {
    var user_data = JSON.parse(localStorage.getItem("user"));
    if (null != user_data) {
      this.$store.commit("setToLoggedIn");
      console.log(this.$store.state.isLoggedIn);
    }
  },
  components: {
    Navbar,
  },
};
</script>

<style>
:root {
  --main-color: #223240;
  --secondary-color: #8c452b;
  --tertiary-color: #73614c;
  --dark-grey: #323232;
  --light-font: #F4F4F4;
}

html {
  box-sizing: border-box;
}

*:visited, *:active, a, ion-icon {
  color: var(--dark-grey);
}

* {
  padding: 0;
  margin: 0;
}

*,
*:before,
*:after {
  box-sizing: inherit;
  font-family: Avenir, Helvetica, Arial, sans-serif;
}

h1,
h2 {
  padding-bottom: 1rem;
}

main {
  width: auto;
  height: 100%;
  margin: 10vw;
}

#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--dark-grey);
}

.construction {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.app-container > div > *:nth-child(2) {
  margin-top: 10rem;
}

ion-icon {
  font-size: 1.5rem;
}
</style>
