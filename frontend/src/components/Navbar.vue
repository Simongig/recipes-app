<template>
  <nav>
    <div class="nav-brand">
      <router-link class="nav-item home-link" to="/">Kochbuch.io</router-link>
    </div>
    <div class="nav-main-container">
      <div class="nav-links">
        <router-link class="nav-item" to="/recipe/all"
          >Alle Rezepte</router-link
        >
        <router-link class="nav-item" to="/recipe/categories"
          >Kategorien</router-link
        >
        <router-link
          class="nav-item"
          v-if="this.$store.state.isLoggedIn"
          to="/createRecipe"
          >Erstellen</router-link
        >
      </div>
      <div class="nav-extra-items">
        <router-link
          class="nav-item nav-profile"
          v-if="this.$store.state.isLoggedIn"
          to="/profile"
          ><ion-icon name="person-outline"></ion-icon
        ></router-link>

        <router-link class="nav-item nav-login" v-else to="/login"
          ><ion-icon name="person-add-outline"></ion-icon
        ></router-link>
      </div>
    </div>
    <div class="nav-mobile-menu nav-item" @click="openNavMenu">
      <ion-icon name="menu-outline"></ion-icon>
    </div>
  </nav>
</template>

<script>
export default {
  name: "Navbar",
  methods: {
    openNavMenu(event) {
      console.log(event);
      let nav_container = event.target.closest("body");
      nav_container.classList.toggle("nav-mobile-show");
    },
  },
};
</script>

<style>
nav {
  padding: 1rem;
  text-align: center;
  display: grid;
  grid-template-columns: auto 1fr auto;
  /* background-color: #DAD0CE; */
  /* background-color: #323232; */
  background-color: rgba(255, 255, 255, 0.658);
  margin-bottom: 3rem;
  position: fixed;
  top: 0;
  width: 100%;
  box-shadow: 0px 0px 22px -2px #cecece;
  z-index: 100;
  color: white;
  backdrop-filter: blur(20px);
}

nav a.router-link-exact-active:not(.home-link),
nav a:hover {
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
}

.nav-links,
.nav-login,
.nav-profile {
  display: flex;
  align-items: center;
}

.nav-mobile-menu {
  display: none;
  align-items: center;
}

.nav-item {
  padding: 0.5rem;
  text-decoration: none;
  cursor: pointer;
}

@media (max-width: 768px) {
  .nav-mobile-menu {
    display: flex;
    justify-content: flex-end;
  }

  .nav-main-container {
    display: none;
  }

  .nav-mobile-show .nav-main-container {
    grid-area: main;
  }

  .nav-mobile-show .nav-brand {
    grid-area: brand;
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

  .nav-mobile-show nav {
    height: 100vh;
    grid-template-areas: "brand menu" "main main";
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto 1fr;
  }
  .nav-mobile-show .nav-main-container {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin: 2rem 0 3rem 0;
  }

  .nav-links > .nav-item {
    font-size: 1.7rem;
    font-weight: light;
    padding-top: 1.2rem;
    padding-bottom: 1.2rem;
  }

  .nav-links > .nav-item::after {
    content: "";
    background-image: url(/img/chevron-forward-outline.65e801bd.svg);
    height: 100%;
    display: inline-block;
    width: 20px;
    background-repeat: no-repeat;
    background-position: center;
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
</style>