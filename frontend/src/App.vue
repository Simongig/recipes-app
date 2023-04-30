<template>
  <div class="app-container">
    <div>
      <navbar></navbar>
      <router-view></router-view>
      <Footer></Footer>
    </div>
  </div>
</template>

<script>
import Navbar from "./components/Navbar.vue";
import Footer from "./components/Footer.vue";
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
    Footer,
  },
};
</script>

<style>
:root {
  --main-color: #212e2f;
  --secondary-color: #c3a452;
  --tertiary-color: #fdfdfa;
  --dark-grey: #333333;
  --light-grey: #E3E3E3;
  --light-font: #fdfdfa;
}

html {
  box-sizing: border-box;
  background-color: var(--tertiary-color);
}

*:visited,
*:active,
a,
ion-icon {
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
  max-width: 1300px;
  height: 100%;
  margin: 10vw auto;
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

@media (max-width: 1300px) {
  main {
    margin-left: 10vw;
    margin-right: 10vw;
  }
}

@media (max-width: 550px) {
  main {
    margin-left: 5vw;
    margin-right: 5vw;
  }
}
</style>
