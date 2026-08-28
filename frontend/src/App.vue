<template>
  <div class="app-container">
    <div>
      <navbar></navbar>
      <div v-for="alert in alertStore.alerts" :key="alert.id" class="alerts-container">
        <Alert ref="alert" class="border-green-700 bg-green-100 fixed top-[15vh] left-1/2 -translate-x-1/2 w-fit z-50"
          v-if="alert.title">
          <CheckCircle2Icon />
          <AlertTitle>{{ alert.title }}</AlertTitle>
          <AlertDescription>{{ alert.message }}</AlertDescription>
        </Alert>
      </div>
      <router-view></router-view>
      <Footer></Footer>
    </div>
  </div>
</template>

<script>
import { useAuthStore} from '@/stores/authStore'
import { useAlertStore} from '@/stores/alertStore'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'
import { CheckCircle2Icon } from '@lucide/vue'
import { Alert, AlertTitle, AlertDescription } from "@/components/ui/alert"


export default {
  name: 'App',
  components: { Alert, AlertTitle, AlertDescription, CheckCircle2Icon, Navbar, Footer },
  setup(){
    const authStore = useAuthStore();
    const alertStore = useAlertStore();
    return { authStore, alertStore }
  },
  mounted() {
    var user_data = localStorage.getItem('access_token');
    if (null != user_data) {
      this.authStore.setToLoggedIn();
    }
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

.app-container > div > main{
  margin-top: 10rem;
}
@media (max-width: 550px) {
  main {
    margin-left: 5vw;
    margin-right: 5vw;
  }
}
</style>
