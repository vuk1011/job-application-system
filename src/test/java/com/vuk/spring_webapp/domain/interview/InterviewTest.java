package com.vuk.spring_webapp.domain.interview;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Interview Unit Tests")
class InterviewTest {

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

}