<script setup>
import { computed } from 'vue';

const props = defineProps({
  id: Number,
  name: String,
  accepted: [Boolean, null],
  disableButtons: Boolean,
})

const acceptedText = computed(
  () => props.accepted == null ? ('/') : (props.accepted ? 'YES' : 'NO')
)

const disableButtons = computed(
  () => props.disableButtons || props.accepted != null
)
</script>

<template>
  <div>
    <h4>{{ name }}</h4>
    <p><i>Accepted: {{ acceptedText }}</i></p>
    <button type="button" :disabled="disableButtons" @click="$emit('acceptOffer', id)">Accept</button>
    <button type="button" :disabled="disableButtons" @click="$emit('rejectOffer', id)">Reject</button>
  </div>
</template>

<style scoped>
div {
  margin: 5px 10px;
  padding: 8px;
  border-radius: 5px;
  border: 2px solid #4FB180;
}

button {
  margin-right: 8px;
}
</style>
