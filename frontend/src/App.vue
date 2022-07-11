<template>
  <div class="app-container" >
    <div>
    <navbar></navbar>
    <router-view></router-view></div>
  </div>
</template>

<script>
import Navbar from "./components/Navbar.vue";

export default {
  name: "App",
  data() {
    return {
      testCookie: -1 < document.cookie.indexOf('testCookie=true')
    }
  },
  mounted () {
    if(document.location.search.indexOf('testCookie=true')) {
      document.cookie = 'testCookie=true'
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
  --secondary-color: #8C452B;
  --tertiary-color: #73614C;
}

#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /* color: #2c3e50; */
}

* {
  color: var(--main-color);
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
</style>
