<script setup>
import UnmanagedJobAppl from '@/components/UnmanagedJobAppl.vue';
import { addJobApplToManaged, getUnmanagedJobApplsByJobPostingId } from '@/services/jobApplService';
import { getJobPostings } from '@/services/jobPostingService';
import { onMounted, ref } from 'vue';

const selectedJobId = ref(null)
const jobPostings = ref([])
const jobApplications = ref([])
const successMessage = ref('')
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getJobPostings()
    jobPostings.value = response.data.data.map(job => ({
      id: job.id,
      title: job.title,
      status: job.status,
    }))
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed loading job postings'
    setErrorMessage(message)
  }
})

const searchApplications = async () => {
  if (!selectedJobId) return
  jobApplications.value = []
  try {
    const response = await getUnmanagedJobApplsByJobPostingId(selectedJobId.value)
    jobApplications.value = response.data.data.map(appl => ({
      id: appl.id,
      submitted: appl.dateOfSubmission,
    }))
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed loading job applications for selected job posting'
    setErrorMessage(message)
  }
}

const addToManaged = async (id) => {
  window.scroll({ top: 0, behavior: 'smooth' })
  const request = { applicationId: id }
  try {
    const response = await addJobApplToManaged(request)
    jobApplications.value = jobApplications.value.filter(appl => appl.id !== id)
    setSuccessMessage(response.data.message)
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed adding job application to the managed list'
    setErrorMessage(message)
  }
}

const setSuccessMessage = (message) => {
  errorMessage.value = ''
  successMessage.value = message
}

const setErrorMessage = (message) => {
  successMessage.value = ''
  errorMessage.value = message
}
</script>

<template>
  <div>
    <h1>Search job applications by job posting</h1>

    <select v-model="selectedJobId">
      <option v-for="job in jobPostings" :key="job.id" :value="job.id">
        {{ `${job.title} (${job.status})` }}
      </option>
    </select>
    <button type="button" @click="searchApplications">Search</button>

    <hr>
    <div class="error-container">
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>

    <UnmanagedJobAppl v-for="appl in jobApplications" :key="appl.id" :id="appl.id" :submitted="new Date(appl.submitted)"
      @manage-application="id => addToManaged(id)" />
  </div>
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
  margin-left: 15px;
}
</style>
