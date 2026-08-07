package com.vuk.spring_webapp.service.job_posting;

import com.vuk.spring_webapp.domain.company.Company;
import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.domain.user.Sex;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.repository.EmployeeRepository;
import com.vuk.spring_webapp.repository.JobPostingRepository;
import com.vuk.spring_webapp.transfer.dto.JobPostingDto;
import com.vuk.spring_webapp.transfer.request.CreateJobPostingRequest;
import com.vuk.spring_webapp.transfer.request.UpdateJobPostingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.CLOSED;
import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.PUBLISHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobPostingServiceImpl Unit Tests")
class JobPostingServiceImplTest {

    @Mock
    private JobPostingRepository jobPostingRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JobPosting jobPostingPublished;
    @Mock
    private JobPosting jobPostingClosed;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    private Long jobPostingId;
    private JobPostingDto dto;
    private CreateJobPostingRequest createJobPostingRequest;
    private UpdateJobPostingRequest updateJobPostingRequest;
    private Company company;
    private String employeeEmail;
    private Employee employee;

    @BeforeEach
    void setUp() {
        jobPostingId = 1L;

        dto = new JobPostingDto();

        createJobPostingRequest = new CreateJobPostingRequest();
        createJobPostingRequest.setTitle("Backend Java Engineer");
        createJobPostingRequest.setDescription("Description...");

        updateJobPostingRequest = new UpdateJobPostingRequest();
        updateJobPostingRequest.setTitle("Backend Java Engineer - NEW");
        updateJobPostingRequest.setDescription("Description... - NEW");

        company = new Company("Krem sirevi", "Proizvodimo krem sireve", "Zrenjaninski put 104b");

        employeeEmail = "vuk@company.com";
        employee = new Employee(
                "Vuk", "Perović", Sex.FEMALE, "381621233444", "Ulica 5", employeeEmail,
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), company
        );
    }

    @Test
    @DisplayName("findById returns mapped job posting when it exists")
    void findByIdReturnsJobPosting() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPostingPublished));
        when(modelMapper.map(jobPostingPublished, JobPostingDto.class)).thenReturn(dto);

        JobPostingDto result = jobPostingService.findById(jobPostingId);

        assertEquals(dto, result);

        verify(jobPostingRepository).findById(jobPostingId);
        verify(modelMapper).map(jobPostingPublished, JobPostingDto.class);
        verifyNoMoreInteractions(jobPostingRepository, modelMapper);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when job posting does not exist")
    void findByIdThrowsResourceNotFoundException() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobPostingService.findById(jobPostingId)
        );
        assertEquals("Job posting not found with id " + jobPostingId, exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("findAll returns mapped job postings for the authenticated employee's company")
    void findAllReturnsJobPostings() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(employeeEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(employee);
        when(jobPostingRepository.findAllByCompany(company)).thenReturn(List.of(jobPostingPublished));
        when(modelMapper.map(jobPostingPublished, JobPostingDto.class)).thenReturn(dto);

        List<JobPostingDto> result;
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            result = jobPostingService.findAll();
        }

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(employeeRepository).findByEmail(employeeEmail);
        verify(jobPostingRepository).findAllByCompany(company);
        verify(modelMapper).map(jobPostingPublished, JobPostingDto.class);
        verifyNoMoreInteractions(employeeRepository, jobPostingRepository, modelMapper);
    }

    @Test
    @DisplayName("findAll returns empty list when company has no job postings")
    void findAllReturnsEmptyListWhenNoPostings() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(employeeEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(employee);
        when(jobPostingRepository.findAllByCompany(company)).thenReturn(List.of());

        List<JobPostingDto> result;
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            result = jobPostingService.findAll();
        }

        assertTrue(result.isEmpty());

        verify(employeeRepository).findByEmail(employeeEmail);
        verify(jobPostingRepository).findAllByCompany(company);
        verifyNoMoreInteractions(employeeRepository, jobPostingRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("findAllPublished returns only mapped published job postings")
    void findAllPublishedReturnsPublishedPostings() {
        when(jobPostingPublished.getStatus()).thenReturn(PUBLISHED);
        when(jobPostingClosed.getStatus()).thenReturn(CLOSED);
        when(jobPostingRepository.findAll()).thenReturn(List.of(jobPostingPublished, jobPostingClosed));
        when(modelMapper.map(jobPostingPublished, JobPostingDto.class)).thenReturn(dto);

        List<JobPostingDto> result = jobPostingService.findAllPublished();

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(jobPostingRepository).findAll();
        verify(modelMapper).map(jobPostingPublished, JobPostingDto.class);
        verifyNoMoreInteractions(jobPostingRepository, modelMapper);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("findAllPublished returns empty list when no job postings are published")
    void findAllPublishedReturnsEmptyListWhenNonePublished() {
        when(jobPostingClosed.getStatus()).thenReturn(CLOSED);
        when(jobPostingRepository.findAll()).thenReturn(List.of(jobPostingClosed));

        List<JobPostingDto> result = jobPostingService.findAllPublished();

        assertTrue(result.isEmpty());

        verify(jobPostingRepository).findAll();
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("create creates and returns job posting when expiration date is valid")
    void createSavesJobPostingWhenExpirationIsValid() {
        createJobPostingRequest.setDateOfExpiration(LocalDate.now().plusDays(30));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(employeeEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(employee);
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(JobPosting.class), eq(JobPostingDto.class))).thenReturn(dto);

        JobPostingDto result;
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            result = jobPostingService.create(createJobPostingRequest);
        }

        assertEquals(dto, result);

        ArgumentCaptor<JobPosting> captor = ArgumentCaptor.forClass(JobPosting.class);
        verify(jobPostingRepository).save(captor.capture());

        JobPosting saved = captor.getValue();
        assertEquals(createJobPostingRequest.getTitle(), saved.getTitle());
        assertEquals(createJobPostingRequest.getDescription(), saved.getDescription());
        assertEquals(LocalDate.now(), saved.getDateOfPublishing());
        assertEquals(createJobPostingRequest.getDateOfExpiration(), saved.getDateOfExpiration());
        assertEquals(company, saved.getCompany());

        verify(employeeRepository).findByEmail(employeeEmail);
        verify(modelMapper).map(any(JobPosting.class), eq(JobPostingDto.class));
        verifyNoMoreInteractions(employeeRepository, jobPostingRepository, modelMapper);
    }

    @Test
    @DisplayName("create throws ConflictException when expiration date is in the past")
    void createThrowsConflictExceptionForPastExpirationDate() {
        createJobPostingRequest.setDateOfExpiration(LocalDate.now().minusDays(1));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobPostingService.create(createJobPostingRequest)
        );
        assertEquals("Invalid date of expiration", exception.getMessage());

        verifyNoInteractions(jobPostingRepository, employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteById deletes job posting when it exists")
    void deleteByIdDeletesJobPosting() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPostingPublished));

        jobPostingService.deleteById(jobPostingId);

        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobPostingRepository).delete(jobPostingPublished);
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteById throws ResourceNotFoundException when job posting does not exist")
    void deleteByIdThrowsResourceNotFoundException() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobPostingService.deleteById(jobPostingId)
        );
        assertEquals("Job posting not found with id " + jobPostingId, exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobPostingRepository, never()).delete(any());
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("updateById updates and saves job posting when expiration date is valid")
    void updateByIdUpdatesJobPosting() {
        JobPosting existingPosting = new JobPosting(
                "Stari naslov", "Stari opis", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(5), company
        );

        updateJobPostingRequest.setDateOfExpiration(LocalDate.now().plusDays(30));

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(existingPosting));
        when(jobPostingRepository.save(existingPosting)).thenReturn(existingPosting);
        when(modelMapper.map(existingPosting, JobPostingDto.class)).thenReturn(dto);

        jobPostingService.updateById(jobPostingId, updateJobPostingRequest);

        assertEquals(updateJobPostingRequest.getTitle(), existingPosting.getTitle());
        assertEquals(updateJobPostingRequest.getDescription(), existingPosting.getDescription());
        assertEquals(updateJobPostingRequest.getDateOfExpiration(), existingPosting.getDateOfExpiration());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobPostingRepository).save(existingPosting);
        verify(modelMapper).map(existingPosting, JobPostingDto.class);
        verifyNoMoreInteractions(jobPostingRepository, modelMapper);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("updateById throws ConflictException when expiration date is in the past")
    void updateByIdThrowsConflictExceptionForPastExpirationDate() {
        updateJobPostingRequest.setDateOfExpiration(LocalDate.now().minusDays(1));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobPostingService.updateById(jobPostingId, updateJobPostingRequest)
        );
        assertEquals("Expiration date cannot be set before current time", exception.getMessage());

        verifyNoInteractions(jobPostingRepository, employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("updateById throws ResourceNotFoundException when job posting does not exist")
    void updateByIdThrowsResourceNotFoundException() {
        updateJobPostingRequest.setDateOfExpiration(LocalDate.now().plusDays(30));

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobPostingService.updateById(jobPostingId, updateJobPostingRequest)
        );
        assertEquals("Job posting not found with id " + jobPostingId, exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobPostingRepository, never()).save(any());
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }
}
