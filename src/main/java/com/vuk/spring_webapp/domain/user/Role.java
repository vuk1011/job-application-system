package com.vuk.spring_webapp.domain.user;

/**
 * Represent's a user's role in the system.
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.user.AppUser
 */
public enum Role {

    /**
     * Searches and applies for job postings.
     */
    CANDIDATE,

    /**
     * Manages job postings, job applications, associated interviews and offers.
     */
    EMPLOYEE
}
