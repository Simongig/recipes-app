<template>
  <nav class="header-nav">
    <div class="nav-inner" @click="closeNavOnLinkClick">
      <div class="nav-brand">
        <router-link class="nav-item home-link" to="/"
          >Kochbuch.io<sup
            style="
              font-size: x-small;
              font-weight: normal;
              margin-left: 0.25rem;
            "
            >beta</sup
          ></router-link
        >
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
      <div class="nav-mobile-menu nav-item" @click="toggleNav">
        <ion-icon
          class="nav-close-icon hydrated"
          name="close-outline"
        ></ion-icon>
        <ion-icon class="nav-menu-icon hydrated" name="menu-outline"></ion-icon>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: "Navbar",
  methods: {
    toggleNav(event) {
      let nav_container = event.target.closest("body");
      nav_container.classList.toggle("nav-mobile-show");
    },
    closeNavOnLinkClick(event) {
      console.log(event.target);
      if (event.target.closest(".nav-links .nav-item, .nav-brand") == null)
        return;
      let nav_container = event.target.closest("body");
      nav_container.classList.remove("nav-mobile-show");
    },
  },
};
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
  color: white;
  backdrop-filter: blur(20px);
}

.header-nav .nav-inner {
  text-align: center;
  display: grid;
  grid-template-columns: auto 1fr auto;
  max-width: 1300px;
  margin: auto;
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


.nav-profile {
  background-color: var(--light-grey);
  border-radius: 50%;
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

.nav-item {
  padding:  0.5rem 0.5rem 0.5rem 0;
  text-decoration: none;
  cursor: pointer;
}

@media (max-width: 1500px) {
  .header-nav .nav-inner {
    margin-left: 10vw;
    margin-right: 10vw;
    width: 80vw;
  }
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
    grid-template-areas: "brand menu" "main main";
    grid-template-columns: 1fr auto;
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

@media (max-width: 550px) {
  .header-nav .nav-inner {
    margin-left: 5vw;
    margin-right: 5vw;
    width: 90vw;
  }
}
</style>