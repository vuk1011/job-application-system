<script setup>
import { deleteJobPosting, getJobPostingById, updateJobPosting } from '@/services/jobPostingService';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const job = ref({
  id: useRoute().params.id,
  title: '',
  desc: '',
  published: '',
  expires: '',
  status: '',
})
const successMessage = ref('')
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getJobPostingById(job.value.id)
    const data = response.data.data
    job.value.title = data.title
    job.value.desc = data.description
    job.value.published = data.dateOfPublishing
    job.value.expires = data.dateOfExpiration
    job.value.status = data.status
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed getting the list of applications you manage'
    setErrorMessage(message)
  }
})

const updateJob = async () => {
  if (!isValid()) {
    setErrorMessage('Invalid input')
    return
  }
  try {
    const request = { title: job.value.title, description: job.value.desc, dateOfExpiration: job.value.expires }
    const response = await updateJobPosting(job.value.id, request)
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed updating job posting'
    setErrorMessage(message)
  }
}

const deleteJob = async () => {
  try {
    const response = await deleteJobPosting(job.value.id)
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed deleting job posting'
    setErrorMessage(message)
  }
}

const isValid = () => {
  if (job.value.title.trim() === '') return false
  if (job.value.desc.trim() === '') return false
  if (new Date(job.value.expires).getTime < new Date().getTime()) return false
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
  <h1>Job Posting Details</h1>
  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  <hr>

  <label for="title"><b>Title:</b></label>
  <input id="title" type="text" v-model="job.title" maxlength="50">
  <br>

  <div>
    <label for="desc"><b>Description:</b></label>
    <textarea id="desc" name="description" rows="6" cols="50" maxlength="3000" v-model="job.desc"></textarea>
  </div>

  <label for="published"><b>Published:</b></label>
  <input id="published" type="date" v-model="job.published" disabled="true">
  <br>

  <label for="expires"><b>Expires:</b></label>
  <input id="expires" type="date" v-model="job.expires" :disabled="job.status === 'CLOSED'">
  <br>

  <label for="status"><b>Status:</b></label>
  <span>{{ job.status }}</span>
  <br>

  <button type="button" @click="updateJob">Update</button>
  <button type="button" @click="deleteJob">Delete</button>
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
