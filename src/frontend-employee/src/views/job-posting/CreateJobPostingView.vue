<script setup>
import { createJobPosting } from '@/services/jobPostingService';
import { ref } from 'vue';

const job = ref({
  title: '',
  desc: '',
  expires: '',
})
const successMessage = ref('')
const errorMessage = ref('')

const isValid = () => {
  if (job.value.title.trim() === '') return false
  if (job.value.desc.trim() === '') return false
  if (job.value.expires === '') return false
  if (new Date(job.value.expires).getTime < new Date().getTime()) return false
  return true
}

const handleCreate = async () => {
  if (!isValid()) {
    setErrorMessage('Invalid input')
    return
  }
  try {
    const request = { title: job.value.title, description: job.value.desc, dateOfExpiration: job.value.expires }
    const response = await createJobPosting(request)
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed to create job posting'
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
  <h1>Create a New Job Posting</h1>
  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  <hr>

  <form @submit.prevent="handleCreate">
    <label for="title"><b>Title:</b></label>
    <input id="title" type="text" v-model="job.title" maxlength="50">
    <br>

    <label for="desc"><b>Description:</b></label>
    <textarea id="desc" name="description" rows="6" cols="50" maxlength="3000" v-model="job.desc"></textarea>
    <br>

    <label for="expires"><b>Expires:</b></label>
    <input id="expires" type="date" v-model="job.expires">
    <br>

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
</style>
