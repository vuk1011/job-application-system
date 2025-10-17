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

    router.push('/')
    localStorage.setItem('jwt', jwt)
    localStorage.setItem('firstName', firstName)
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
  <div class="form-box">
    <h1>Login as an Employee</h1>
    <br>
    <form @submit.prevent="handleLogin">
      <div class="form-row">
        <label for="email">Email</label>
        <input type="text" id="email" v-model="formData.email" maxlength="50">
      </div>

      <div class="form-row">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="formData.password" maxlength="30">
      </div>

      <button type="submit">Login</button>
    </form>

    <div class="error-container">
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<style scoped>
.error-message {
  color: red;
  font-weight: bold;
}

.form-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  width: 100%;
}

button {
  margin-top: 15px;
  width: 50%;
}

input {
  width: 60%;
}
</style>
