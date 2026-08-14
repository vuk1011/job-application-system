package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.company.Company;
import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an employee type of user.
 *
 * <p>An employee can manage job postings and applications for them. They can see candidate's profile information.
 * They can also manage interviews and offers associated with an application, affecting its status.</p>
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
@DiscriminatorValue("employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends AppUser {

    /**
     * Government issued unique identifier.
     * Must not be blank and must be between 10 and 20 characters.
     */
    @NotBlank(message = "National ID is required")
    @Size(min = 10, max = 20, message = "National ID must be between 10 and 20 characters")
    @Column(name = "national_id", length = 20)
    private String nationalId;

    /**
     * Date of birth.
     * Must not be null and must be in the past.
     */
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Date of hiring.
     * Must not be null and must be in the past or present.
     */
    @NotNull(message = "Date of hiring is required")
    @Past(message = "Date of hiring must be in the past or present")
    @Column(name = "date_of_hire")
    private LocalDate dateOfHire;

    /**
     * List of job applications the employee manages.
     *
     * @see com.vuk.spring_webapp.domain.job_application.JobApplication
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<JobApplication> managedJobApplications = new ArrayList<>();

    /**
     * Corresponding company the employee works in.
     * Must not be null.
     *
     * @see com.vuk.spring_webapp.domain.company.Company
     */
    @NotNull(message = "Company is required")
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    /**
     * Initializes a new employee with required fields.
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
     * @param nationalId  unique government issued ID, maximum 20 characters
     * @param dateOfBirth
     * @param dateOfHire
     * @param company     employee's company
     * @see com.vuk.spring_webapp.domain.user.AppUser#AppUser(Role, String, String, Sex, String, String, String, String)
     */
    public Employee(String firstName, String lastName, Sex sex, String phone, String address, String email,
                    String password, String nationalId, LocalDate dateOfBirth, LocalDate dateOfHire, Company company) {
        super(Role.EMPLOYEE, firstName, lastName, sex, phone, address, email, password);
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
        this.dateOfHire = dateOfHire;
        this.company = company;
    }

    /**
     * @implNote managedJobApplications is represented by its element count only.
     * company is represented by its ID only.
     */
    @Override
    public String toString() {
        return "Employee{" +
                super.toString() +
                ", nationalId='" + nationalId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", dateOfHire=" + dateOfHire +
                ", managedJobApplicationsCount=" + (managedJobApplications != null ? managedJobApplications.size() : 0) +
                ", companyId=" + (company != null ? company.getId() : null) +
                '}';
    }
}
