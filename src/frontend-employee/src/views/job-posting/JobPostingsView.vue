<script setup>
import JobPosting from '@/components/JobPosting.vue';
import router from '@/router';
import { getJobPostings } from '@/services/jobPostingService';
import { computed, onMounted, ref } from 'vue';

const statusFilterOptions = ['ALL', 'PUBLISHED', 'CLOSED']

const jobs = ref([])
const statusFilterSelection = ref('ALL')
const errorMessage = ref('')

const filteredJobs = computed(() =>
  statusFilterSelection.value === 'ALL'
    ? jobs.value
    : jobs.value.filter(job => job.status === statusFilterSelection.value)
)

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
  <div class="container">
    <div class="list-header">
      <h1>Job Postings</h1>
      <button type="button" @click="router.push('/job-postings/create')">Create New</button>
    </div>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <div>
      <label for="status-filter">Filter by status: </label>
      <select v-model="statusFilterSelection">
        <option v-for="status in statusFilterOptions" :value="status">
          {{ status }}
        </option>
      </select>
    </div>

    <JobPosting v-for="job in filteredJobs" :key="job.id" :id="job.id" :title="job.title" :published="job.published"
      :status="job.status" @open-job-posting="id => openJobPosting(id)" />
  </div>
</template>

<style scoped>
.container {
  width: 800px;
  display: flex;
  flex-direction: column;
}

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
