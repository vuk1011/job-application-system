package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
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
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "date_of_hire")
    @Temporal(TemporalType.DATE)
    private Date dateOfHire;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<JobApplication> managedJobApplications = new ArrayList<>();

    public Employee(String firstName, String lastName, Sex sex, String phone, String address, String email, String password, String nationalId, Date dateOfBirth, Date dateOfHire) {
        super(Role.EMPLOYEE, firstName, lastName, sex, phone, address, email, password);
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
        this.dateOfHire = dateOfHire;
    }
}
