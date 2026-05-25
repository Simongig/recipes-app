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
import { useAuthStore } from '@/stores/authStore'


export default {
  name: 'login-form',
  setup() {
    const authStore = useAuthStore()
    return {authStore}
  },
  mounted() {
    document.querySelector('#login-form').addEventListener('submit', (e) => {
      e.preventDefault()
    })
  },
  methods: {
    
    submitForm() {
      const params = new URLSearchParams()
      const form = document.querySelector('#login-form')
      const formData = new FormData(form)
      params.append('username', formData.get('username'))
      params.append('password', formData.get('password'))
      axios
        .post('/api/v1/auth/login', params, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        })
        .then((response) => {
          if (200 != response.status) {
            alert('Oh nein! Irgendwas ist beim Login schiefgelaufen :( \n Code: ' + response.status)
            return;
          } 
          alert('Super! Dein Login war erfolgreich!')
          localStorage.setItem('access_token', response.data.access_token)
          localStorage.setItem('refresh_token', response.data.refresh_token)
          if (response.data.access_token) {
            this.authStore.setToLoggedIn();
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
