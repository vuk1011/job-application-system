package com.vuk.spring_webapp.domain.job_application;

import com.vuk.spring_webapp.domain.interview.Interview;
import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.offer.Offer;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JobApplication Unit Tests")
class JobApplicationTest {

    private static final LocalDate DATE_OF_SUBMISSION = LocalDate.now();
    private static final JobApplicationStatus STATUS = JobApplicationStatus.UNDER_REVIEW;
    private static final JobPosting JOB_POSTING = new JobPosting();
    private static final Employee EMPLOYEE = new Employee();
    private static final Candidate CANDIDATE = new Candidate();
    private static final Long ID = 1L;

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Date of submission must not be null")
    void dateOfSubmissionMustNotBeNull() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setDateOfSubmission(null);

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "dateOfSubmission");

        assertEquals(1, violations.size());

        ConstraintViolation<JobApplication> violation = violations.iterator().next();

        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Date of submission is required", violation.getMessage());
    }

    @Test
    @DisplayName("Date of submission must not be in the future")
    void dateOfSubmissionMustNotBeInFuture() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setDateOfSubmission(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "dateOfSubmission");

        assertEquals(1, violations.size());

        ConstraintViolation<JobApplication> violation = violations.iterator().next();

        assertEquals(PastOrPresent.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Date of submission must be in the past or present", violation.getMessage());
    }

    @Test
    @DisplayName("Date of submission can be in the present")
    void dateOfSubmissionCanBeInPresent() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setDateOfSubmission(LocalDate.now());

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "dateOfSubmission");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid date of submission produces no violations")
    void validDateOfSubmissionProducesNoViolations() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setDateOfSubmission(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "dateOfSubmission");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Status must not be null")
    void statusMustNotBeNull() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(null);

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "status");

        assertEquals(1, violations.size());

        ConstraintViolation<JobApplication> violation = violations.iterator().next();

        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Status is required", violation.getMessage());
    }

    @ParameterizedTest
    @EnumSource(JobApplicationStatus.class)
    @DisplayName("Valid status produces no violations")
    void validStatusProducesNoViolations(JobApplicationStatus status) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(status);

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "status");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Job posting must not be null")
    void jobPostingMustNotBeNull() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setJobPosting(null);

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "jobPosting");

        assertEquals(1, violations.size());

        ConstraintViolation<JobApplication> violation = violations.iterator().next();

        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Job posting is required", violation.getMessage());
    }

    @Test
    @DisplayName("Valid job posting produces no violations")
    void validJobPostingProducesNoViolations() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setJobPosting(new JobPosting());

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "jobPosting");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Candidate must not be null")
    void candidateMustNotBeNull() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setCandidate(null);

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "candidate");

        assertEquals(1, violations.size());

        ConstraintViolation<JobApplication> violation = violations.iterator().next();

        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Candidate is required", violation.getMessage());
    }

    @Test
    @DisplayName("Valid candidate produces no violations")
    void validCandidateProducesNoViolations() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setCandidate(new Candidate());

        Set<ConstraintViolation<JobApplication>> violations = validator.validateProperty(jobApplication, "candidate");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        JobApplication jobApplication = new JobApplication();

        assertNull(jobApplication.getId());
        assertNull(jobApplication.getDateOfSubmission());
        assertNull(jobApplication.getStatus());
        assertNull(jobApplication.getJobPosting());
        assertNull(jobApplication.getEmployee());
        assertNull(jobApplication.getCandidate());
        assertNull(jobApplication.getOffers());
        assertNull(jobApplication.getInterviews());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        JobApplication jobApplication = new JobApplication(DATE_OF_SUBMISSION, STATUS, JOB_POSTING, EMPLOYEE, CANDIDATE);

        assertNull(jobApplication.getId());
        assertNull(jobApplication.getOffers());
        assertNull(jobApplication.getInterviews());

        assertEquals(DATE_OF_SUBMISSION, jobApplication.getDateOfSubmission());
        assertEquals(STATUS, jobApplication.getStatus());
        assertEquals(JOB_POSTING, jobApplication.getJobPosting());
        assertEquals(EMPLOYEE, jobApplication.getEmployee());
        assertEquals(CANDIDATE, jobApplication.getCandidate());
    }

    @Test
    @DisplayName("isManaged returns true when job application has an assigned employee")
    void isManagedReturnsTrueWhenEmployeeIsNotNull() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setEmployee(EMPLOYEE);

        assertTrue(jobApplication.isManaged());
    }

    @Test
    @DisplayName("isManaged returns false when job application has no assigned employee")
    void isManagedReturnsFalseWhenEmployeeIsNull() {
        JobApplication jobApplication = new JobApplication();

        assertFalse(jobApplication.isManaged());
    }

    @Test
    @DisplayName("statusIsFinal returns true when job application has status Accepted")
    void statusIsFinalReturnsTrueWhenStatusAccepted() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(JobApplicationStatus.ACCEPTED);

        assertTrue(jobApplication.statusIsFinal());
    }

    @Test
    @DisplayName("statusIsFinal returns false when job application has no status")
    void statusIsFinalReturnsFalseWhenStatusNotSet() {
        JobApplication jobApplication = new JobApplication();

        assertFalse(jobApplication.statusIsFinal());
    }

    @Test
    @DisplayName("statusIsFinal returns false when job application has status Submitted")
    void statusIsFinalReturnsFalseWhenStatusSubmitted() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(JobApplicationStatus.SUBMITTED);

        assertFalse(jobApplication.statusIsFinal());
    }

    @Test
    @DisplayName("statusIsFinal returns false when job application has status Under Review")
    void statusIsFinalReturnsFalseWhenStatusUnderReview() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(JobApplicationStatus.UNDER_REVIEW);

        assertFalse(jobApplication.statusIsFinal());
    }

    @Test
    @DisplayName("statusIsFinal returns false when job application has status Interview Scheduled")
    void statusIsFinalReturnsFalseWhenStatusInterviewScheduled() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(JobApplicationStatus.INTERVIEW_SCHEDULED);

        assertFalse(jobApplication.statusIsFinal());
    }

    @Test
    @DisplayName("statusIsFinal returns false when job application has status Offered")
    void statusIsFinalReturnsFalseWhenStatusOffered() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(JobApplicationStatus.OFFERED);

        assertFalse(jobApplication.statusIsFinal());
    }

    @Test
    @DisplayName("statusIsFinal returns false when job application has status Rejected")
    void statusIsFinalReturnsFalseWhenStatusRejected() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setStatus(JobApplicationStatus.REJECTED);

        assertFalse(jobApplication.statusIsFinal());
    }

    @Test
    @DisplayName("toString includes all basic fields")
    void toStringIncludesAllBasicFields() {
        JobApplication jobApplication = new JobApplication(DATE_OF_SUBMISSION, STATUS, null, null, null);
        jobApplication.setId(ID);

        String result = jobApplication.toString();

        assertTrue(result.contains("id=" + ID));
        assertTrue(result.contains("dateOfSubmission=" + DATE_OF_SUBMISSION));
        assertTrue(result.contains("status=" + STATUS));
    }

    @Test
    @DisplayName("toString includes element count for collections")
    void toStringIncludesElementCountForCollections() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setOffers(List.of(new Offer()));
        jobApplication.setInterviews(List.of(new Interview(), new Interview()));

        String result = jobApplication.toString();

        assertTrue(result.contains("offersCount=1"));
        assertTrue(result.contains("interviewsCount=2"));
    }

    @Test
    @DisplayName("toString handles empty collections")
    void toStringHandlesEmptyCollections() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setOffers(List.of());
        jobApplication.setInterviews(List.of());

        String result = jobApplication.toString();

        assertTrue(result.contains("offersCount=0"));
        assertTrue(result.contains("interviewsCount=0"));
    }

    @Test
    @DisplayName("toString handles null collections without throwing an exception and treats them as empty")
    void toStringHandlesNullCollections() {
        JobApplication jobApplication = new JobApplication();

        assertDoesNotThrow(jobApplication::toString);

        String result = jobApplication.toString();

        assertTrue(result.contains("offersCount=0"));
        assertTrue(result.contains("interviewsCount=0"));
    }

    @Test
    @DisplayName("toString includes ID for associated entities")
    void toStringIncludesIdForAssociatedEntities() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setId(ID);
        Employee employee = new Employee();
        employee.setId(ID);
        Candidate candidate = new Candidate();
        candidate.setId(ID);
        JobApplication jobApplication = new JobApplication(DATE_OF_SUBMISSION, STATUS, jobPosting, employee, candidate);

        String result = jobApplication.toString();

        assertTrue(result.contains("jobPostingId=" + ID));
        assertTrue(result.contains("employeeId=" + ID));
        assertTrue(result.contains("candidateId=" + ID));
    }

    @Test
    @DisplayName("toString handles null associated entities without throwing an exception")
    void toStringHandlesNullAssociatedEntities() {
        JobApplication jobApplication = new JobApplication();

        assertDoesNotThrow(jobApplication::toString);

        String result = jobApplication.toString();

        assertTrue(result.contains("jobPostingId=null"));
        assertTrue(result.contains("employeeId=null"));
        assertTrue(result.contains("candidateId=null"));
    }

    @Test
    @DisplayName("equals returns true when job applications have the same ID")
    void equalsReturnsTrueWhenSameId() {
        JobApplication jobApplication1 = new JobApplication();
        JobApplication jobApplication2 = new JobApplication();
        jobApplication1.setId(1L);
        jobApplication2.setId(1L);

        assertEquals(jobApplication1, jobApplication2);
    }

    @Test
    @DisplayName("equals returns false when job applications don't have the same ID")
    void equalsReturnsFalseWhenDifferentId() {
        JobApplication jobApplication1 = new JobApplication();
        JobApplication jobApplication2 = new JobApplication();
        jobApplication1.setId(1L);
        jobApplication2.setId(2L);

        assertNotEquals(jobApplication1, jobApplication2);
    }

    @Test
    @DisplayName("hashCode computes value based on ID only")
    void hashCodeComputesBasedOnId() {
        JobApplication jobApplication1 = new JobApplication();
        JobApplication jobApplication2 = new JobApplication();
        jobApplication1.setId(1L);
        jobApplication2.setId(1L);

        assertEquals(jobApplication1.hashCode(), jobApplication2.hashCode());
    }

}