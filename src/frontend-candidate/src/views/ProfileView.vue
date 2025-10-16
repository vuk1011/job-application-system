<script setup>
import { getProfile, updateProfile } from '@/services/profileService';
import { onMounted, ref } from 'vue';

const successMessage = ref('')
const errorMessage = ref('')

const profileInfo = ref({
  'id': 0,
  'firstName': '',
  'lastName': '',
  'sex': 'MALE',
  'phone': '',
  'address': '',
  'email': '',
})

onMounted(async () => {
  try {
    const response = await getProfile()
    profileInfo.value.id = response.data.data.id
    profileInfo.value.firstName = response.data.data.firstName
    profileInfo.value.lastName = response.data.data.lastName
    profileInfo.value.sex = response.data.data.sex
    profileInfo.value.phone = response.data.data.phone
    profileInfo.value.address = response.data.data.address
    profileInfo.value.email = response.data.data.email
  } catch (error) {
    let message = error.response?.data?.message || error.message || 'Registration failed'
    setErrorMessage(message)
  }
})

const handleUpdate = async () => {
  if (!isValid()) {
    setErrorMessage('Invalid form input')
    return
  }
  try {
    let request = {
      'firstName': profileInfo.value.firstName,
      'lastName': profileInfo.value.lastName,
      'sex': profileInfo.value.sex,
      'phone': profileInfo.value.phone,
      'address': profileInfo.value.address,
    }
    const response = await updateProfile(request)
    setSuccessMessage(response.data.message)
  } catch (error) {
    let message = error.response?.data?.message || error.message || 'Profile update failed'
    setErrorMessage(message)
  }
}

const isValid = () => {
  if (profileInfo.value.firstName.trim() === '') return false
  if (profileInfo.value.lastName.trim() === '') return false
  if (profileInfo.value.phone.trim() === '') return false
  if (profileInfo.value.address.trim() === '') return false
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
  <h1>Your Profile</h1>

  <label for="firstName">First name</label>
  <input type="text" id="firstName" v-model="profileInfo.firstName" maxlength="30">
  <br>

  <label for="lastName">Last name</label>
  <input type="text" id="lastName" v-model="profileInfo.lastName" maxlength="30">
  <br>

  <label for="phone">Phone</label>
  <input type="text" id="phone" v-model="profileInfo.phone" maxlength="16">
  <br>

  <label for="address">Address</label>
  <input type="text" id="address" v-model="profileInfo.address" maxlength="50">
  <br>

  <label>Sex</label>
  <input type="radio" name="sex" id="male" value="MALE" v-model="profileInfo.sex">
  <label for="male">M</label>
  <input type="radio" name="sex" id="female" value="FEMALE" v-model="profileInfo.sex">
  <label for="female">F</label>
  <br>

  <label for="email">Email</label>
  <input type="text" id="email" v-model="profileInfo.email" readonly>
  <br>

  <button type="button" @click="handleUpdate">Update Profile</button>

  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

  <br>
  <RouterLink to="/profile/resume">Resume</RouterLink>
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
