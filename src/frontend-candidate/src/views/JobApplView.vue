<script setup>
import { getJobApplById, withdrawJobAppl } from '@/services/jobApplService';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

const id = useRoute().params.id

const ableToWithdraw = ref(true)
const successMessage = ref('')
const errorMessage = ref('')

const jobAppl = ref({
	title: '-',
	description: '-',
	submitted: new Date(),
	status: '-',
})

onMounted(async () => {
	try {
		const response = await getJobApplById(id)
		const data = response.data.data
		jobAppl.value.title = data.jobPosting.title
		jobAppl.value.description = data.jobPosting.description
		jobAppl.value.submited = new Date(data.dateOfSubmission)
		jobAppl.value.status = data.status

		if (data.status === 'OFFERED' || data.status === 'ACCEPTED' || data.status === 'REJECTED') {
			ableToWithdraw.value = false
		}
	} catch (_) {
		setErrorMessage('Failed loading job application')
	}
})

const withdraw = async () => {
	window.scroll({ top: 0, behavior: 'smooth' })
	try {
		const response = await withdrawJobAppl(id)
		setSuccessMessage(response.data.message)
		ableToWithdraw.value = false
	} catch (_) {
		setErrorMessage('Failed to withdraw job application')
	}
}

const setErrorMessage = (message) => {
	successMessage.value = ''
	errorMessage.value = message
}

const setSuccessMessage = (message) => {
	errorMessage.value = ''
	successMessage.value = message
}
</script>

<template>
	<h1>{{ jobAppl.title }}</h1>
	<div class="error-container">
		<p v-if="successMessage" class="success-message">{{ successMessage }}</p>
		<p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
	</div>
	<hr>
	<p>{{ jobAppl.description }}</p>
	<p><i>Submitted: {{ jobAppl.submitted.toLocaleDateString() }}</i></p>
	<p>Status: {{ jobAppl.status }}</p>
	<button v-show="ableToWithdraw" type="button" @click="withdraw">Withdraw</button>
</template>

<style scoped>
h1,
p,
button {
	margin-left: 20px;
}

.success-message {
	color: green;
	font-weight: bold;
}

.error-message {
	color: red;
	font-weight: bold;
}
</style>
