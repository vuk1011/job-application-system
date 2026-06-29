package com.vuk.spring_webapp.domain.company;

import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.user.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents a company registered in the system.
 *
 * <p>A company has employees and job postings tied to it.</p>
 *
 * @author Vuk Perovic
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company {

    /**
     * Unique identifier for the company.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the company, has to be unique.
     */
    @Column(length = 50, nullable = false, unique = true)
    private String name;

    /**
     * Text with details about the company.
     */
    @Column(length = 200, nullable = false)
    private String about;

    /**
     * Company's address.
     */
    @Column(length = 50, nullable = false)
    private String address;

    /**
     * Employees the company has.
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Employee> employees;

    /**
     * Job postings from the company.
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<JobPosting> jobPostings;

    /**
     * Initializes a new company with required fields.
     *
     * @param name    unique name of the company, maximum 50 characters
     * @param about   a description of the company, maximum 200 characters
     * @param address company's address, maximum 50 characters
     */
    public Company(String name, String about, String address) {
        this.name = name;
        this.about = about;
        this.address = address;
    }
}
