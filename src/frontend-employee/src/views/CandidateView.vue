<script setup>
import { getCandidateInfoByJobApplId, getCandidateResumeByJobApplId } from '@/services/jobApplService';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const applicationId = useRoute().params.id

const personalInfo = ref({
  firstName: 'Candidate',
  lastName: '',
  sex: '',
  phone: '',
  address: '',
  email: '',
})
const resumeUrl = ref(null)
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getCandidateInfoByJobApplId(applicationId)
    const data = response.data.data
    personalInfo.value.firstName = data.firstName
    personalInfo.value.lastName = data.lastName
    personalInfo.value.sex = data.sex
    personalInfo.value.phone = data.phone
    personalInfo.value.address = data.address
    personalInfo.value.email = data.email
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed loading candidate\'s information'
    window.scroll({ top: 0, behavior: 'smooth' })
    setErrorMessage(message)
  }

  try {
    const response = await getCandidateResumeByJobApplId(applicationId);
    const blob = new Blob([response.data], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    resumeUrl.value = url;
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Failed loading candidate\'s resume'
    window.scroll({ top: 0, behavior: 'smooth' })
    setErrorMessage(message)
  }

})

const setErrorMessage = (message) => {
  errorMessage.value = message
}
</script>

<template>
  <h1>{{ personalInfo.firstName }}'s profile</h1>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  <hr>

  <h2>Personal Information</h2>
  <p><i>First name: </i>{{ personalInfo.firstName }}</p>
  <p><i>Last name: </i>{{ personalInfo.lastName }}</p>
  <p><i>Sex: </i>{{ personalInfo.sex }}</p>
  <p><i>Phone: </i>{{ personalInfo.phone }}</p>
  <p><i>Address: </i>{{ personalInfo.address }}</p>
  <p><i>Email: </i>{{ personalInfo.email }}</p>
  <hr>

  <h2>Resume</h2>
  <iframe v-if="resumeUrl" :src="resumeUrl" width="80%" height="700px"></iframe>
</template>

<style scoped>
.error-message {
  color: red;
  font-weight: bold;
}
</style>
