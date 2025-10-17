<script setup>
import { RouterLink } from 'vue-router';

const name = localStorage.getItem('firstName')
</script>

<template>
  <div class="center-box">
    <h1>Hello, {{ name }}</h1>
    <RouterLink to="/job-applications" class="link">Unmanaged job applications</RouterLink>
    <RouterLink to="/managed" class="link">Job applications you manage</RouterLink>
    <RouterLink to="/job-postings" class="link">Job postings</RouterLink>
  </div>
</template>

<style scoped>
.center-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.link {
  margin-bottom: 15px;
}
</style>
