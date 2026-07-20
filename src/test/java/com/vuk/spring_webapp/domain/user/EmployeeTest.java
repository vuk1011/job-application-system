package com.vuk.spring_webapp.domain.user;

import com.vuk.spring_webapp.domain.company.Company;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Employee Unit Tests")
class EmployeeTest {

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

}