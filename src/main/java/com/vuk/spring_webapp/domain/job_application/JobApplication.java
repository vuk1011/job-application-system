package com.vuk.spring_webapp.domain.job_application;

import com.vuk.spring_webapp.domain.interview.Interview;
import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.offer.Offer;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.ACCEPTED;

/**
 * Represents candidate's job application in the system.
 *
 * <p>A job application is created when a candidate applies for a job posting.</p>
 * <p>It can be managed by an employee.</p>
 * <p>Employee's and/or candidate's actions on it can cause status change.</p>
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.job_posting.JobPosting
 * @see com.vuk.spring_webapp.domain.user.Employee
 * @see com.vuk.spring_webapp.domain.user.Candidate
 * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
 */
@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
public class JobApplication {

    /**
     * Unique identifier for the job application.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date when the candidate applied for the job posting.
     */
    @Column(name = "date_of_submission", nullable = false)
    private LocalDate dateOfSubmission;

    /**
     * Job application status, affecting allowed actions.
     *
     * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
     */
    @Enumerated(EnumType.STRING)
    private JobApplicationStatus status;

    /**
     * Corresponding job posting to which the candidate applied for.
     *
     * @see com.vuk.spring_webapp.domain.job_posting.JobPosting
     */
    @ManyToOne
    @JoinColumn(name = "job_posting_id", referencedColumnName = "id", nullable = false)
    private JobPosting jobPosting;

    /**
     * Corresponding employee who manages the job application.
     *
     * @see com.vuk.spring_webapp.domain.user.Employee
     */
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    /**
     * Corresponding candidate that created the job application.
     *
     * @see com.vuk.spring_webapp.domain.user.Candidate
     */
    @ManyToOne
    @JoinColumn(name = "candidate_id", referencedColumnName = "id", nullable = false)
    private Candidate candidate;

    /**
     * Corresponding list of offers made.
     *
     * @see com.vuk.spring_webapp.domain.offer.Offer
     */
    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL)
    private List<Offer> offers;

    /**
     * Corresponding list of interviews scheduled.
     *
     * @see com.vuk.spring_webapp.domain.interview.Interview
     */
    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL)
    private List<Interview> interviews;

    /**
     * Initializes a new job application with required fields.
     *
     * @param dateOfSubmission date when the candidate applied for the job posting
     * @param status           job application status
     * @param jobPosting       corresponding job posting to which the candidate applied for
     * @param employee         corresponding employee who manages the job application
     * @param candidate        corresponding candidate that created the job application
     * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
     * @see com.vuk.spring_webapp.domain.job_posting.JobPosting
     * @see com.vuk.spring_webapp.domain.user.Employee
     * @see com.vuk.spring_webapp.domain.user.Candidate
     */
    public JobApplication(LocalDate dateOfSubmission, JobApplicationStatus status, JobPosting jobPosting, Employee employee, Candidate candidate) {
        this.dateOfSubmission = dateOfSubmission;
        this.status = status;
        this.jobPosting = jobPosting;
        this.employee = employee;
        this.candidate = candidate;
    }

    /**
     * Checks whether this job application has been assigned to an employee.
     *
     * @return true if the job application has an assigned employee, false otherwise
     */
    public boolean isManaged() {
        return employee != null;
    }

    /**
     * Checks whether this job application's status is final, meaning no further changes are allowed.
     *
     * @return true if the job application's status is ACCEPTED, false otherwise
     */
    public boolean statusIsFinal() {
        return status == ACCEPTED;
    }

    /**
     * Checks equality between two JobApplication instances based on ID.
     *
     * @param o the reference object with which to compare.
     * @return true if both applications have the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Calculates a hash code based on the job application's ID.
     *
     * @return hash code of the ID
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
