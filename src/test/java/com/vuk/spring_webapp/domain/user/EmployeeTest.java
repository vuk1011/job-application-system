package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.company.Company;
import com.vuk.spring_webapp.domain.job_application.JobApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Employee Unit Tests")
class EmployeeTest {

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "Mans";
    private static final String LAST_NAME = "Bjork";
    private static final Sex SEX = Sex.MALE;
    private static final String PHONE = "123456789";
    private static final String ADDRESS = "Oak Street 1";
    private static final String EMAIL = "mans@yahoo.com";
    private static final String PASSWORD = "secret123";
    private static final String NATIONAL_ID = "0000000000000";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.now();
    private static final LocalDate DATE_OF_HIRE = LocalDate.now();
    private static final Company COMPANY = new Company();

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        Employee employee = new Employee();

        assertNull(employee.getId());
        assertNull(employee.getRole());
        assertNull(employee.getFirstName());
        assertNull(employee.getLastName());
        assertNull(employee.getSex());
        assertNull(employee.getPhone());
        assertNull(employee.getAddress());
        assertNull(employee.getEmail());
        assertNull(employee.getPassword());
        assertNull(employee.getNationalId());
        assertNull(employee.getDateOfBirth());
        assertNull(employee.getDateOfHire());
        assertEquals(0, employee.getManagedJobApplications().size());
        assertNull(employee.getCompany());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        Employee employee = new Employee(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD,
                NATIONAL_ID, DATE_OF_BIRTH, DATE_OF_HIRE, COMPANY);

        assertNull(employee.getId());
        assertEquals(0, employee.getManagedJobApplications().size());

        assertEquals(Role.EMPLOYEE, employee.getRole());
        assertEquals(FIRST_NAME, employee.getFirstName());
        assertEquals(LAST_NAME, employee.getLastName());
        assertEquals(SEX, employee.getSex());
        assertEquals(PHONE, employee.getPhone());
        assertEquals(ADDRESS, employee.getAddress());
        assertEquals(EMAIL, employee.getEmail());
        assertEquals(PASSWORD, employee.getPassword());
        assertEquals(NATIONAL_ID, employee.getNationalId());
        assertEquals(DATE_OF_BIRTH, employee.getDateOfBirth());
        assertEquals(DATE_OF_HIRE, employee.getDateOfHire());
        assertEquals(COMPANY, employee.getCompany());
    }

    @Test
    @DisplayName("toString includes all basic fields")
    void toStringIncludesAllBasicFields() {
        Employee employee = new Employee(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD,
                NATIONAL_ID, DATE_OF_BIRTH, DATE_OF_HIRE, null);
        employee.setId(ID);

        String result = employee.toString();

        assertTrue(result.contains("id=" + ID));
        assertTrue(result.contains("role=" + Role.EMPLOYEE));
        assertTrue(result.contains("firstName='" + FIRST_NAME + '\''));
        assertTrue(result.contains("lastName='" + LAST_NAME + '\''));
        assertTrue(result.contains("sex=" + SEX));
        assertTrue(result.contains("phone='" + PHONE + '\''));
        assertTrue(result.contains("address='" + ADDRESS + '\''));
        assertTrue(result.contains("email='" + EMAIL + '\''));
        assertTrue(result.contains("password='" + PASSWORD + '\''));
        assertTrue(result.contains("nationalId='" + NATIONAL_ID + '\''));
        assertTrue(result.contains("dateOfBirth=" + DATE_OF_BIRTH));
        assertTrue(result.contains("dateOfHire=" + DATE_OF_HIRE));
    }

    @Test
    @DisplayName("toString includes element count for collections")
    void toStringIncludesElementCountForCollections() {
        Employee employee = new Employee(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD,
                NATIONAL_ID, DATE_OF_BIRTH, DATE_OF_HIRE, null);
        employee.setManagedJobApplications(List.of(new JobApplication(), new JobApplication()));

        String result = employee.toString();

        assertTrue(result.contains("managedJobApplicationsCount=2"));
    }

    @Test
    @DisplayName("toString handles empty collections")
    void toStringHandlesEmptyCollections() {
        Employee employee = new Employee(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD,
                NATIONAL_ID, DATE_OF_BIRTH, DATE_OF_HIRE, null);
        employee.setManagedJobApplications(List.of());

        String result = employee.toString();

        assertTrue(result.contains("managedJobApplicationsCount=0"));
    }

    @Test
    @DisplayName("toString handles null collections without throwing an exception and treats them as empty")
    void toStringHandlesNullCollections() {
        Employee employee = new Employee(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD,
                NATIONAL_ID, DATE_OF_BIRTH, DATE_OF_HIRE, null);

        assertDoesNotThrow(employee::toString);

        String result = employee.toString();

        assertTrue(result.contains("managedJobApplicationsCount=0"));
    }

    @Test
    @DisplayName("toString includes ID for associated entities")
    void toStringIncludesIdForAssociatedEntities() {
        Company company = new Company();
        company.setId(ID);
        Employee employee = new Employee(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD,
                NATIONAL_ID, DATE_OF_BIRTH, DATE_OF_HIRE, company);

        String result = employee.toString();

        assertTrue(result.contains("companyId=" + ID));
    }

    @Test
    @DisplayName("toString handles null associated entities without throwing an exception")
    void toStringHandlesNullAssociatedEntities() {
        Employee employee = new Employee(FIRST_NAME, LAST_NAME, SEX, PHONE, ADDRESS, EMAIL, PASSWORD,
                NATIONAL_ID, DATE_OF_BIRTH, DATE_OF_HIRE, null);

        assertDoesNotThrow(employee::toString);

        String result = employee.toString();

        assertTrue(result.contains("companyId=null"));
    }

    @Test
    @DisplayName("equals returns true when employees have the same ID")
    void equalsReturnsTrueWhenSameId() {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee1.setId(1L);
        employee2.setId(1L);

        assertEquals(employee1, employee2);
    }

    @Test
    @DisplayName("equals returns false when employees don't have the same ID")
    void equalsReturnsFalseWhenDifferentId() {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee1.setId(1L);
        employee2.setId(2L);

        assertNotEquals(employee1, employee2);
    }

    @Test
    @DisplayName("hashCode computes value based on ID only")
    void hashCodeComputesBasedOnId() {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee1.setId(1L);
        employee2.setId(1L);

        assertEquals(employee1.hashCode(), employee2.hashCode());
    }

}