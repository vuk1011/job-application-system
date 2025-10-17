<script setup>
import { RouterLink } from 'vue-router';

const name = localStorage.getItem('firstName')
</script>

<template>
  <div class="center-box">
    <h1>Hello, {{ name }}</h1>
    <RouterLink to="/profile" class="link">My Profile</RouterLink>
    <RouterLink to="/job-postings" class="link">See Published Job Postings</RouterLink>
    <RouterLink to="/job-applications" class="link">My Job Applications</RouterLink>
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
