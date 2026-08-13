package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Candidate Unit Tests")
class CandidateTest {

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "Mans";
    private static final String LAST_NAME = "Bjork";
    private static final Sex SEX = Sex.MALE;
    private static final String PHONE = "123456789";
    private static final String ADDRESS = "Oak Street 1";
    private static final String EMAIL = "mans@yahoo.com";
    private static final String PASSWORD = "secret123";
    private static final int RESUME_SIZE = 10;

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

    @Test
    @DisplayName("toString includes all basic fields")
    void toStringIncludesAllBasicFields() {
        Candidate candidate = new Candidate(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD);
        candidate.setId(ID);

        String result = candidate.toString();

        assertTrue(result.contains("id=" + ID));
        assertTrue(result.contains("role=" + Role.CANDIDATE));
        assertTrue(result.contains("firstName='" + FIRST_NAME + '\''));
        assertTrue(result.contains("lastName='" + LAST_NAME + '\''));
        assertTrue(result.contains("sex=" + SEX));
        assertTrue(result.contains("phone='" + PHONE + '\''));
        assertTrue(result.contains("address='" + ADDRESS + '\''));
        assertTrue(result.contains("email='" + EMAIL + '\''));
        assertTrue(result.contains("password='" + PASSWORD + '\''));
    }

    @Test
    @DisplayName("toString includes element count for collections")
    void toStringIncludesElementCountForCollections() {
        Candidate candidate = new Candidate(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD);
        candidate.setJobApplications(List.of(new JobApplication(), new JobApplication()));

        String result = candidate.toString();

        assertTrue(result.contains("jobApplicationsCount=2"));
    }

    @Test
    @DisplayName("toString handles empty collections")
    void toStringHandlesEmptyCollections() {
        Candidate candidate = new Candidate(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD);
        candidate.setJobApplications(List.of());

        String result = candidate.toString();

        assertTrue(result.contains("jobApplicationsCount=0"));
    }

    @Test
    @DisplayName("toString handles null collections without throwing an exception and treats them as empty")
    void toStringHandlesNullCollections() {
        Candidate candidate = new Candidate(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD);

        assertDoesNotThrow(candidate::toString);

        String result = candidate.toString();

        assertTrue(result.contains("jobApplicationsCount=0"));
    }

    @Test
    @DisplayName("toString includes resume size in bytes")
    void toStringIncludesResumeSize() {
        Candidate candidate = new Candidate(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD);
        candidate.setResume(new byte[RESUME_SIZE]);

        String result = candidate.toString();

        assertTrue(result.contains("resumeSize=" + RESUME_SIZE + " bytes"));
    }

    @Test
    @DisplayName("toString handles no resume without throwing an exception")
    void toStringHandlesNoResume() {
        Candidate candidate = new Candidate(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD);

        assertDoesNotThrow(candidate::toString);

        String result = candidate.toString();

        assertTrue(result.contains("resumeSize=null"));
    }

}