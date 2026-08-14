package com.vuk.spring_webapp.domain.interview;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Interview Unit Tests")
class InterviewTest {

    private static final Long ID = 1L;
    private static final String TITLE = "Technical interview #1";
    private static final String DESCRIPTION = "First round of technical interview. You'll be joined by the team lead and HR.";
    private static final LocalDateTime TIME_SCHEDULED = LocalDateTime.now();
    private static final JobApplication JOB_APPLICATION = new JobApplication();

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Title must not be blank")
    void titleMustNotBeBlank() {
        Interview interview = new Interview();
        interview.setTitle(" ");

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "title");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Title must not exceed 50 characters")
    void titleMustNotExceed50Characters() {
        Interview interview = new Interview();
        interview.setTitle("x".repeat(51));

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "title");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Title can have 50 characters")
    void titleCanHave50Characters() {
        Interview interview = new Interview();
        interview.setTitle("x".repeat(50));

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "title");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid title produces no violations")
    void validTitleProducesNoViolations() {
        Interview interview = new Interview();
        interview.setTitle(TITLE);

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "title");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Description must not be blank")
    void descriptionMustNotBeBlank() {
        Interview interview = new Interview();
        interview.setDescription(" ");

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "description");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Description must not exceed 200 characters")
    void descriptionMustNotExceed50Characters() {
        Interview interview = new Interview();
        interview.setDescription("x".repeat(201));

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "description");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Description can have 200 characters")
    void descriptionCanHave50Characters() {
        Interview interview = new Interview();
        interview.setDescription("x".repeat(200));

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "description");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid description produces no violations")
    void validDescriptionProducesNoViolations() {
        Interview interview = new Interview();
        interview.setDescription(DESCRIPTION);

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "description");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Time scheduled must not be null")
    void timeScheduledMustNotBeNull() {
        Interview interview = new Interview();
        interview.setTimeScheduled(null);

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "timeScheduled");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Time scheduled must not be in the past")
    void timeScheduledMustNotBeInPast() {
        Interview interview = new Interview();
        interview.setTimeScheduled(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "timeScheduled");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Time scheduled must not be in the present")
    void timeScheduledMustNotBeInPresent() {
        Interview interview = new Interview();
        interview.setTimeScheduled(LocalDateTime.now());

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "timeScheduled");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid time scheduled produces no violations")
    void validTimeScheduledProducesNoViolations() {
        Interview interview = new Interview();
        interview.setTimeScheduled(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "timeScheduled");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Job application must not be null")
    void jobApplicationMustNotBeNull() {
        Interview interview = new Interview();
        interview.setJobApplication(null);

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "jobApplication");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid job application produces no violations")
    void validJobApplicationProducesNoViolations() {
        Interview interview = new Interview();
        interview.setJobApplication(new JobApplication());

        Set<ConstraintViolation<Interview>> violations = validator.validateProperty(interview, "jobApplication");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        Interview interview = new Interview();

        assertNull(interview.getId());
        assertNull(interview.getTitle());
        assertNull(interview.getDescription());
        assertNull(interview.getTimeScheduled());
        assertNull(interview.getJobApplication());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        Interview interview = new Interview(TITLE, DESCRIPTION, TIME_SCHEDULED, JOB_APPLICATION);

        assertNull(interview.getId());

        assertEquals(TITLE, interview.getTitle());
        assertEquals(DESCRIPTION, interview.getDescription());
        assertEquals(TIME_SCHEDULED, interview.getTimeScheduled());
        assertEquals(JOB_APPLICATION, interview.getJobApplication());
    }

    @Test
    @DisplayName("toString includes all basic fields")
    void toStringIncludesAllBasicFields() {
        Interview interview = new Interview(TITLE, DESCRIPTION, TIME_SCHEDULED, null);
        interview.setId(ID);

        String result = interview.toString();

        assertTrue(result.contains("id=" + ID));
        assertTrue(result.contains("title='" + TITLE + '\''));
        assertTrue(result.contains("description='" + DESCRIPTION + '\''));
        assertTrue(result.contains("timeScheduled=" + TIME_SCHEDULED));
    }

    @Test
    @DisplayName("toString includes ID for associated entities")
    void toStringIncludesIdForAssociatedEntities() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(ID);
        Interview interview = new Interview(TITLE, DESCRIPTION, TIME_SCHEDULED, jobApplication);

        String result = interview.toString();

        assertTrue(result.contains("jobApplicationId=" + ID));
    }

    @Test
    @DisplayName("toString handles null associated entities without throwing an exception")
    void toStringHandlesNullAssociatedEntities() {
        Interview interview = new Interview();

        assertDoesNotThrow(interview::toString);

        String result = interview.toString();

        assertTrue(result.contains("jobApplicationId=null"));
    }

    @Test
    @DisplayName("equals returns true when interviews have the same ID")
    void equalsReturnsTrueWhenSameId() {
        Interview interview1 = new Interview();
        Interview interview2 = new Interview();
        interview1.setId(1L);
        interview2.setId(1L);

        assertEquals(interview1, interview2);
    }

    @Test
    @DisplayName("equals returns false when interviews don't have the same ID")
    void equalsReturnsFalseWhenDifferentId() {
        Interview interview1 = new Interview();
        Interview interview2 = new Interview();
        interview1.setId(1L);
        interview2.setId(2L);

        assertNotEquals(interview1, interview2);
    }

    @Test
    @DisplayName("hashCode computes value based on ID only")
    void hashCodeComputesBasedOnId() {
        Interview interview1 = new Interview();
        Interview interview2 = new Interview();
        interview1.setId(1L);
        interview2.setId(1L);

        assertEquals(interview1.hashCode(), interview2.hashCode());
    }

}