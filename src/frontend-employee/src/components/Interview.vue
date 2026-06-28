<script setup>
defineProps({
  id: Number,
  title: String,
  description: String,
  timeScheduled: Date,
})
</script>

<template>
  <div>
    <h4>{{ title }}</h4>
    <p>Description: {{ description }}</p>
    <p><i>Scheduled: {{ timeScheduled.toLocaleString() }}</i></p>
    <button type="button" @click="$emit('deleteInterview', id)">Delete</button>
  </div>
</template>

<style scoped>
div {
  margin: 5px 10px;
  padding: 8px;
  border-radius: 5px;
  border: 2px solid #4FB180;
}
</style>
