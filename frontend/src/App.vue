<template>
  <div class="app-container">
    <div>
      <navbar></navbar>
      <div class="alerts-container fixed top-[15vh] w-full flex flex-col gap-4 justify-center items-center">
        <Alert ref="alert" v-for="alert in alertStore.alerts" :key="alert.id" class="mx-auto w-fit z-50"
          :variant="alert.variant">
          <CheckCircle2Icon v-if="alert.variant === 'success'" />
          <CircleAlertIcon v-if="alert.variant === 'destructive'" />
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
import { useAuthStore } from '@/stores/authStore'
import { useAlertStore } from '@/stores/alertStore'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'
import { CheckCircle2Icon, CircleAlertIcon } from '@lucide/vue'
import { Alert, AlertTitle, AlertDescription } from "@/components/ui/alert"


export default {
  name: 'App',
  components: { Alert, AlertTitle, AlertDescription, CheckCircle2Icon, CircleAlertIcon, Navbar, Footer },
  setup() {
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

.app-container>div {
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

.app-container>div>main {
  margin-top: 10rem;
}

@media (max-width: 550px) {
  main {
    margin-left: 5vw;
    margin-right: 5vw;
  }
}
</style>
