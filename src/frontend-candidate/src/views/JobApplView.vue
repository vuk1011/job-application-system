<script setup>
import Interview from '@/components/Interview.vue';
import { getInterviewsByJobApplId } from '@/services/interviewService';
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
	companyName: '-',
})
const interviews = ref([])

onMounted(async () => {
	try {
		await loadJobAppl()
		await loadInterviews()
		console.log(interviews)
	} catch (_) {
		setErrorMessage('Failed loading job application')
	}
})

const loadJobAppl = async () => {
	const response = await getJobApplById(id)
	const data = response.data.data
	jobAppl.value.title = data.jobPosting.title
	jobAppl.value.description = data.jobPosting.description
	jobAppl.value.submited = new Date(data.dateOfSubmission)
	jobAppl.value.status = data.status
	jobAppl.value.companyName = data.jobPosting.companyName

	if (data.status === 'OFFERED' || data.status === 'ACCEPTED' || data.status === 'REJECTED') {
		ableToWithdraw.value = false
	}
}

const loadInterviews = async () => {
	const response = await getInterviewsByJobApplId(id)
	interviews.value = response.data.data.map(interview => ({
		id: interview.id,
		title: interview.title,
		description: interview.description,
		timeScheduled: interview.timeScheduled,
	}))
}

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

	<h4>Company: {{ jobAppl.companyName }}</h4>
	<p>{{ jobAppl.description }}</p>
	<p><i>Submitted: {{ jobAppl.submitted.toLocaleDateString() }}</i></p>
	<p>Status: {{ jobAppl.status }}</p>
	<button v-show="ableToWithdraw" type="button" @click="withdraw">Withdraw</button>
	<hr>

	<h3>Interviews</h3>
	<Interview v-for="interview in interviews" :key="interview.id" :id="interview.id" :title="interview.title"
		:description="interview.description" :time-scheduled="new Date(interview.timeScheduled)" />
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
