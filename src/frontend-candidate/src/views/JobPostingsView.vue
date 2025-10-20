<script setup>
import JobPosting from '@/components/JobPosting.vue';
import { submitJobAppl } from '@/services/jobApplService';
import { getJobPostings } from '@/services/jobPostingService';
import { onMounted, ref } from 'vue';

const jobPostings = ref([])
const successMessage = ref('')
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getJobPostings()
    jobPostings.value = response.data.data.map(jobDto => ({
      id: jobDto.id,
      title: jobDto.title,
      description: jobDto.description,
      expires: jobDto.dateOfExpiration,
    }))
  } catch (_) {
    setErrorMessage('Failed loading job postings')
  }
})

const submitApplication = async (jobId) => {
  window.scroll({ top: 0, behavior: 'smooth' })
  const request = { jobPostingId: jobId }
  try {
    const response = await submitJobAppl(request)
    setSuccessMessage(response.data.message)
  } catch (_) {
    setErrorMessage('Failed submitting application')
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
  <h1>Published Job Postings</h1>
  <div class="error-container">
    <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
  <JobPosting v-for="job in jobPostings" :key="job.id" :id="job.id" :title="job.title" :description="job.description"
    :expires="new Date(job.expires)" @apply="id => submitApplication(id)" />
</template>

<style scoped>
h1 {
  margin-left: 20px;
}

.error-container {
  margin-left: 20px;
}

.success-message {
  color: green;
  font-weight: bold;
}

.error-message {
  color: red;
  font-weight: bold;
}
</style>
