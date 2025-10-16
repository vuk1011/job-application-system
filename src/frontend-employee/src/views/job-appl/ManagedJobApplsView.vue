<script setup>
import ManagedJobAppl from '@/components/ManagedJobAppl.vue';
import { getManagedJobAppls } from '@/services/jobApplService';
import { onMounted, ref } from 'vue';
import router from '@/router';

const jobApplications = ref([])
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getManagedJobAppls()
    jobApplications.value = response.data.data.map(appl => ({
      id: appl.id,
      submitted: appl.dateOfSubmission,
      status: appl.status,
      job: appl.jobPosting.title,
    }))
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed getting the list of applications you manage'
    setErrorMessage(message)
  }
})

const openApplication = (id) => {
  router.push(`/managed/${id}`)
}

const setErrorMessage = (message) => {
  successMessage.value = ''
  errorMessage.value = message
}
</script>

<template>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

  <ManagedJobAppl v-for="appl in jobApplications" :key="appl.id" :id="appl.id" :submitted="new Date(appl.submitted)"
    :status="appl.status" :job="appl.job" @open-application="id => openApplication(id)" />
</template>

<style scoped>
.error-message {
  color: red;
  font-weight: bold;
}
</style>
