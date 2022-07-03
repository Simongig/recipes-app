<template>
  <nav>
    <div class="nav-brand">
      <router-link to="/">Home</router-link>
    </div>
    <div class="nav-links">
      <router-link to="/recipe/all">All Recipes</router-link>
      <router-link v-if="this.$store.state.isLoggedIn" to="/createRecipe">Create</router-link>
      <router-link to="/login">Login</router-link>
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
  border-bottom: 1px solid var(--main-color);
  position: fixed;
  top: 0;
  width: 100%;
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
</style>