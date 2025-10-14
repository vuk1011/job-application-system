package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("candidate")
@Getter
@Setter
@NoArgsConstructor
public class Candidate extends AppUser {

    @Lob
    private byte[] resume;

    @OneToMany(mappedBy = "candidate")
    private List<JobApplication> jobApplications;

    public Candidate(String firstName, String lastName, Sex sex, String phone, String address, String email, String password) {
        super(Role.CANDIDATE, firstName, lastName, sex, phone, address, email, password);
    }
}
