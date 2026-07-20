package com.vuk.spring_webapp.domain.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Company Unit Tests")
class CompanyTest {

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

}