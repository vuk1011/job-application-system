<script setup>
defineProps({
  id: Number,
  submitted: Date,
  status: String,
  job: String,
})
</script>

<template>
  <div>
    <h2>#{{ id }} ({{ job }}) | <i>submitted: {{ submitted.toLocaleDateString() }}</i> | {{ status }}</h2>
    <button type="button" @click="$emit('openApplication', id)">See details...</button>
  </div>
</template>

<style scoped>
div {
  margin: 5px 10px;
  padding: 8px;
  border-radius: 5px;
  background-color: bisque;
}
</style>
