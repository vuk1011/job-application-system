package com.vuk.spring_webapp.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CandidateTest {

    private static final String FIRST_NAME = "Mans";
    private static final String LAST_NAME = "Bjork";
    private static final Sex SEX = Sex.MALE;
    private static final String PHONE = "123456789";
    private static final String ADDRESS = "Oak Street 1";
    private static final String EMAIL = "mans@yahoo.com";
    private static final String PASSWORD = "secret123";

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        Candidate candidate = new Candidate();

        assertNull(candidate.getId());
        assertNull(candidate.getRole());
        assertNull(candidate.getFirstName());
        assertNull(candidate.getLastName());
        assertNull(candidate.getSex());
        assertNull(candidate.getPhone());
        assertNull(candidate.getAddress());
        assertNull(candidate.getEmail());
        assertNull(candidate.getPassword());
        assertNull(candidate.getResume());
        assertNull(candidate.getJobApplications());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        Candidate candidate = new Candidate(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD);

        assertNull(candidate.getId());
        assertNull(candidate.getResume());
        assertNull(candidate.getJobApplications());

        assertEquals(Role.CANDIDATE, candidate.getRole());
        assertEquals(FIRST_NAME, candidate.getFirstName());
        assertEquals(LAST_NAME, candidate.getLastName());
        assertEquals(SEX, candidate.getSex());
        assertEquals(PHONE, candidate.getPhone());
        assertEquals(ADDRESS, candidate.getAddress());
        assertEquals(EMAIL, candidate.getEmail());
        assertEquals(PASSWORD, candidate.getPassword());
    }

}