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
    private JobPosting jobPosting;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    @Test
    @DisplayName("findById returns mapped job posting when it exists")
    void findByIdReturnsJobPosting() {
        Long id = 1L;
        JobPostingDto dto = new JobPostingDto();

        when(jobPostingRepository.findById(id)).thenReturn(Optional.of(jobPosting));
        when(modelMapper.map(jobPosting, JobPostingDto.class)).thenReturn(dto);

        JobPostingDto result = jobPostingService.findById(id);

        assertEquals(dto, result);

        verify(jobPostingRepository).findById(id);
        verify(modelMapper).map(jobPosting, JobPostingDto.class);
        verifyNoMoreInteractions(jobPostingRepository, modelMapper);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when job posting does not exist")
    void findByIdThrowsResourceNotFoundException() {
        Long id = 1L;

        when(jobPostingRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobPostingService.findById(id)
        );
        assertEquals("Job posting not found with id " + id, exception.getMessage());

        verify(jobPostingRepository).findById(id);
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("findAll returns mapped job postings for the authenticated employee's company")
    void findAllReturnsJobPostings() {
        String email = "marko@gmail.com";
        Company company = new Company("Firma ABC", "Informacije...", "Hrastova 22");

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), company
        );

        JobPostingDto dto = new JobPostingDto();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(employeeRepository.findByEmail(email)).thenReturn(employee);
        when(jobPostingRepository.findAllByCompany(company)).thenReturn(List.of(jobPosting));
        when(modelMapper.map(jobPosting, JobPostingDto.class)).thenReturn(dto);

        List<JobPostingDto> result;
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            result = jobPostingService.findAll();
        }

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(employeeRepository).findByEmail(email);
        verify(jobPostingRepository).findAllByCompany(company);
        verify(modelMapper).map(jobPosting, JobPostingDto.class);
        verifyNoMoreInteractions(employeeRepository, jobPostingRepository, modelMapper);
    }

    @Test
    @DisplayName("findAll returns empty list when company has no job postings")
    void findAllReturnsEmptyListWhenNoPostings() {
        String email = "marko@gmail.com";
        Company company = new Company("Firma ABC", "Informacije...", "Hrastova 22");

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), company
        );

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(employeeRepository.findByEmail(email)).thenReturn(employee);
        when(jobPostingRepository.findAllByCompany(company)).thenReturn(List.of());

        List<JobPostingDto> result;
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            result = jobPostingService.findAll();
        }

        assertTrue(result.isEmpty());

        verify(employeeRepository).findByEmail(email);
        verify(jobPostingRepository).findAllByCompany(company);
        verifyNoMoreInteractions(employeeRepository, jobPostingRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("findAllPublished returns only mapped published job postings")
    void findAllPublishedReturnsPublishedPostings() {
        JobPosting publishedPosting = mock(JobPosting.class);
        JobPosting closedPosting = mock(JobPosting.class);

        JobPostingDto dto = new JobPostingDto();

        when(publishedPosting.getStatus()).thenReturn(PUBLISHED);
        when(closedPosting.getStatus()).thenReturn(CLOSED);
        when(jobPostingRepository.findAll()).thenReturn(List.of(publishedPosting, closedPosting));
        when(modelMapper.map(publishedPosting, JobPostingDto.class)).thenReturn(dto);

        List<JobPostingDto> result = jobPostingService.findAllPublished();

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(jobPostingRepository).findAll();
        verify(modelMapper).map(publishedPosting, JobPostingDto.class);
        verifyNoMoreInteractions(jobPostingRepository, modelMapper);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("findAllPublished returns empty list when no job postings are published")
    void findAllPublishedReturnsEmptyListWhenNonePublished() {
        JobPosting closedPosting = mock(JobPosting.class);

        when(closedPosting.getStatus()).thenReturn(CLOSED);
        when(jobPostingRepository.findAll()).thenReturn(List.of(closedPosting));

        List<JobPostingDto> result = jobPostingService.findAllPublished();

        assertTrue(result.isEmpty());

        verify(jobPostingRepository).findAll();
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("create creates and returns job posting when expiration date is valid")
    void createSavesJobPostingWhenExpirationIsValid() {
        String email = "marko@gmail.com";
        Company company = new Company("Firma ABC", "Informacije...", "Hrastova 22");

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), company
        );

        CreateJobPostingRequest request = new CreateJobPostingRequest();
        request.setTitle("Software Engineer");
        request.setDescription("Job description here");
        request.setDateOfExpiration(LocalDate.now().plusDays(30));

        JobPostingDto dto = new JobPostingDto();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(employeeRepository.findByEmail(email)).thenReturn(employee);
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(JobPosting.class), eq(JobPostingDto.class))).thenReturn(dto);

        JobPostingDto result;
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            result = jobPostingService.create(request);
        }

        assertEquals(dto, result);

        ArgumentCaptor<JobPosting> captor = ArgumentCaptor.forClass(JobPosting.class);
        verify(jobPostingRepository).save(captor.capture());

        JobPosting saved = captor.getValue();
        assertEquals("Software Engineer", saved.getTitle());
        assertEquals("Job description here", saved.getDescription());
        assertEquals(LocalDate.now(), saved.getDateOfPublishing());
        assertEquals(request.getDateOfExpiration(), saved.getDateOfExpiration());
        assertEquals(company, saved.getCompany());

        verify(employeeRepository).findByEmail(email);
        verify(modelMapper).map(any(JobPosting.class), eq(JobPostingDto.class));
        verifyNoMoreInteractions(employeeRepository, jobPostingRepository, modelMapper);
    }

    @Test
    @DisplayName("create throws ConflictException when expiration date is in the past")
    void createThrowsConflictExceptionForPastExpirationDate() {
        CreateJobPostingRequest request = new CreateJobPostingRequest();
        request.setTitle("Software Engineer");
        request.setDescription("Job description here");
        request.setDateOfExpiration(LocalDate.now().minusDays(1));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobPostingService.create(request)
        );
        assertEquals("Invalid date of expiration", exception.getMessage());

        verifyNoInteractions(jobPostingRepository, employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteById deletes job posting when it exists")
    void deleteByIdDeletesJobPosting() {
        Long id = 1L;

        when(jobPostingRepository.findById(id)).thenReturn(Optional.of(jobPosting));

        jobPostingService.deleteById(id);

        verify(jobPostingRepository).findById(id);
        verify(jobPostingRepository).delete(jobPosting);
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteById throws ResourceNotFoundException when job posting does not exist")
    void deleteByIdThrowsResourceNotFoundException() {
        Long id = 1L;

        when(jobPostingRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobPostingService.deleteById(id)
        );
        assertEquals("Job posting not found with id " + id, exception.getMessage());

        verify(jobPostingRepository).findById(id);
        verify(jobPostingRepository, never()).delete(any());
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("updateById updates and saves job posting when expiration date is valid")
    void updateByIdUpdatesJobPosting() {
        Long id = 1L;
        Company company = new Company("Firma", "...", "Adresa");

        JobPosting existingPosting = new JobPosting(
                "Stari naslov", "Stari opis", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(5), company
        );

        UpdateJobPostingRequest request = new UpdateJobPostingRequest();
        request.setTitle("Novi naslov");
        request.setDescription("Novi opis");
        request.setDateOfExpiration(LocalDate.now().plusDays(60));

        JobPostingDto dto = new JobPostingDto();

        when(jobPostingRepository.findById(id)).thenReturn(Optional.of(existingPosting));
        when(jobPostingRepository.save(existingPosting)).thenReturn(existingPosting);
        when(modelMapper.map(existingPosting, JobPostingDto.class)).thenReturn(dto);

        jobPostingService.updateById(id, request);

        assertEquals("Novi naslov", existingPosting.getTitle());
        assertEquals("Novi opis", existingPosting.getDescription());
        assertEquals(request.getDateOfExpiration(), existingPosting.getDateOfExpiration());

        verify(jobPostingRepository).findById(id);
        verify(jobPostingRepository).save(existingPosting);
        verify(modelMapper).map(existingPosting, JobPostingDto.class);
        verifyNoMoreInteractions(jobPostingRepository, modelMapper);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("updateById throws ConflictException when expiration date is in the past")
    void updateByIdThrowsConflictExceptionForPastExpirationDate() {
        Long id = 1L;

        UpdateJobPostingRequest request = new UpdateJobPostingRequest();
        request.setTitle("Novi naslov");
        request.setDescription("Novi opis");
        request.setDateOfExpiration(LocalDate.now().minusDays(1));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobPostingService.updateById(id, request)
        );
        assertEquals("Expiration date cannot be set before current time", exception.getMessage());

        verifyNoInteractions(jobPostingRepository, employeeRepository, modelMapper);
    }

    @Test
    @DisplayName("updateById throws ResourceNotFoundException when job posting does not exist")
    void updateByIdThrowsResourceNotFoundException() {
        Long id = 1L;

        UpdateJobPostingRequest request = new UpdateJobPostingRequest();
        request.setTitle("Novi naslov");
        request.setDescription("Novi opis");
        request.setDateOfExpiration(LocalDate.now().plusDays(30));

        when(jobPostingRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobPostingService.updateById(id, request)
        );
        assertEquals("Job posting not found with id " + id, exception.getMessage());

        verify(jobPostingRepository).findById(id);
        verify(jobPostingRepository, never()).save(any());
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(employeeRepository, modelMapper);
    }
}