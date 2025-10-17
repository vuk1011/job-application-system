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
  <div class="list-header">
    <h1>Job Postings</h1>
    <button type="button" @click="router.push('/job-postings/create')">Create New</button>
  </div>
  
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

  <JobPosting v-for="job in jobs" :key="job.id" :id="job.id" :title="job.title" :published="job.published"
    :status="job.status" @open-job-posting="id => openJobPosting(id)" />
</template>

<style scoped>
.error-message {
  color: red;
  font-weight: bold;
}

button {
  font-size: 14px;
  padding: 5px;
  margin-left: 20px;
  height: 30%;
  width: 30%;
}

button:hover {
  color: #4FB180;
  font-weight: 600;
}

.list-header {
  display: flex;
  justify-content: space-around;
  align-items: center;
}
</style>
