package com.vuk.spring_webapp.domain.job_posting;

import com.vuk.spring_webapp.domain.company.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

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
     * Must not be blank and must not exceed 50 characters.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must be at most 50 characters")
    @Column(length = 50, nullable = false)
    private String title;

    /**
     * Detailed description of the job posting.
     * Must not be blank and must not exceed 3000 characters.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 3000, message = "Description must be at most 3000 characters")
    @Column(length = 3000, nullable = false)
    private String description;

    /**
     * Date when the posting was created (published).
     * Must not be null and must be in the past or present.
     */
    @NotNull(message = "Date of publishing is required")
    @PastOrPresent(message = "Date of publishing must be in the past or present")
    @Column(name = "date_of_publishing", nullable = false)
    private LocalDate dateOfPublishing;

    /**
     * Date when the posting expires and stops being visible to candidates.
     * Must not be null and must be in the present or future.
     */
    @NotNull(message = "Date of expiration is required")
    @FutureOrPresent(message = "Date of expiration must be in the present or future")
    @Column(name = "date_of_expiration", nullable = false)
    private LocalDate dateOfExpiration;

    /**
     * Corresponding company.
     * Must not be null.
     *
     * @see com.vuk.spring_webapp.domain.company.Company
     */
    @NotNull(message = "Company is required")
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

    /**
     * @implNote company is represented by its ID only.
     */
    @Override
    public String toString() {
        return "JobPosting{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateOfPublishing=" + dateOfPublishing +
                ", dateOfExpiration=" + dateOfExpiration +
                ", companyId=" + (company != null ? company.getId() : null) +
                '}';
    }

    /**
     * Checks equality between two JobPosting instances based on ID.
     *
     * @param o the reference object with which to compare.
     * @return true if both job postings have the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JobPosting that = (JobPosting) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Calculates a hash code based on the job posting's ID.
     *
     * @return hash code of the ID
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
