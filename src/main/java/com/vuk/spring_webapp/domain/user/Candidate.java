package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents a candidate type of user.
 *
 * <p>A candidate can view published job postings and apply to them. They can see their application's status and
 * the interviews and offers associated with it. A candidate can also edit their résumé, including a PDF file.</p>
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.user.AppUser
 * @see com.vuk.spring_webapp.domain.job_posting.JobPosting
 * @see com.vuk.spring_webapp.domain.job_application.JobApplication
 * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
 * @see com.vuk.spring_webapp.domain.interview.Interview
 * @see com.vuk.spring_webapp.domain.offer.Offer
 */
@Entity
@DiscriminatorValue("candidate")
@Getter
@Setter
@NoArgsConstructor
public class Candidate extends AppUser {

    /**
     * Candidate's resume as a PDF file.
     */
    @Lob
    private byte[] resume;

    /**
     * Corresponding list of job applications the candidate has made.
     *
     * @see com.vuk.spring_webapp.domain.job_application.JobApplication
     */
    @OneToMany(mappedBy = "candidate")
    private List<JobApplication> jobApplications;

    /**
     * Initializes a new candidate with required fields.
     *
     * <p>Calls the user's constructor and sets the role.</p>
     *
     * @param firstName
     * @param lastName
     * @param sex
     * @param phone
     * @param address
     * @param email
     * @param password
     * @see com.vuk.spring_webapp.domain.user.AppUser#AppUser(Role, String, String, Sex, String, String, String, String)
     */
    public Candidate(String firstName, String lastName, Sex sex, String phone, String address, String email, String password) {
        super(Role.CANDIDATE, firstName, lastName, sex, phone, address, email, password);
    }
}
