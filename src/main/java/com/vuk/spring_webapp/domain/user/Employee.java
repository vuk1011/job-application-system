package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.company.Company;
import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.*;
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
     */
    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

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
     *
     * @see com.vuk.spring_webapp.domain.company.Company
     */
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
}
