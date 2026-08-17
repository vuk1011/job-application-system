package com.vuk.spring_webapp.domain.job_posting;

import com.vuk.spring_webapp.domain.company.Company;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.CLOSED;
import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.PUBLISHED;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JobPosting Unit Tests")
class JobPostingTest {

    private static final Long ID = 1L;
    private static final String TITLE = "Fullstack .NET developer";
    private static final String DESCRIPTION = "Looking for a fullstack developer...";
    private static final LocalDate DATE_OF_PUBLISHING = LocalDate.now();
    private static final LocalDate DATE_OF_EXPIRATION = LocalDate.now();
    private static final LocalDate DATE_OF_EXPIRATION_IN_PAST = LocalDate.now().minusDays(1);
    private static final LocalDate DATE_OF_EXPIRATION_IN_FUTURE = LocalDate.now().plusDays(1);
    private static final Company COMPANY = new Company();

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Title must not be blank")
    void titleMustNotBeBlank() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(" ");

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "title");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Title is required", violation.getMessage());
    }

    @Test
    @DisplayName("Title must not exceed 50 characters")
    void titleMustNotExceed50Characters() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle("x".repeat(51));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "title");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(Size.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Title must be at most 50 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Title can have 50 characters")
    void titleCanHave50Characters() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle("x".repeat(50));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "title");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid title produces no violations")
    void validTitleProducesNoViolations() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTitle(TITLE);

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "title");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Description must not be blank")
    void descriptionMustNotBeBlank() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDescription(" ");

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "description");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Description is required", violation.getMessage());
    }

    @Test
    @DisplayName("Description must not exceed 3000 characters")
    void descriptionMustNotExceed3000Characters() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDescription("x".repeat(3001));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "description");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(Size.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Description must be at most 3000 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Description can have 3000 characters")
    void descriptionCanHave3000Characters() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDescription("x".repeat(3000));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "description");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid description produces no violations")
    void validDescriptionProducesNoViolations() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDescription(DESCRIPTION);

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "description");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Date of publishing must not be null")
    void dateOfPublishingMustNotBeNull() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfPublishing(null);

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfPublishing");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Date of publishing is required", violation.getMessage());
    }

    @Test
    @DisplayName("Date of publishing must not be in the future")
    void dateOfPublishingMustNotBeInFuture() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfPublishing(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfPublishing");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(PastOrPresent.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Date of publishing must be in the past or present", violation.getMessage());
    }

    @Test
    @DisplayName("Date of publishing can be in the present")
    void dateOfPublishingCanBeInPresent() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfPublishing(LocalDate.now());

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfPublishing");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid date of publishing produces no violations")
    void validDateOfPublishingProducesNoViolations() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfPublishing(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfPublishing");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Date of expiration must not be null")
    void dateOfExpirationMustNotBeNull() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(null);

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfExpiration");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Date of expiration is required", violation.getMessage());
    }

    @Test
    @DisplayName("Date of expiration must not be in the past")
    void dateOfExpirationMustNotBeInPast() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfExpiration");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(FutureOrPresent.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Date of expiration must be in the present or future", violation.getMessage());
    }

    @Test
    @DisplayName("Date of expiration can be in the present")
    void dateOfExpirationCanBeInPresent() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(LocalDate.now());

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfExpiration");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid date of expiration produces no violations")
    void validDateOfExpirationProducesNoViolations() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "dateOfExpiration");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Company must not be null")
    void companyMustNotBeNull() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setCompany(null);

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "company");

        assertEquals(1, violations.size());

        ConstraintViolation<JobPosting> violation = violations.iterator().next();

        assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Company is required", violation.getMessage());
    }

    @Test
    @DisplayName("Valid company produces no violations")
    void validCompanyProducesNoViolations() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setCompany(new Company());

        Set<ConstraintViolation<JobPosting>> violations = validator.validateProperty(jobPosting, "company");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        JobPosting jobPosting = new JobPosting();

        assertNull(jobPosting.getId());
        assertNull(jobPosting.getTitle());
        assertNull(jobPosting.getDescription());
        assertNull(jobPosting.getDateOfPublishing());
        assertNull(jobPosting.getDateOfExpiration());
        assertNull(jobPosting.getCompany());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        JobPosting jobPosting = new JobPosting(TITLE, DESCRIPTION, DATE_OF_PUBLISHING, DATE_OF_EXPIRATION, COMPANY);

        assertNull(jobPosting.getId());

        assertEquals(TITLE, jobPosting.getTitle());
        assertEquals(DESCRIPTION, jobPosting.getDescription());
        assertEquals(DATE_OF_PUBLISHING, jobPosting.getDateOfPublishing());
        assertEquals(DATE_OF_EXPIRATION, jobPosting.getDateOfExpiration());
        assertEquals(COMPANY, jobPosting.getCompany());
    }

    @Test
    @DisplayName("getStatus evaluates to Closed when date of expiration has passed")
    void getStatusReturnsClosedWhenExpirationIsDue() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(DATE_OF_EXPIRATION_IN_PAST);

        assertEquals(CLOSED, jobPosting.getStatus());
    }

    @Test
    @DisplayName("getStatus evaluates to Published when date of expiration has not passed")
    void getStatusReturnsPublishedWhenExpirationIsNotDue() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(DATE_OF_EXPIRATION_IN_FUTURE);

        assertEquals(PUBLISHED, jobPosting.getStatus());
    }

    @Test
    @DisplayName("getStatus evaluates to Published when date of expiration is today")
    void getStatusReturnsPublishedWhenExpirationIsToday() {
        JobPosting jobPosting = new JobPosting();
        jobPosting.setDateOfExpiration(DATE_OF_EXPIRATION);

        assertEquals(PUBLISHED, jobPosting.getStatus());
    }

    @Test
    @DisplayName("toString includes all basic fields")
    void toStringIncludesAllBasicFields() {
        JobPosting jobPosting = new JobPosting(TITLE, DESCRIPTION, DATE_OF_PUBLISHING, DATE_OF_EXPIRATION, null);
        jobPosting.setId(ID);

        String result = jobPosting.toString();

        assertTrue(result.contains("id=" + ID));
        assertTrue(result.contains("title='" + TITLE + '\''));
        assertTrue(result.contains("description='" + DESCRIPTION + '\''));
        assertTrue(result.contains("dateOfPublishing=" + DATE_OF_PUBLISHING));
        assertTrue(result.contains("dateOfExpiration=" + DATE_OF_EXPIRATION));
    }

    @Test
    @DisplayName("toString includes ID for associated entities")
    void toStringIncludesIdForAssociatedEntities() {
        Company company = new Company();
        company.setId(ID);
        JobPosting jobPosting = new JobPosting(TITLE, DESCRIPTION, DATE_OF_PUBLISHING, DATE_OF_EXPIRATION, company);

        String result = jobPosting.toString();

        assertTrue(result.contains("companyId=" + ID));
    }

    @Test
    @DisplayName("toString handles null associated entities without throwing an exception")
    void toStringHandlesNullAssociatedEntities() {
        JobPosting jobPosting = new JobPosting(TITLE, DESCRIPTION, DATE_OF_PUBLISHING, DATE_OF_EXPIRATION, null);

        assertDoesNotThrow(jobPosting::toString);

        String result = jobPosting.toString();

        assertTrue(result.contains("companyId=null"));
    }

    @Test
    @DisplayName("equals returns true when job postings have the same ID")
    void equalsReturnsTrueWhenSameId() {
        JobPosting jobPosting1 = new JobPosting();
        JobPosting jobPosting2 = new JobPosting();
        jobPosting1.setId(1L);
        jobPosting2.setId(1L);

        assertEquals(jobPosting1, jobPosting2);
    }

    @Test
    @DisplayName("equals returns false when job postings don't have the same ID")
    void equalsReturnsFalseWhenDifferentId() {
        JobPosting jobPosting1 = new JobPosting();
        JobPosting jobPosting2 = new JobPosting();
        jobPosting1.setId(1L);
        jobPosting2.setId(2L);

        assertNotEquals(jobPosting1, jobPosting2);
    }

    @Test
    @DisplayName("hashCode computes value based on ID only")
    void hashCodeComputesBasedOnId() {
        JobPosting jobPosting1 = new JobPosting();
        JobPosting jobPosting2 = new JobPosting();
        jobPosting1.setId(1L);
        jobPosting2.setId(1L);

        assertEquals(jobPosting1.hashCode(), jobPosting2.hashCode());
    }
}