<template>
  <section>
    <form class="boxshadow login-form" ref="loginForm" id="login_form" action="/login" method="post">
      <h2>Anmeldung</h2>
      <input type="text" name="username" placeholder="Benutzername" id="" />
       <input type="password" name="password" placeholder="Passwort" id="" />
      <input type="submit" @click="submitForm" value="Anmelden" />
    </form>
  </section>
</template>

<script setup lang="ts">
import axios from 'axios'
import router from '../router'
import { useAuthStore } from '@/stores/authStore'
import { useAlertStore } from '@/stores/alertStore'
import { onMounted, ref } from 'vue'


const name = 'login-form'
const authStore = useAuthStore()
const alertStore = useAlertStore()

const loginForm = ref<HTMLFormElement | null>(null)

function submitForm(ev: Event) {
  ev.preventDefault()

  const params = new URLSearchParams()
  const form = loginForm.value
  if (!form) {
    alertStore.addAlert({ title: 'Fehler', message: 'Ein Fehler ist aufgetreten. Bitte versuche es erneut', variant: 'destructive' })
    return
  }
  const formData = new FormData(form)
  params.append('username', String(formData.get('username') ?? ''))
  params.append('password', String(formData.get('password') ?? ''))
  axios
    .post('/api/v1/auth/login', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
    .then((response) => {
      if (200 != response.status) {
        alertStore.addAlert({ title: 'Fehler', message: 'Oh nein! Irgendwas ist beim Login schiefgelaufen :( \n Code: ' + response.status, variant: 'destructive' })
        return;
      }
      localStorage.setItem('access_token', response.data.access_token)
      localStorage.setItem('refresh_token', response.data.refresh_token)
      if (response.data.access_token) {
        alertStore.addAlert({ title: 'Du hast dich erfolgreich eingeloggt', variant: 'success' })
        authStore.setToLoggedIn();
      }
      router.push({ path: '/' })
    })
    .catch((e) => {
      alertStore.addAlert({ title: 'Fehler', message: 'Oh nein! Irgendwas ist beim Login schiefgelaufen :(', variant: 'destructive' })
      console.error(e)
    })
}
</script>

<style>
.login-form {
  display: grid;
  grid-auto-rows: minmax(2rem, auto);
  width: min(90vw, 400px);
  margin: auto auto;
  gap: 10px;
  border: 1px solid rgba(100, 100, 111, 0.2);
  padding: 20px;
  border-radius: 10px;
}
</style>
