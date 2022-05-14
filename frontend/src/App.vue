<template>
  <div class="app-container" >
    <div v-if="this.testCookie">
    <navbar></navbar>
    <router-view></router-view></div>
    <div v-else>
      <h1>This site is under construction</h1>
    </div>
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
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /* color: #2c3e50; */
  color: #1b2625;
}

.app-container > div > *:nth-child(2) {
  margin-top: 10rem;
}
</style>
