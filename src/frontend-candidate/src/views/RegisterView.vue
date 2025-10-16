<script setup>
import { register } from '@/services/authService';
import { ref } from 'vue';

const successMessage = ref('')
const errorMessage = ref('')

const formData = ref({
  'firstName': '',
  'lastName': '',
  'phone': '',
  'address': '',
  'sex': 'MALE',
  'email': '',
  'password': '',
})

const handleRegister = async () => {
  if (!isValid()) {
    setErrorMessage('Invalid form input')
    return
  }
  try {
    const response = await register(formData.value)
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Registration failed'
    setErrorMessage(message)
  }
}

const isValid = () => {
  if (formData.value.firstName.trim() === '') return false
  if (formData.value.lastName.trim() === '') return false
  if (formData.value.phone.trim() === '') return false
  if (formData.value.address.trim() === '') return false
  if (formData.value.email.trim() === '') return false
  if (formData.value.password.trim() === '') return false
  return true
}

const setErrorMessage = (message) => {
  successMessage.value = ''
  errorMessage.value = message
}

const setSuccessMessage = (message) => {
  errorMessage.value = ''
  successMessage.value = message
}
</script>

<template>
  <h1>Register as a Candidate</h1>
  <form @submit.prevent="handleRegister">
    <label for="firstName">First name</label>
    <input type="text" id="firstName" v-model="formData.firstName" maxlength="30">
    <br>

    <label for="lastName">Last name</label>
    <input type="text" id="lastName" v-model="formData.lastName" maxlength="30">
    <br>

    <label for="phone">Phone</label>
    <input type="text" id="phone" v-model="formData.phone" maxlength="16">
    <br>

    <label for="address">Address</label>
    <input type="text" id="address" v-model="formData.address" maxlength="50">
    <br>

    <label>Sex</label>
    <input type="radio" name="sex" id="male" value="MALE" v-model="formData.sex">
    <label for="male">M</label>
    <input type="radio" name="sex" id="female" value="FEMALE" v-model="formData.sex">
    <label for="female">F</label>
    <br>

    <label for="email">Email</label>
    <input type="text" id="email" v-model="formData.email" maxlength="50">
    <br>

    <label for="password">Password</label>
    <input type="password" id="password" v-model="formData.password" maxlength="30">
    <br>

    <button type="submit">Register</button>
  </form>

  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

  <RouterLink to="/login">Login</RouterLink>
</template>

<style scoped>
.success-message {
  color: green;
  font-weight: bold;
}

.error-message {
  color: red;
  font-weight: bold;
}
</style>
