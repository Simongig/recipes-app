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
import { useAuthStore } from '@/stores/authStore'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'


export default {
  name: 'App',
  data() {
    return {
    }
  },
  setup(){
    const authStore = useAuthStore();
    return { authStore }
  },
  mounted() {
    var user_data = localStorage.getItem('access_token');
    if (null != user_data) {
      this.authStore.setToLoggedIn();
    }
  },
  components: {
    Navbar,
    Footer,
  },
}
</script>

<style>
:root {
  --main-color: #212e2f;
  --secondary-color: #c3a452;
  --tertiary-color: #fdfdfa;
  --dark-grey: #333333;
  --light-grey: #e3e3e3;
  --light-font: #fdfdfa;
}

html {
  box-sizing: border-box;
  background-color: var(--tertiary-color);
}

*,
*:before,
*:after {
  box-sizing: inherit;
  font-family: Avenir, Helvetica, Arial, sans-serif;
}

.app-container > div {
	min-height: 100vh;
	display: flex;
	flex-direction: column;
  justify-content: space-between;
}

main {
  width: auto;
  margin-left: 10vw;
  margin-right: 10vw;
  height: 100%;
}

@media (min-width: 1820px) {
  main {
    width: 1600px;
    margin-left: auto;
    margin-right: auto;
    padding-left: 160px;
    padding-right: 160px;
  }
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
@media (max-width: 550px) {
  main {
    margin-left: 5vw;
    margin-right: 5vw;
  }
}
</style>
