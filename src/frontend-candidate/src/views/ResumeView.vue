<script setup>
import { deleteResume, getResume, uploadResume } from '@/services/profileService'
import { ref } from 'vue'

const selectedFile = ref(null)
const isLoading = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

const handleUpload = async () => {
  if (!selectedFile.value) return
  isLoading.value = true
  try {
    const response = await uploadResume(selectedFile.value)
    setSuccessMessage(response.data.message)
  } catch (_) {
    setErrorMessage('Failed to upload resume')
  } finally {
    isLoading.value = false
  }
}

const handleDownload = async () => {
  isLoading.value = true
  try {
    const response = await getResume()
    const blob = new Blob([response.data], { type: response.headers['content-type'] })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'resume.pdf'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (_) {
    setErrorMessage('Failed to download resume')
  } finally {
    isLoading.value = false
  }
}

const handleDelete = async () => {
  isLoading.value = true
  try {
    const response = await deleteResume()
    setSuccessMessage(response.data.message)
  } catch (_) {
    setErrorMessage('Failed to delete resume')
  } finally {
    isLoading.value = false
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
  <div class="resume-container">
    <div>
      <h1>Your Resume</h1>
    </div>

    <input type="file" accept=".pdf" @change="e => selectedFile = e.target.files[0]" />

    <div>
      <button @click="handleUpload" :disabled="!selectedFile || isLoading">
        Upload
      </button>

      <button @click="handleDownload" :disabled="isLoading">
        Download
      </button>

      <button @click="handleDelete" :disabled="isLoading">
        Delete
      </button>
    </div>

    <div class="error-container">
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<style scoped>
.resume-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

button {
  margin-bottom: 5px;
  margin-left: 5px;
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
