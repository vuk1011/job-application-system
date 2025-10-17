<script setup>
defineProps({
  id: Number,
  title: String,
  published: Date,
  status: String
})
</script>

<template>
  <div>
    <h2>{{ title }} | <i>published: {{ published.toLocaleDateString() }}</i> | {{ status }}</h2>
    <button type="button" @click="$emit('openJobPosting', id)">Edit</button>
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
