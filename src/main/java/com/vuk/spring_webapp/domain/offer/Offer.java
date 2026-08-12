package com.vuk.spring_webapp.domain.offer;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents a job offer.
 *
 * <p>An offer is created by an employee. It is associated with a candidate's job application. A candidate can
 * accept or reject an offer, impacting the job application's status.</p>
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.user.Employee
 * @see com.vuk.spring_webapp.domain.user.Candidate
 * @see com.vuk.spring_webapp.domain.job_application.JobApplication
 * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
 */
@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
public class Offer {

    /**
     * Unique identifier for the offer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short name for the offer.
     */
    @Column(length = 50, nullable = false)
    private String name;

    /**
     * Indicates whether offer was accepted/rejected or neither.
     */
    private Boolean accepted;

    /**
     * Corresponding job application.
     *
     * @see com.vuk.spring_webapp.domain.job_application.JobApplication
     */
    @ManyToOne
    @JoinColumn(name = "job_application_id", referencedColumnName = "id", nullable = false)
    private JobApplication jobApplication;

    /**
     * Initializes a new offer with required fields.
     *
     * @param name           short name, maximum 50 characters
     * @param jobApplication corresponding job application
     * @see com.vuk.spring_webapp.domain.job_application.JobApplication
     */
    public Offer(String name, JobApplication jobApplication) {
        this.name = name;
        this.jobApplication = jobApplication;
    }

    /**
     * @implNote jobApplication is represented by its ID only.
     */
    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", accepted=" + accepted +
                ", jobApplicationId=" + (jobApplication != null ? jobApplication.getId() : null) +
                '}';
    }

    /**
     * Checks equality between two Offer instances based on ID.
     *
     * @param o the reference object with which to compare.
     * @return true if both offers have the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Offer offer = (Offer) o;
        return Objects.equals(id, offer.id);
    }

    /**
     * Calculates a hash code based on the offer's ID.
     *
     * @return hash code of the ID
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
