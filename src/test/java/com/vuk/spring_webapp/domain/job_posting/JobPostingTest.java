package com.vuk.spring_webapp.domain.job_posting;

import com.vuk.spring_webapp.domain.company.Company;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.CLOSED;
import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("JobPosting Unit Tests")
class JobPostingTest {

    private static final String TITLE = "Fullstack .NET developer";
    private static final String DESCRIPTION = "Looking for a fullstack developer...";
    private static final LocalDate DATE_OF_PUBLISHING = LocalDate.now();
    private static final LocalDate DATE_OF_EXPIRATION = LocalDate.now();
    private static final LocalDate DATE_OF_EXPIRATION_IN_PAST = LocalDate.now().minusDays(1);
    private static final LocalDate DATE_OF_EXPIRATION_IN_FUTURE = LocalDate.now().plusDays(1);
    private static final Company COMPANY = new Company();

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        JobPosting jobPosting = new JobPosting();

        assertNull(jobPosting.getId());
        assertNull(jobPosting.getTitle());
        assertNull(jobPosting.getDescription());
        assertNull(jobPosting.getDateOfPublishing());
        assertNull(jobPosting.getDateOfExpiration());
        assertNull(jobPosting.getCompany());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        JobPosting jobPosting = new JobPosting(TITLE, DESCRIPTION, DATE_OF_PUBLISHING, DATE_OF_EXPIRATION, COMPANY);

        assertNull(jobPosting.getId());

        assertEquals(TITLE, jobPosting.getTitle());
        assertEquals(DESCRIPTION, jobPosting.getDescription());
        assertEquals(DATE_OF_PUBLISHING, jobPosting.getDateOfPublishing());
        assertEquals(DATE_OF_EXPIRATION, jobPosting.getDateOfExpiration());
        assertEquals(COMPANY, jobPosting.getCompany());
    }

    @Test
    @DisplayName("getStatus evaluates to Closed when date of expiration has passed")
    void getStatusReturnsClosedWhenExpirationIsDue() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(DATE_OF_EXPIRATION_IN_PAST);

        assertEquals(CLOSED, jobPosting.getStatus());
    }

    @Test
    @DisplayName("getStatus evaluates to Published when date of expiration has not passed")
    void getStatusReturnsPublishedWhenExpirationIsNotDue() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(DATE_OF_EXPIRATION_IN_FUTURE);

        assertEquals(PUBLISHED, jobPosting.getStatus());
    }

    @Test
    @DisplayName("getStatus evaluates to Published when date of expiration is today")
    void getStatusReturnsPublishedWhenExpirationIsToday() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(DATE_OF_EXPIRATION);

        assertEquals(PUBLISHED, jobPosting.getStatus());
    }

}