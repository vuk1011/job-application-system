<script setup>
import router from '@/router';
import { login } from '@/services/authService';
import { ref } from 'vue';

const errorMessage = ref('')

const formData = ref({
  'email': '',
  'password': '',
})

const handleLogin = async () => {
  if (!isValid()) {
    setErrorMessage('Invalid form input')
    return
  }
  try {
    const response = await login(formData.value)
    let jwt = response.data.data.jwt
    let firstName = response.data.data.firstName

    router.push({ path: '/', query: { name: firstName } })
    localStorage.setItem('jwt', jwt)
  } catch (error) {
    let message = error.response?.data?.message || error.message || 'Login failed'
    setErrorMessage(message)
  }
}

const isValid = () => {
  if (formData.value.email.trim() === '') return false
  if (formData.value.password.trim() === '') return false
  return true
}

const setErrorMessage = (message) => {
  errorMessage.value = message
}
</script>

<template>
  <h1>Login as an Employee</h1>
  <form @submit.prevent="handleLogin">
    <label for="email">Email</label>
    <input type="text" id="email" v-model="formData.email" maxlength="50">
    <br>
    <label for="password">Password</label>
    <input type="password" id="password" v-model="formData.password" maxlength="30">
    <br>
    <button type="submit">Login</button>
  </form>

  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
</template>

<style scoped>
.error-message {
  color: red;
  font-weight: bold;
}
</style>
