package com.vuk.spring_webapp.domain.interview;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an interview that is scheduled for a job application.
 *
 * <p>If an interview exists for a job application, the application has passed from UNDER_REVIEW status.</p>
 *
 * @author Vuk Perovic
 * @see com.vuk.spring_webapp.domain.job_application.JobApplication
 * @see com.vuk.spring_webapp.domain.job_application.JobApplicationStatus
 */
@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
public class Interview {

    /**
     * Unique identifier for the interview.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short name of the interview.
     * Must not be blank and must not exceed 50 characters.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must be at most 50 characters")
    @Column(length = 50, nullable = false)
    private String title;

    /**
     * Detailed description of the interview.
     * Must not be blank and must not exceed 200 characters.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 50, message = "Description must be at most 200 characters")
    @Column(length = 200, nullable = false)
    private String description;

    /**
     * Date and time the interview's scheduled for.
     * Must not be null and must be in the future.
     */
    @NotNull(message = "Time scheduled is required")
    @Future(message = "Time scheduled must be in the future")
    @Column(name = "time_scheduled", nullable = false)
    private LocalDateTime timeScheduled;

    /**
     * Corresponding job application.
     */
    @ManyToOne
    @JoinColumn(name = "job_application_id", referencedColumnName = "id", nullable = false)
    private JobApplication jobApplication;

    /**
     * Initializes a new interview with required fields.
     *
     * @param title          short name for the interview, maximum 50 characters
     * @param description    detailed description of the interview, maximum 200 characters
     * @param timeScheduled  date and time the interview is scheduled for
     * @param jobApplication corresponding job application
     * @see com.vuk.spring_webapp.domain.job_application.JobApplication
     */
    public Interview(String title, String description, LocalDateTime timeScheduled, JobApplication jobApplication) {
        this.title = title;
        this.description = description;
        this.timeScheduled = timeScheduled;
        this.jobApplication = jobApplication;
    }

    /**
     * @implNote jobApplication is represented by its ID only..
     */
    @Override
    public String toString() {
        return "Interview{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", timeScheduled=" + timeScheduled +
                ", jobApplicationId=" + (jobApplication != null ? jobApplication.getId() : null) +
                '}';
    }

    /**
     * Checks equality between two Interview instances based on ID.
     *
     * @param o the reference object with which to compare.
     * @return true if both interviews have the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Interview interview = (Interview) o;
        return Objects.equals(id, interview.id);
    }

    /**
     * Calculates a hash code based on the interview's ID.
     *
     * @return hash code of the ID
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
