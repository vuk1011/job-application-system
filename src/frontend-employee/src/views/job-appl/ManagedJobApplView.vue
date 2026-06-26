<script setup>
import Interview from '@/components/Interview.vue';
import { getInterviewsByJobApplId } from '@/services/interviewService';
import { getManagedJobApplById, updateManagedJobAppl } from '@/services/jobApplService';
import { onMounted, ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';

const id = useRoute().params.id
const stateSelection = ref([
  'OFFERED',
  'ACCEPTED',
  'REJECTED'
])

const jobApplication = ref({
  id: 0,
  submitted: new Date(),
  status: '',
  jobTitle: '',
  jobDesc: '',
  candidateId: 0,
})
const interviews = ref([])

const selectedStatus = ref('')
const successMessage = ref('')
const errorMessage = ref('')

onMounted(async () => {
  try {
    await loadManagedJobAppl()
    await loadInterviews()
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed getting the list of applications you manage'
    setErrorMessage(message)
  }
})

const loadManagedJobAppl = async () => {
  const response = await getManagedJobApplById(id)
  const data = response.data.data
  jobApplication.value.id = data.id
  jobApplication.value.submitted = new Date(data.dateOfSubmission)
  jobApplication.value.status = data.status
  jobApplication.value.jobTitle = data.jobPosting.title
  jobApplication.value.jobDesc = data.jobPosting.description
  jobApplication.value.candidateId = data.candidateId
}

const loadInterviews = async () => {
  const response = await getInterviewsByJobApplId(id)
  interviews.value = response.data.data.map(interview => ({
    id: interview.id,
    title: interview.title,
    description: interview.description,
    timeScheduled: interview.timeScheduled,
  }))
}

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

  <RouterLink :to="`/managed/${jobApplication.id}/candidate`" class="link">Candidate Profile</RouterLink>
  <hr>

  <p><b>Application status: </b>{{ jobApplication.status }}</p>
  <select v-model="selectedStatus">
    <option v-for="state in stateSelection" :value="state">
      {{ state }}
    </option>
  </select>
  <button type="button" @click="updateApplicationStatus"
    :disabled="selectedStatus === '' || selectedStatus === jobApplication.status">Update status</button>
  <hr>

  <h3>Interviews</h3>
  <Interview v-for="interview in interviews" :key="interview.id" :id="interview.id" :title="interview.title"
    :description="interview.description" :time-scheduled="new Date(interview.timeScheduled)" />
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

h1,
h2,
p,
select {
  margin-left: 15px;
  margin-right: 15px;
}

.link {
  margin: 15px;
}
</style>
