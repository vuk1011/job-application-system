package com.vuk.spring_webapp.domain.company;

import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.user.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Company Unit Tests")
class CompanyTest {

    private static final Long ID = 1L;
    private static final String NAME = "Robots 101";
    private static final String ABOUT = "Company about robots.";
    private static final String ADDRESS = "Palms Blvd 3";

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        Company company = new Company();

        assertNull(company.getId());
        assertNull(company.getName());
        assertNull(company.getAbout());
        assertNull(company.getAddress());
        assertNull(company.getEmployees());
        assertNull(company.getJobPostings());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        Company company = new Company(NAME, ABOUT, ADDRESS);

        assertNull(company.getId());
        assertNull(company.getEmployees());
        assertNull(company.getJobPostings());

        assertEquals(NAME, company.getName());
        assertEquals(ABOUT, company.getAbout());
        assertEquals(ADDRESS, company.getAddress());
    }

    @Test
    @DisplayName("toString includes all basic fields")
    void toStringIncludesAllBasicFields() {
        Company company = new Company(NAME, ABOUT, ADDRESS);
        company.setId(ID);

        String result = company.toString();

        assertTrue(result.contains("id=" + ID));
        assertTrue(result.contains("name='" + NAME + '\''));
        assertTrue(result.contains("about='" + ABOUT + '\''));
        assertTrue(result.contains("address='" + ADDRESS + '\''));
    }

    @Test
    @DisplayName("toString includes element count for collections")
    void toStringIncludesElementCountForCollections() {
        Company company = new Company();
        company.setEmployees(List.of(new Employee()));
        company.setJobPostings(List.of(new JobPosting(), new JobPosting()));

        String result = company.toString();

        assertTrue(result.contains("employeesCount=1"));
        assertTrue(result.contains("jobPostingsCount=2"));
    }

    @Test
    @DisplayName("toString handles empty collections")
    void toStringHandlesEmptyCollections() {
        Company company = new Company();
        company.setEmployees(List.of());
        company.setJobPostings(List.of());

        String result = company.toString();

        assertTrue(result.contains("employeesCount=0"));
        assertTrue(result.contains("jobPostingsCount=0"));
    }

    @Test
    @DisplayName("toString handles null collections without throwing an exception and treats them as empty")
    void toStringHandlesNullCollections() {
        Company company = new Company();

        assertDoesNotThrow(company::toString);

        String result = company.toString();

        assertTrue(result.contains("employeesCount=0"));
        assertTrue(result.contains("jobPostingsCount=0"));
    }
}