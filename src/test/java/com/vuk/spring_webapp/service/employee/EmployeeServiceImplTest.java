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

    private Long companyId;
    private Long employeeId;
    private Company company;
    private RegisterEmployeeRequest request;
    private String passwordEncoded;

    @BeforeEach
    void setUp() {
        companyId = 1L;
        employeeId = 3L;

        company = new Company("Firma za kutije", "Tekst o nama...", "Višnjički drum 12");

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
        request.setCompanyId(companyId);

        passwordEncoded = "secret123-encoded";
    }

    @Test
    @DisplayName("register encodes password and saves employee when company exists and email is not in use")
    void registerSavesEmployeeWithEncodedPassword() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(companyRepository.findById(request.getCompanyId())).thenReturn(Optional.of(company));
        when(passwordEncoder.encode(request.getPassword())).thenReturn(passwordEncoded);

        employeeService.register(request);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(userRepository).save(captor.capture());

        Employee saved = captor.getValue();
        assertEquals(request.getFirstName(), saved.getFirstName());
        assertEquals(request.getLastName(), saved.getLastName());
        assertEquals(request.getSex(), saved.getSex());
        assertEquals(request.getPhone(), saved.getPhone());
        assertEquals(request.getAddress(), saved.getAddress());
        assertEquals(request.getEmail(), saved.getEmail());
        assertEquals(passwordEncoded, saved.getPassword());
        assertEquals(request.getNationalId(), saved.getNationalId());
        assertEquals(request.getDateOfBirth(), saved.getDateOfBirth());
        assertEquals(request.getDateOfHire(), saved.getDateOfHire());
        assertEquals(company, saved.getCompany());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(companyRepository).findById(companyId);
        verify(passwordEncoder).encode(request.getPassword());
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

        verify(userRepository).existsByEmail(request.getEmail());
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

        verify(userRepository).existsByEmail(request.getEmail());
        verify(companyRepository).findById(companyId);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, employeeRepository);
    }

    @Test
    @DisplayName("getIdByEmail returns employee ID when email exists")
    void getIdByEmailReturnsId() {
        Employee employee = new Employee(
                "", "", Sex.FEMALE, "", "", request.getEmail(),
                "", "", null, null, company
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(employee);

        Long id = employeeService.getIdByEmail(request.getEmail());

        assertEquals(employeeId, id);

        verify(employeeRepository).findByEmail(request.getEmail());
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(userRepository, companyRepository, passwordEncoder);
    }

    @Test
    @DisplayName("getIdByEmail throws NullPointerException when email does not match any employee")
    void getIdByEmailThrowsNullPointerException() {
        String email = "unknown@yahoo.com";
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