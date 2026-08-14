package com.vuk.spring_webapp.domain.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AppUser Unit Tests")
class AppUserTest {

    private static final String FIRST_NAME = "Mans";
    private static final String LAST_NAME = "Bjork";
    private static final Sex SEX = Sex.MALE;
    private static final String PHONE = "38165000333";
    private static final String ADDRESS = "Oak Street 1";
    private static final String EMAIL = "mans@yahoo.com";
    private static final String PASSWORD = "secret123";

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Role must not be null")
    void roleMustNotBeNull() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setRole(null);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "role");

        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    @DisplayName("Valid role produces no violations")
    void validRoleProducesNoViolations(Role role) {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setRole(role);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "role");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("First name must not be blank")
    void firstNameMustNotBeBlank() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setFirstName(" ");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "firstName");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("First name must not exceed 30 characters")
    void firstNameMustNotExceed30Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setFirstName("x".repeat(31));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "firstName");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("First name can have 30 characters")
    void firstNameCanHave30Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setFirstName("x".repeat(30));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "firstName");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid firstName produces no violations")
    void validFirstNameProducesNoViolations() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setFirstName(FIRST_NAME);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "firstName");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Last name must not be blank")
    void lastNameMustNotBeBlank() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setLastName(" ");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "lastName");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Last name must not exceed 30 characters")
    void lastNameMustNotExceed30Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setLastName("x".repeat(31));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "lastName");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Last name can have 30 characters")
    void lastNameCanHave30Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setLastName("x".repeat(30));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "lastName");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid lastName produces no violations")
    void validLastNameProducesNoViolations() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setLastName(LAST_NAME);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "lastName");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Sex must not be null")
    void sexMustNotBeNull() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setSex(null);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "sex");

        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(Sex.class)
    @DisplayName("Valid sex produces no violations")
    void validSexProducesNoViolations(Sex sex) {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setSex(sex);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "sex");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Phone must not be blank")
    void phoneMustNotBeBlank() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setPhone(" ");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "phone");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Phone must not have less than 8 digits")
    void phoneMustNotBeShorterThan8Digits() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setPhone("0".repeat(7));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "phone");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Phone can have 8 digits")
    void phoneCanHave8Digits() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setPhone("0".repeat(8));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "phone");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Phone must not have more than 16 digits")
    void phoneMustNotBeLongerThan16Digits() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setPhone("0".repeat(17));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "phone");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Phone can have 16 digits")
    void phoneCanHave16Digits() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setPhone("0".repeat(16));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "phone");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Phone must only contain digits")
    void phoneMustOnlyContainDigits() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setPhone(PHONE + "x");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "phone");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid phone produces no violations")
    void validPhoneProducesNoViolations() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setPhone(PHONE);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "phone");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Address must not be blank")
    void addressMustNotBeBlank() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setAddress(" ");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "address");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Address must not exceed 50 characters")
    void addressMustNotExceed50Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setAddress("x".repeat(51));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "address");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Address can have 50 characters")
    void addressCanHave50Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setAddress("x".repeat(50));

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "address");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid address produces no violations")
    void validAddressProducesNoViolations() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setAddress(ADDRESS);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "address");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Email must not be blank")
    void emailMustNotBeBlank() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setEmail(" ");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "email");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Email must not exceed 50 characters")
    void emailMustNotExceed50Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setEmail("x".repeat(50) + "@gmail.com");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "email");

        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alex@company",
            "kasandra@gmail..com",
            "alisa@yahoo.com@",
            "misa@ razmak.org",
            "marina@crtica-com",
    })
    @DisplayName("Email must be formatted well")
    void emailMustBeFormattedWell(String email) {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setEmail(email);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "email");

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Address can have 50 characters")
    void emailCanHave50Characters() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setEmail("x".repeat(40) + "@gmail.com");

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "email");

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Valid email produces no violations")
    void validEmailProducesNoViolations() {
        TestableAppUser appUser = new TestableAppUser();
        appUser.setEmail(EMAIL);

        Set<ConstraintViolation<TestableAppUser>> violations = validator.validateProperty(appUser, "email");

        assertTrue(violations.isEmpty());
    }

}