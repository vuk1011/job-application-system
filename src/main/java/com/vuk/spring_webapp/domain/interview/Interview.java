package com.vuk.spring_webapp.domain.interview;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
     */
    @Column(length = 50, nullable = false)
    private String title;

    /**
     * Detailed description of the interview.
     */
    @Column(length = 200, nullable = false)
    private String description;

    /**
     * Date and time the interview's scheduled for.
     */
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
}
