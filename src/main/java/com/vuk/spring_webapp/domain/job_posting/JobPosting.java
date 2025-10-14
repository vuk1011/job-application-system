package com.vuk.spring_webapp.domain.job_posting;

import com.vuk.spring_webapp.domain.position.Position;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 3000, nullable = false)
    private String description;

    @Column(name = "date_of_publishing", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfPublishing;

    @Column(name = "date_of_expiration", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfExpiration;

    @Enumerated(EnumType.STRING)
    private JobPostingStatus status;

    @OneToOne
    @JoinColumn(name = "position_id", referencedColumnName = "id", nullable = false)
    private Position position;

    public JobPosting(String title, String description, Date dateOfPublishing, Date dateOfExpiration, JobPostingStatus status, Position position) {
        this.title = title;
        this.description = description;
        this.dateOfPublishing = dateOfPublishing;
        this.dateOfExpiration = dateOfExpiration;
        this.status = status;
        this.position = position;
    }
}
