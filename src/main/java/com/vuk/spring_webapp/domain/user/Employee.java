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

@Entity
@DiscriminatorValue("employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends AppUser {

    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_hire")
    private LocalDate dateOfHire;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<JobApplication> managedJobApplications = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    public Employee(String firstName, String lastName, Sex sex, String phone, String address, String email,
                    String password, String nationalId, LocalDate dateOfBirth, LocalDate dateOfHire, Company company) {
        super(Role.EMPLOYEE, firstName, lastName, sex, phone, address, email, password);
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
        this.dateOfHire = dateOfHire;
        this.company = company;
    }
}
