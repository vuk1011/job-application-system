<script setup>
import { RouterLink, useRouter } from 'vue-router'

const router = useRouter()

const handleSignOut = () => {
  localStorage.removeItem('jwt')
  router.push('/login')
}
</script>

<template>
  <header class="app-header">
    <RouterLink to="/" class="nav-link">Home</RouterLink>
    <button @click="handleSignOut" class="nav-link">Sign Out</button>
  </header>
</template>

<style scoped>
.app-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #4FB180;
  width: 100%;
}

.nav-link {
  text-decoration: none;
  color: white;
  margin: 0 1rem;
  background: none;
  border: none;
  cursor: pointer;
}

.nav-link:hover {
  font-weight: bold;
}
</style>
