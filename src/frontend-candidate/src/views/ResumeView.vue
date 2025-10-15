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
  } catch (error) {
    let message = error.response?.data?.message || error.message || 'Resume upload failed'
    setErrorMessage(message)
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
  } catch (error) {
    let message = error.response?.data?.message || error.message || 'Resume download failed'
    setErrorMessage(message)
  } finally {
    isLoading.value = false
  }
}

const handleDelete = async () => {
  isLoading.value = true
  try {
    const response = await deleteResume()
    setSuccessMessage(response.data.message)
  } catch (error) {
    let message = error.response?.data?.message || error.message || 'Resume deletion failed'
    setErrorMessage(message)
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
  <h2>Your Resume</h2>
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

  <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
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
</style>
