<script setup>
import { computed } from 'vue';

const props = defineProps({
  id: Number,
  name: String,
  accepted: [Boolean, null],
})

const acceptedText = computed(
  () => props.accepted == null ? ('/') : (props.accepted ? 'YES' : 'NO')
)
</script>

<template>
  <div>
    <h4>{{ name }}</h4>
    <p><i>Accepted: {{ acceptedText }}</i></p>
    <button type="button" @click="$emit('deleteOffer', id)">Delete</button>
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
