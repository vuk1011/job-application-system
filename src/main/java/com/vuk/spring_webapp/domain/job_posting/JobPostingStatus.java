package com.vuk.spring_webapp.domain.job_posting;

/**
 * Represents a status that a job posting can be in.
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.job_posting.JobPosting
 */
public enum JobPostingStatus {

    /**
     * From creation, until the date of expiration.
     */
    PUBLISHED,

    /**
     * After the date of expiration.
     */
    CLOSED
}
