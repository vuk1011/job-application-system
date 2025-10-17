<script setup>
defineProps({
  id: Number,
  title: String,
  description: String,
  expires: Date,
})
</script>

<template>
  <div>
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
    <p><i>Expires: {{ expires.toLocaleDateString() }}</i></p>
    <button type="button" @click="$emit('apply', id)">Apply</button>
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
