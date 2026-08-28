<template>
  <nav class="header-nav">
    <div class="nav-inner">
      <div class="nav-brand">
        <router-link class="nav-item home-link" to="/" @click="closeNav">Kochbuch.io<sup
            style="font-size: x-small; font-weight: normal; margin-left: 0.25rem">beta</sup></router-link>
      </div>
      <div class="nav-main-container">
        <div class="nav-links">
          <router-link class="nav-item" to="/recipe/all" @click="closeNav">Alle Rezepte</router-link>
          <!-- <router-link class="nav-item" to="/recipe/categories" @click="closeNav">Kategorien</router-link> -->
          <div disabled class="nav-item color-gray cursor-not-allowed">Kategorien</div>
          <router-link class="nav-item" to="/mealplans" @click="closeNav">Meal Planer</router-link>
          <router-link class="nav-item" v-if="isLoggedIn" to="/createRecipe" @click="closeNav">
            Erstellen
          </router-link>
        </div>
        <div class="nav-extra-items">
          <router-link class="nav-profile" v-if="isLoggedIn" to="/profile" @click="closeNav">
            <UserRound class="w-7 h-7" />
          </router-link>

          <router-link class="nav-login" v-else to="/login" @click="closeNav">
            <UserRoundPlus class="w-7 h-7" />
          </router-link>
        </div>
      </div>
      <div class="nav-mobile-menu nav-item">
        <button><X class="nav-close-icon w-6 h-6" @click="toggleNav"></X></button>
        <button><Menu class="nav-menu-icon w-6 h-6" @click="toggleNav"></Menu></button>
      </div>
    </div>
  </nav>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import {  UserRoundPlus, UserRound, X, Menu } from '@lucide/vue'


export default {
  name: 'Navbar',
  components: { UserRoundPlus, UserRound, X, Menu },
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  computed: {
    isLoggedIn() { return this.authStore.isLoggedIn }
  },
  methods: {
    toggleNav(event) {
      document.body.classList.toggle('nav-mobile-show')
    },
    closeNav() {
      document.body.classList.remove('nav-mobile-show')
    },
  },
}
</script>

<style>
.header-nav {
  padding: 1rem 0;
  /* background-color: #DAD0CE; */
  /* background-color: #323232; */
  background-color: rgba(255, 255, 255, 0.658);
  position: fixed;
  top: 0;
  width: 100%;
  margin-bottom: 3rem;
  box-shadow: 0px 0px 22px -2px #cecece;
  z-index: 100;
  backdrop-filter: blur(20px);
}

.header-nav .nav-inner {
  text-align: center;
  display: grid;
  grid-template-columns: auto 1fr auto;
  width: auto;
  margin-left: 10vw;
  margin-right: 10vw;
}

.header-nav a.router-link-exact-active:not(.home-link),
.header-nav a:hover {
  color: var(--secondary-color);
}

.nav-main-container {
  display: flex;
  justify-content: space-between;
}

.home-link {
  margin-right: 2rem;
  font-size: 2rem;
}

.nav-brand {
  font-weight: bold;
  display: flex;
  align-items: flex-start;
}

.nav-links,
.nav-login,
.nav-profile,
.nav-extra-items {
  display: flex;
  align-items: center;
}

.nav-item {
  padding: 0.5rem 0.5rem 0.5rem 0;
  text-decoration: none;
}

.nav-mobile-menu {
  display: none;
  align-items: center;
}

.nav-close-icon {
  display: none;
}

.nav-mobile-show .nav-close-icon {
  display: initial;
}

.nav-mobile-show .nav-menu-icon {
  display: none;
}

@media (max-width: 768px) {
  .nav-mobile-menu {
    display: flex;
    justify-content: flex-end;
  }

  .home-link {
    font-size: 1.5rem;
  }

  .nav-profile {
    background-color: transparent;
  }

  .nav-profile::after {
    content: 'Profile';
    margin-left: 0.5rem;
  }

  .nav-main-container {
    display: none;
  }

  .nav-mobile-show .nav-main-container {
    grid-area: main;
  }

  .nav-mobile-show .nav-brand {
    grid-area: brand;
    text-align: left;
  }

  .nav-mobile-show .nav-mobile-menu {
    grid-area: menu;
  }

  body.nav-mobile-show {
    overflow: hidden;
    position: fixed;
    width: 100%;
    height: 100%;
  }

  .nav-mobile-show nav,
  .nav-mobile-show .nav-inner {
    height: 100vh;
  }

  .nav-mobile-show .nav-inner {
    grid-template-areas: 'brand menu' 'main main';
    grid-template-columns: 1fr auto;
    grid-template-rows: auto 1fr;
  }

  .nav-mobile-show .nav-main-container {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin: 2rem 0 3rem 0;
  }

  .nav-links>.nav-item {
    font-size: 1.7rem;
    font-weight: light;
    padding-top: 1.2rem;
    padding-bottom: 1.2rem;
  }
  .nav-mobile-show .nav-links {
    flex-direction: column;
    align-items: flex-start;
  }

  .nav-mobile-show .nav-extra-items {
    width: 100%;
    border-top: 1px solid var(--dark-grey);
    padding-top: 0.8rem;
  }
}

@media (max-width: 550px) {
  .header-nav .nav-inner {
    margin-left: 5vw;
    margin-right: 5vw;
    width: 90vw;
  }
}

@media (min-width: 1820px) {
  .header-nav .nav-inner {
    width: 1600px;
    padding-left: 160px;
    padding-right: 160px;
    margin-left: auto;
    margin-right: auto;
  }
}
</style>
