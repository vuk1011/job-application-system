<script setup>
defineProps({
  id: Number,
  title: String,
  submitted: Date,
})
</script>

<template>
  <div>
    <h2>{{ title }}</h2>
    <p><i>Submitted: {{ submitted.toLocaleDateString() }}</i></p>
    <button type="button" @click="$emit('openApplication', id)">Details</button>
  </div>
</template>

<style scoped>
div {
  border: 2px solid black;
  margin: 5px 10px;
  padding: 8px;
}
</style>
