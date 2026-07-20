package com.vuk.spring_webapp.domain.job_application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;
import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatusUtil.isStatusChangeAllowed;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JobApplicationStatusUtil Unit Tests")
class JobApplicationStatusUtilTest {

    @Test
    @DisplayName("Status change is allowed when two are the same")
    void statusChangeAllowedForSameStatus() {
        for (var status : JobApplicationStatus.values()) {
            assertTrue(isStatusChangeAllowed(status, status));
        }
    }

    @Test
    @DisplayName("Status change is allowed from status Under Review to Interview Scheduled or Rejected")
    void statusChangeAllowedFromStatusUnderReview() {
        assertTrue(isStatusChangeAllowed(UNDER_REVIEW, INTERVIEW_SCHEDULED));
        assertTrue(isStatusChangeAllowed(UNDER_REVIEW, REJECTED));
    }

    @Test
    @DisplayName("Status change is allowed from status Interview Scheduled to Offered or Rejected")
    void statusChangeAllowedFromStatusInterviewScheduled() {
        assertTrue(isStatusChangeAllowed(INTERVIEW_SCHEDULED, OFFERED));
        assertTrue(isStatusChangeAllowed(INTERVIEW_SCHEDULED, REJECTED));
    }

    @Test
    @DisplayName("Status change is allowed from status Offered to Accepted or Rejected")
    void statusChangeAllowedFromStatusOffered() {
        assertTrue(isStatusChangeAllowed(OFFERED, ACCEPTED));
        assertTrue(isStatusChangeAllowed(OFFERED, REJECTED));
    }

    @Test
    @DisplayName("Status change is allowed from status Rejected to Offered or Accepted")
    void statusChangeAllowedFromStatusRejected() {
        assertTrue(isStatusChangeAllowed(REJECTED, OFFERED));
        assertTrue(isStatusChangeAllowed(REJECTED, ACCEPTED));
    }

    @Test
    @DisplayName("Status change is not allowed from status Accepted to any other")
    void statusChangeNotAllowedFromStatusAccepted() {
        for (var status : JobApplicationStatus.values()) {
            if (status == ACCEPTED) continue;
            assertFalse(isStatusChangeAllowed(ACCEPTED, status));
        }
    }

}