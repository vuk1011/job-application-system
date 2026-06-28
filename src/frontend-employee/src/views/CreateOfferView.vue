<script setup>
import { createOffer } from '@/services/offerService';
import { ref } from 'vue';
import { useRoute } from 'vue-router';

const offer = ref({
  name: '',
  jobApplicationId: useRoute().params.id,
})
const successMessage = ref('')
const errorMessage = ref('')

const isValid = () => {
  if (offer.value.name.trim() === '') return false
  return true
}

const handleCreate = async () => {
  if (!isValid()) {
    setErrorMessage('Invalid input')
    return
  }
  try {
    const response = await createOffer(offer.value)
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed creating the offer'
    setErrorMessage(message)
  }
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
  <h1>Create a New Offer</h1>
  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

  <form @submit.prevent="handleCreate">
    <div class="form-row">
      <label for="name"><b>Name:</b></label>
      <input id="name" type="text" v-model="offer.name" maxlength="50">
    </div>

    <button type="submit">Create</button>

  </form>
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

button {
  margin-top: 20px;
  font-size: 16px;
  padding: 5px;
}
</style>
