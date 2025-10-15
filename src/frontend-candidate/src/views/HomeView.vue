<script setup>
import router from '@/router';
import { RouterLink, useRoute } from 'vue-router';

const name = useRoute().query.name

const handleSignOut = () => {
  localStorage.removeItem('jwt')
  router.push('/login')
}
</script>

<template>
  <h1>Hello, {{ name }}</h1>
  <RouterLink to="/profile">My Profile</RouterLink>
  <br>
  <RouterLink to="/job-postings">See Published Job Postings</RouterLink>
  <br>
  <RouterLink to="/job-applications">My Job Applications</RouterLink>
  <br>
  <button type="button" @click="handleSignOut">Sign Out</button>
  <br>
</template>

<style scoped></style>
