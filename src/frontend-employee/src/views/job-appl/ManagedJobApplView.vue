<script setup>
import { getManagedJobApplById, updateManagedJobAppl } from '@/services/jobApplService';
import { onMounted, ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';

const id = useRoute().params.id
const stateSelection = ref(['INTERVIEW_SCHEDULED', 'OFFERED', 'ACCEPTED', 'REJECTED'])

const jobApplication = ref({
  id: 0,
  submitted: new Date(),
  status: '',
  jobTitle: '',
  jobDesc: '',
  candidateId: 0,
})
const selectedStatus = ref('')
const successMessage = ref('')
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getManagedJobApplById(id)
    const dto = response.data.data
    jobApplication.value.id = dto.id
    jobApplication.value.submitted = new Date(dto.dateOfSubmission)
    jobApplication.value.status = dto.status
    jobApplication.value.jobTitle = dto.jobPosting.title
    jobApplication.value.jobDesc = dto.jobPosting.description
    jobApplication.value.candidateId = dto.candidateId
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed getting the list of applications you manage'
    setErrorMessage(message)
  }
})

const updateApplicationStatus = async () => {
  window.scroll({ top: 0, behavior: 'smooth' })
  try {
    const request = { status: selectedStatus.value }
    const response = await updateManagedJobAppl(jobApplication.value.id, request)
    jobApplication.value.status = selectedStatus.value
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed updating application\'s status'
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
  <h1>Application #{{ jobApplication.id }} | <i>submitted: {{ jobApplication.submitted.toLocaleDateString() }}</i></h1>
  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  <hr>

  <h2>Job Posting Information</h2>
  <p><b>Title: </b>{{ jobApplication.jobTitle }}</p>
  <p><b>Description: </b>{{ jobApplication.jobDesc }}</p>
  <hr>

  <RouterLink :to="`/managed/${jobApplication.id}/candidate`">Candidate Profile</RouterLink>
  <hr>

  <p><b>Application status: </b>{{ jobApplication.status }}</p>
  <select v-model="selectedStatus">
    <option v-for="state in stateSelection" :value="state">
      {{ state }}
    </option>
  </select>
  <button type="button" @click="updateApplicationStatus"
    :disabled="selectedStatus === '' || selectedStatus === jobApplication.status">Update status</button>
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
