<script setup>
defineProps({
  id: Number,
  title: String,
  submitted: Date,
  companyName: String,
})
</script>

<template>
  <div>
    <h2>{{ title }}</h2>
    <h4>- {{ companyName }} -</h4>
    <p><i>Submitted: {{ submitted.toLocaleDateString() }}</i></p>
    <button type="button" @click="$emit('openApplication', id)">Details</button>
  </div>
</template>

<style scoped>
div {
  margin: 5px 15px;
  padding: 8px;
  border-radius: 5px;
  border: 2px solid #4FB180;
}
</style>
