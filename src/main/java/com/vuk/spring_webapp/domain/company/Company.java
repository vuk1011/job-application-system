package com.vuk.spring_webapp.domain.company;

import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.user.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

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
     * Must not be blank and must not exceed 50 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    @Column(length = 50, nullable = false, unique = true)
    private String name;

    /**
     * Text with details about the company.
     * Must not be blank and must not exceed 200 characters.
     */
    @NotBlank(message = "About is required")
    @Size(max = 200, message = "About must be at most 200 characters")
    @Column(length = 200, nullable = false)
    private String about;

    /**
     * Company's address.
     * Must not be blank and must not exceed 50 characters.
     */
    @NotBlank(message = "Address is required")
    @Size(max = 50, message = "Address must be at most 50 characters")
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

    /**
     * @implNote employees and jobPostings are represented by their element count only.
     */
    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", about='" + about + '\'' +
                ", address='" + address + '\'' +
                ", employeesCount=" + (employees != null ? employees.size() : 0) +
                ", jobPostingsCount=" + (jobPostings != null ? jobPostings.size() : 0) +
                '}';
    }

    /**
     * Checks equality between two Company instances based on ID.
     *
     * @param o the reference object with which to compare.
     * @return true if both companies have the same ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id);
    }

    /**
     * Calculates a hash code based on the company's ID.
     *
     * @return hash code of the ID
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
