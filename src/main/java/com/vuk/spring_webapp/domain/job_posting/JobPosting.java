package com.vuk.spring_webapp.domain.job_posting;

import com.vuk.spring_webapp.domain.company.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents a company's job posting candidates can apply for.
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.company.Company
 * @see com.vuk.spring_webapp.domain.job_application.JobApplication
 */
@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
public class JobPosting {

    /**
     * Unique identifier for the job posting.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short title of the job posting.
     */
    @Column(length = 50, nullable = false)
    private String title;

    /**
     * Detailed description of the job posting.
     */
    @Column(length = 3000, nullable = false)
    private String description;

    /**
     * Date when the posting was created (published).
     */
    @Column(name = "date_of_publishing", nullable = false)
    private LocalDate dateOfPublishing;

    /**
     * Date when the posting expires and stops being visible to candidates.
     */
    @Column(name = "date_of_expiration", nullable = false)
    private LocalDate dateOfExpiration;

    /**
     * Corresponding company.
     *
     * @see com.vuk.spring_webapp.domain.company.Company
     */
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    /**
     * Evaluates the job posting's status based on its date of expiration.
     *
     * @return status of the job posting, whether it is PUBLISHED or CLOSED
     * @see com.vuk.spring_webapp.domain.job_posting.JobPostingStatus
     */
    @Transient
    public JobPostingStatus getStatus() {
        return this.dateOfExpiration.isBefore(LocalDate.now()) ? JobPostingStatus.CLOSED : JobPostingStatus.PUBLISHED;
    }

    /**
     * Initializes a new job posting with required fields.
     *
     * @param title            short title, maximum 50 characters
     * @param description      detailed description, maximum 3000 characters
     * @param dateOfPublishing date of creation/publishing
     * @param dateOfExpiration date of expiration, when it becomes invisible for new candidates
     * @param company          corresponding company
     * @see com.vuk.spring_webapp.domain.company.Company
     */
    public JobPosting(String title, String description, LocalDate dateOfPublishing, LocalDate dateOfExpiration,
                      Company company) {
        this.title = title;
        this.description = description;
        this.dateOfPublishing = dateOfPublishing;
        this.dateOfExpiration = dateOfExpiration;
        this.company = company;
    }
}
