<template>
  <nav>
    <div class="nav-brand">
      <router-link to="/">Home</router-link>
    </div>
    <div class="nav-links">
      <router-link to="/recipe/all">Rezepte</router-link>
      <router-link v-if="this.$store.state.isLoggedIn" to="/createRecipe"
        >Erstellen</router-link
      >
      <router-link v-if="this.$store.state.isLoggedIn" to="/profile"
        >Profil</router-link
      >
      <router-link v-else to="/login">Login</router-link>
    </div>
  </nav>
</template>

<script>
export default {
  name: "Navbar",
};
</script>

<style>
nav {
  padding: 30px;
  text-align: center;
  display: grid;
  grid-template-columns: 1fr 1fr;
  /* background-color: #DAD0CE; */
  background-color: white;
  margin-bottom: 3rem;
  position: fixed;
  top: 0;
  width: 100%;
  box-shadow: 0px 0px 22px -2px #cecece;
}

nav a {
  font-weight: bold;
  text-decoration: none;
  color: var(--main-color);
}
nav a.router-link-exact-active {
  color: var(--secondary-color);
}

nav a:hover {
  color: var(--secondary-color);
}

.nav-brand {
  display: flex;
  align-items: flex-start;
}

.nav-links {
  display: flex;
  justify-content: space-around;
}

.nav-links > * + * {
  padding-left: 0.5rem;
}
</style>