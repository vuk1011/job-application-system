package com.vuk.spring_webapp.domain.job_application;

import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.ACCEPTED;
import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.REJECTED;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_submission", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOfSubmission;

    @Enumerated(EnumType.STRING)
    private JobApplicationStatus status;

    @ManyToOne
    @JoinColumn(name = "job_posting_id", referencedColumnName = "id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "candidate_id", referencedColumnName = "id", nullable = false)
    private Candidate candidate;

    public JobApplication(Date dateOfSubmission, JobApplicationStatus status, JobPosting jobPosting, Employee employee, Candidate candidate) {
        this.dateOfSubmission = dateOfSubmission;
        this.status = status;
        this.jobPosting = jobPosting;
        this.employee = employee;
        this.candidate = candidate;
    }

    public boolean isManaged() {
        return employee != null;
    }

    public boolean statusIsFinal() {
        return status == ACCEPTED || status == REJECTED;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JobApplication that = (JobApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
