package com.vuk.spring_webapp.domain.job_posting;

import com.vuk.spring_webapp.domain.company.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    private LocalDate dateOfPublishing;

    @Column(name = "date_of_expiration", nullable = false)
    private LocalDate dateOfExpiration;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @Transient
    public JobPostingStatus getStatus() {
        return this.dateOfExpiration.isBefore(LocalDate.now()) ? JobPostingStatus.CLOSED : JobPostingStatus.PUBLISHED;
    }

    public JobPosting(String title, String description, LocalDate dateOfPublishing, LocalDate dateOfExpiration,
                      Company company) {
        this.title = title;
        this.description = description;
        this.dateOfPublishing = dateOfPublishing;
        this.dateOfExpiration = dateOfExpiration;
        this.company = company;
    }
}
