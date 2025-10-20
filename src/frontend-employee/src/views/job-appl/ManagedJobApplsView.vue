<script setup>
import ManagedJobAppl from '@/components/ManagedJobAppl.vue';
import router from '@/router';
import { getManagedJobAppls } from '@/services/jobApplService';
import { computed, onMounted, ref } from 'vue';

const statusFilterOptions = ['ALL', 'INTERVIEW_SCHEDULED', 'OFFERED', 'ACCEPTED', 'REJECTED']

const jobApplications = ref([])
const statusFilterSelection = ref('ALL')
const errorMessage = ref('')

const filteredApplications = computed(() =>
  statusFilterSelection.value === 'ALL'
    ? jobApplications.value
    : jobApplications.value.filter(appl => appl.status === statusFilterSelection.value)
)

onMounted(async () => {
  try {
    const response = await getManagedJobAppls()
    jobApplications.value = response.data.data.map(appl => ({
      id: appl.id,
      submitted: appl.dateOfSubmission,
      status: appl.status,
      job: appl.jobPosting.title,
    }))
  } catch (_) {
    setErrorMessage('Failed to load job applications you manage')
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
  <div class="container">
    <div class="error-container">
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>

    <div>
      <label for="status-filter">Filter by status: </label>
      <select v-model="statusFilterSelection">
        <option v-for="status in statusFilterOptions" :value="status">
          {{ status }}
        </option>
      </select>
    </div>

    <ManagedJobAppl v-for="appl in filteredApplications" :key="appl.id" :id="appl.id"
      :submitted="new Date(appl.submitted)" :status="appl.status" :job="appl.job"
      @open-application="id => openApplication(id)" />
  </div>
</template>

<style scoped>
.container {
  width: 1000px;
  display: flex;
  flex-direction: column;
}

.error-message {
  color: red;
  font-weight: bold;
}
</style>
