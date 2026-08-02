package com.vuk.spring_webapp.service.employee;

import com.vuk.spring_webapp.domain.company.Company;
import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.domain.user.Sex;
import com.vuk.spring_webapp.exception.EmailInUseException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.repository.AppUserRepository;
import com.vuk.spring_webapp.repository.CompanyRepository;
import com.vuk.spring_webapp.repository.EmployeeRepository;
import com.vuk.spring_webapp.transfer.request.RegisterEmployeeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImpl Unit Tests")
class EmployeeServiceImplTest {

    @Mock
    private AppUserRepository userRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private RegisterEmployeeRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterEmployeeRequest();
        request.setFirstName("Maja");
        request.setLastName("Simic");
        request.setSex(Sex.FEMALE);
        request.setPhone("38163321321");
        request.setAddress("Ulica 3");
        request.setEmail("majas@gmail.com");
        request.setPassword("secret123");
        request.setNationalId("1010001123001");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setDateOfHire(LocalDate.of(2020, 1, 10));
        request.setCompanyId(1L);
    }

    @Test
    @DisplayName("register encodes password and saves employee when company exists and email is not in use")
    void registerSavesEmployeeWithEncodedPassword() {
        Company company = new Company("Firma za konzerve", "...", "Trg Republike 11");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(companyRepository.findById(request.getCompanyId())).thenReturn(Optional.of(company));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        employeeService.register(request);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(userRepository).save(captor.capture());

        Employee saved = captor.getValue();
        assertEquals("Maja", saved.getFirstName());
        assertEquals("Simic", saved.getLastName());
        assertEquals(Sex.FEMALE, saved.getSex());
        assertEquals("38163321321", saved.getPhone());
        assertEquals("Ulica 3", saved.getAddress());
        assertEquals("majas@gmail.com", saved.getEmail());
        assertEquals("encodedPassword", saved.getPassword());
        assertEquals("1010001123001", saved.getNationalId());
        assertEquals(LocalDate.of(1990, 5, 15), saved.getDateOfBirth());
        assertEquals(LocalDate.of(2020, 1, 10), saved.getDateOfHire());
        assertEquals(company, saved.getCompany());

        verify(userRepository).existsByEmail("majas@gmail.com");
        verify(companyRepository).findById(1L);
        verify(passwordEncoder).encode("secret123");
        verifyNoMoreInteractions(userRepository, companyRepository, passwordEncoder);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("register throws IllegalArgumentException when company ID is null")
    void registerThrowsIllegalArgumentExceptionForNullCompanyId() {
        request.setCompanyId(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.register(request)
        );
        assertEquals("Company ID is required", exception.getMessage());

        verifyNoInteractions(userRepository, companyRepository, passwordEncoder, employeeRepository);
    }

    @Test
    @DisplayName("register throws EmailInUseException when email is already in use")
    void registerThrowsEmailInUseException() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        EmailInUseException exception = assertThrows(
                EmailInUseException.class,
                () -> employeeService.register(request)
        );
        assertEquals("Email is already in use", exception.getMessage());

        verify(userRepository).existsByEmail("majas@gmail.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(companyRepository, passwordEncoder, employeeRepository);
    }

    @Test
    @DisplayName("register throws ResourceNotFoundException when company does not exist")
    void registerThrowsResourceNotFoundException() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(companyRepository.findById(request.getCompanyId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.register(request)
        );
        assertEquals("Company not found", exception.getMessage());

        verify(userRepository).existsByEmail("majas@gmail.com");
        verify(companyRepository).findById(1L);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, employeeRepository);
    }

    @Test
    @DisplayName("getIdByEmail returns employee ID when email exists")
    void getIdByEmailReturnsId() {
        String email = "majas@gmail.com";
        Employee employee = new Employee(
                "Maja", "Simic", Sex.FEMALE, "38163321321", "Ulica 3", email,
                "encodedPassword", "1010001123001", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), new Company("Firma za konzerve", "...", "Trg Republike 11")
        );
        ReflectionTestUtils.setField(employee, "id", 42L);

        when(employeeRepository.findByEmail(email)).thenReturn(employee);

        Long id = employeeService.getIdByEmail(email);

        assertEquals(42L, id);

        verify(employeeRepository).findByEmail(email);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(userRepository, companyRepository, passwordEncoder);
    }

    @Test
    @DisplayName("getIdByEmail throws NullPointerException when email does not match any employee")
    void getIdByEmailThrowsNullPointerException() {
        String email = "unknown@example.com";
        when(employeeRepository.findByEmail(email)).thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> employeeService.getIdByEmail(email)
        );

        verify(employeeRepository).findByEmail(email);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(userRepository, companyRepository, passwordEncoder);
    }
}