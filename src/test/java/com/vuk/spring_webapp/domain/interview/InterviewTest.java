package com.vuk.spring_webapp.domain.interview;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Interview Unit Tests")
class InterviewTest {

    private static final Long ID = 1L;
    private static final String TITLE = "Technical interview #1";
    private static final String DESCRIPTION = "First round of technical interview. You'll be joined by the team lead and HR.";
    private static final LocalDateTime TIME_SCHEDULED = LocalDateTime.now();
    private static final JobApplication JOB_APPLICATION = new JobApplication();

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