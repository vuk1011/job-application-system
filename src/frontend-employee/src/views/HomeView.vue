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
  <br>
  <RouterLink to="/job-applications">Unmanaged job applications</RouterLink>
  <br>
  <RouterLink to="/managed">Job applications you manage</RouterLink>
  <br>
  <RouterLink to="/job-postings">Job postings</RouterLink>
  <br>
  <button type="button" @click="handleSignOut">Sign Out</button>
  <br>
</template>

<style scoped></style>
