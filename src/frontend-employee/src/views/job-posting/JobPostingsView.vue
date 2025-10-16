<script setup>
import JobPosting from '@/components/JobPosting.vue';
import router from '@/router';
import { getJobPostings } from '@/services/jobPostingService';
import { onMounted, ref } from 'vue';

const jobs = ref([])
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getJobPostings()
    jobs.value = response.data.data.map(dto => ({
      id: dto.id,
      title: dto.title,
      published: new Date(dto.dateOfPublishing),
      status: dto.status,
    }))
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed updating application\'s status'
    setErrorMessage(message)
  }
})

const openJobPosting = (id) => {
  router.push(`/job-postings/${id}`)
}

const setErrorMessage = (message) => {
  errorMessage.value = message
}
</script>

<template>
  <h1>Job Postings</h1>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  <hr>

  <button type="button" @click="router.push('/job-postings/create')">Create New</button>
  <br>

  <JobPosting v-for="job in jobs" :key="job.id" :id="job.id" :title="job.title" :published="job.published"
    :status="job.status" @open-job-posting="id => openJobPosting(id)" />
</template>

<style scoped>
.error-message {
  color: red;
  font-weight: bold;
}
</style>
