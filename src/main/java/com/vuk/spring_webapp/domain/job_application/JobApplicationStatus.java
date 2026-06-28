package com.vuk.spring_webapp.domain.job_application;

/**
 * Represents a status that a job application can be in.
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.job_application.JobApplication
 */
public enum JobApplicationStatus {

    /**
     * Application has been submitted and doesn't yet have an employee reviewing it.
     */
    SUBMITTED,

    /**
     * Application is currently being reviewed by an assigned employee.
     */
    UNDER_REVIEW,

    /**
     * Applicant has been invited for an interview.
     */
    INTERVIEW_SCHEDULED,

    /**
     * Applicant has received a job offer.
     */
    OFFERED,

    /**
     * Applicant has accepted a job offer, final status.
     */
    ACCEPTED,

    /**
     * Applicant has been rejected at any stage of the process or has rejected an offer.
     */
    REJECTED,
}
