<template>
    <form
    class="boxshadow login-form"
    action="/loginUser"
    method="post"
  >
    <h2>Anmeldung</h2>
    <input type="email" name="email" placeholder="Email" id="">
    <input type="password" placeholder="Passwort" name="" id="">
    <input type="submit" value="Anmelden" />
  </form>
</template>

<script>
export default {
    name: "login-form"
}
</script>

<style>
.login-form {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  max-width: 40rem;
  min-width: 320px;
  margin: auto auto;
  gap: 10px;
  border: 1px solid rgba(100, 100, 111, 0.2);
  padding: 20px;
  border-radius: 10px;
}

</style>