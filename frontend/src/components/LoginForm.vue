<template>
  <form class="boxshadow login-form" id="login-form" action="/login" method="post">
    <h2>Anmeldung</h2>
    <input type="text" name="username" placeholder="username" id="" />
    <input type="password" placeholder="Passwort" name="password" id="" />
    <input type="submit" @click="submitForm" value="Anmelden" />
  </form>
</template>

<script>
import axios from 'axios'
import router from '../router'
import { mapActions } from 'pinia'
import useStore from '../stores'

export default {
  name: 'login-form',
  mounted() {
    document.querySelector('#login-form').addEventListener('submit', (e) => {
      e.preventDefault()
    })
  },
  methods: {
    ...mapActions(useStore, ['setToLoggedIn']),
    submitForm() {
      const params = new URLSearchParams()
      const form = document.querySelector('#login-form')
      const formData = new FormData(form)
      console.log('username', formData.get('username'))
      console.log('password', formData.get('password'))
      params.append('username', formData.get('username'))
      params.append('password', formData.get('password'))
      axios
        .post('/api/auth/login', params, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        })
        .then((response) => {
          if (200 == response.status) {
            alert('Super! Dein Login war erfolgreich!')
          } else {
            alert('Oh nein! Irgendwas ist beim Login schiefgelaufen :( \n Code: ' + response.status)
          }
          localStorage.setItem('user', JSON.stringify(response.data))
          if (response.data.access_token) {
            this.setToLoggedIn()
          }
          router.push({ path: '/' })
        })
        .catch((e) => {
          alert('Oh nein! Irgendwas ist beim Login schiefgelaufen :(')
          console.log(e)
        })
    },
  },
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
