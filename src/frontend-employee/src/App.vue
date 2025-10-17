<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import Footer from './components/Footer.vue';
import Header from './components/Header.vue';

const route = useRoute()

const showHeader = computed(() => {
  return !['/login', '/register'].includes(route.path)
})
</script>

<template>
  <Header v-if="showHeader" />
  <div class="content">
    <RouterView />
  </div>
  <Footer />
</template>

<style>
#app {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  max-width: 100%;
}

.content {
  flex: 1;
  width: auto;
}

* {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

body {
  margin: 0;
  background-color: #EEEADA;
}

.form-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 30vh;
  width: 30vw;
}

form {
  display: flex;
  flex-direction: column;
  background-color: white;
  padding: 2rem 1rem;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  border: 2px solid #4FB180;
  align-items: center;
}

.form-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  width: 100%;
}

.error-container {
  height: 1.5rem;
}

.link {
  color: #339162;
  text-decoration: none;
  padding: 5px;
}

.link:hover {
  border: #339162 solid 1px;
  border-radius: 5px;
}

h1 {
  font-weight: 600;
}

h2 {
  font-weight: 500;
}
</style>