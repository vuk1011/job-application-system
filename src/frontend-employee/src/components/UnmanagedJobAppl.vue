<script setup>
defineProps({
  id: Number,
  submitted: Date,
})
</script>

<template>
  <div>
    <h2>Application #{{ id }} | <i>submitted: {{ submitted.toLocaleDateString() }}</i></h2>
    <button type="button" @click="$emit('manageApplication', id)">Add to managed</button>
  </div>
</template>

<style scoped>
div {
  margin: 5px 10px;
  padding: 8px;
  border-radius: 5px;
  border: 2px solid #4FB180;
}

h2 {
  font-weight: 500;
}
</style>
