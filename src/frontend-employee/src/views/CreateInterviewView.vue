<script setup>
import { createInterview } from '@/services/interviewService';
import { ref } from 'vue';
import { useRoute } from 'vue-router';

const interview = ref({
  title: '',
  description: '',
  timeScheduled: '',
  jobApplicationId: useRoute().params.id,
})
const successMessage = ref('')
const errorMessage = ref('')

const isValid = () => {
  if (interview.value.title.trim() === '') return false
  if (interview.value.description.trim() === '') return false
  if (interview.value.timeScheduled === '') return false
  if (new Date(interview.value.expires).getTime < new Date().getTime()) return false
  return true
}

const handleCreate = async () => {
  if (!isValid()) {
    setErrorMessage('Invalid input')
    return
  }
  try {
    const response = await createInterview(interview.value)
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed creating the interview'
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
  <h1>Create a New Interview</h1>
  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

  <form @submit.prevent="handleCreate">
    <div class="form-row">
      <label for="title"><b>Title:</b></label>
      <input id="title" type="text" v-model="interview.title" maxlength="50">
    </div>

    <div class="form-row">
      <label for="description"><b>Description:</b></label>
      <textarea id="description" name="description" rows="6" cols="50" maxlength="3000"
        v-model="interview.description"></textarea>
    </div>

    <div class="form-row">
      <label for="expires"><b>Expires:</b></label>
      <input id="expires" type="datetime-local" v-model="interview.timeScheduled">
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
