<script setup>
import JobAppl from '@/components/JobAppl.vue';
import router from '@/router';
import { getJobAppls } from '@/services/jobApplService';
import { onMounted, ref } from 'vue';

const jobAppls = ref([])
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getJobAppls()
    jobAppls.value = response.data.data.map(appl => ({
      id: appl.id,
      title: appl.jobPosting.title,
      submitted: appl.dateOfSubmission,
    }))
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed loading job applications'
    setErrorMessage(message)
  }
})

const openApplication = (id) => {
  router.push(`job-applications/${id}`)
}

const setErrorMessage = (message) => {
  successMessage.value = ''
  errorMessage.value = message
}
</script>

<template>
  <h1>Your Job Applications</h1>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

  <JobAppl v-for="appl in jobAppls" :key="appl.id" :id="appl.id" :title="appl.title"
    :submitted="new Date(appl.submitted)" @open-application="id => openApplication(id)" />
</template>

<style scoped>
.error-message {
  color: red;
  font-weight: bold;
}
</style>
