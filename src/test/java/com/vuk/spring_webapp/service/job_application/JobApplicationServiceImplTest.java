package com.vuk.spring_webapp.service.job_application;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.domain.user.Sex;
import com.vuk.spring_webapp.exception.*;
import com.vuk.spring_webapp.repository.CandidateRepository;
import com.vuk.spring_webapp.repository.EmployeeRepository;
import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.repository.JobPostingRepository;
import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.transfer.dto.CandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationCandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationEmployeeDto;
import com.vuk.spring_webapp.transfer.request.SubmitJobApplicationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;
import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.CLOSED;
import static com.vuk.spring_webapp.domain.job_posting.JobPostingStatus.PUBLISHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobApplicationServiceImpl Unit Tests")
class JobApplicationServiceImplTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;
    @Mock
    private JobPostingRepository jobPostingRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CandidateService candidateService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JobPosting jobPosting;

    @InjectMocks
    private JobApplicationServiceImpl jobApplicationService;

    private Long candidateId;
    private Candidate candidate;
    private Long jobPostingId;
    private SubmitJobApplicationRequest submitJobApplicationRequest;
    private Long jobApplicationId;
    private Long otherJobApplicationId;
    private Long employeeId;
    private Long otherEmployeeId;
    private Employee employee;
    private Employee otherEmployee;

    @BeforeEach
    void setUp() {
        candidateId = 1L;
        candidate = new Candidate(
                "Nikola", "Nikolić", Sex.MALE, "381000111", "Ulica 1", "nikola@gmail.com", "encodedPassword"
        );
        candidate.setId(candidateId);

        jobPostingId = 1L;

        submitJobApplicationRequest = new SubmitJobApplicationRequest();
        submitJobApplicationRequest.setJobPostingId(jobPostingId);

        jobApplicationId = 1L;
        otherJobApplicationId = 2L;

        employeeId = 1L;
        otherEmployeeId = 2L;
        employee = new Employee(
                "Uroš", "Rakić", Sex.MALE, "38166444222", "Ulica 200", "uros@gmail.com",
                "encodedPassword", "1101000601101", LocalDate.of(1985, 3, 20),
                LocalDate.of(2019, 6, 1), null
        );
        employee.setId(employeeId);
        otherEmployee = new Employee(
                "Eva", "Katic", Sex.FEMALE, "381654440012", "Ulica 44", "eva4@yahoo.com",
                "encodedPassword", "0309999001123", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        otherEmployee.setId(otherEmployeeId);
    }

    @Test
    @DisplayName("submitJobApplication saves job application when candidate has not applied yet and posting is open")
    void submitJobApplicationSavesApplication() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)).thenReturn(false);
        when(jobPosting.getStatus()).thenReturn(PUBLISHED);

        jobApplicationService.submitJobApplication(candidateId, submitJobApplicationRequest);

        ArgumentCaptor<JobApplication> captor = ArgumentCaptor.forClass(JobApplication.class);
        verify(jobApplicationRepository).save(captor.capture());

        JobApplication saved = captor.getValue();
        assertEquals(LocalDate.now(), saved.getDateOfSubmission());
        assertEquals(SUBMITTED, saved.getStatus());
        assertEquals(jobPosting, saved.getJobPosting());
        assertNull(saved.getEmployee());
        assertEquals(candidate, saved.getCandidate());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(candidateRepository).findById(candidateId);
        verify(jobApplicationRepository).existsByCandidateIdAndJobPostingId(candidateId, jobPostingId);
        verifyNoMoreInteractions(jobPostingRepository, candidateRepository, jobApplicationRepository);
        verifyNoInteractions(employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("submitJobApplication throws ResourceNotFoundException when job posting does not exist")
    void submitJobApplicationThrowsResourceNotFoundExceptionForMissingJobPosting() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, submitJobApplicationRequest)
        );
        assertEquals("Job posting not found", exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(candidateRepository, jobApplicationRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("submitJobApplication throws ResourceNotFoundException when candidate does not exist")
    void submitJobApplicationThrowsResourceNotFoundExceptionForMissingCandidate() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, submitJobApplicationRequest)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(jobPostingRepository, candidateRepository);
        verifyNoInteractions(jobApplicationRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("submitJobApplication throws ApplicationExistsException when candidate already applied for the job posting")
    void submitJobApplicationThrowsApplicationExistsException() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)).thenReturn(true);

        ApplicationExistsException exception = assertThrows(
                ApplicationExistsException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, submitJobApplicationRequest)
        );
        assertEquals("You already applied for this job posting", exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(candidateRepository).findById(candidateId);
        verify(jobApplicationRepository).existsByCandidateIdAndJobPostingId(candidateId, jobPostingId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(jobPostingRepository, candidateRepository, jobApplicationRepository);
        verifyNoInteractions(employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("submitJobApplication throws JobPostingClosedException when job posting is closed")
    void submitJobApplicationThrowsJobPostingClosedException() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)).thenReturn(false);
        when(jobPosting.getStatus()).thenReturn(CLOSED);

        JobPostingClosedException exception = assertThrows(
                JobPostingClosedException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, submitJobApplicationRequest)
        );
        assertEquals("Job posting closed", exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(candidateRepository).findById(candidateId);
        verify(jobApplicationRepository).existsByCandidateIdAndJobPostingId(candidateId, jobPostingId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(jobPostingRepository, candidateRepository, jobApplicationRepository);
        verifyNoInteractions(employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getAllByCandidateId returns mapped job application list when candidate exists")
    void getAllByCandidateIdReturnsJobApplications() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        candidate.setJobApplications(List.of(application));

        JobApplicationCandidateDto dto = new JobApplicationCandidateDto();

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(modelMapper.map(application, JobApplicationCandidateDto.class)).thenReturn(dto);

        List<JobApplicationCandidateDto> result = jobApplicationService.getAllByCandidateId(candidateId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(candidateRepository).findById(candidateId);
        verify(modelMapper).map(application, JobApplicationCandidateDto.class);
        verifyNoMoreInteractions(candidateRepository, modelMapper);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getAllByCandidateId throws ResourceNotFoundException when candidate does not exist")
    void getAllByCandidateIdThrowsResourceNotFoundException() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getAllByCandidateId(candidateId)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getByIdForCandidate returns mapped job application when candidate owns it")
    void getByIdForCandidateReturnsJobApplication() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        application.setId(jobApplicationId);
        candidate.setJobApplications(List.of(application));

        JobApplicationCandidateDto dto = new JobApplicationCandidateDto();

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(modelMapper.map(application, JobApplicationCandidateDto.class)).thenReturn(dto);

        JobApplicationCandidateDto result = jobApplicationService.getByIdForCandidate(candidateId, jobApplicationId);

        assertEquals(dto, result);

        verify(candidateRepository).findById(candidateId);
        verify(modelMapper).map(application, JobApplicationCandidateDto.class);
        verifyNoMoreInteractions(candidateRepository, modelMapper);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getByIdForCandidate throws ResourceNotFoundException when candidate does not exist")
    void getByIdForCandidateThrowsResourceNotFoundExceptionForMissingCandidate() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getByIdForCandidate(candidateId, jobApplicationId)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getByIdForCandidate throws ResourceNotFoundException when job application does not belong to candidate")
    void getByIdForCandidateThrowsResourceNotFoundExceptionForMissingApplication() {
        JobApplication otherApplication = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        otherApplication.setId(otherJobApplicationId);
        candidate.setJobApplications(List.of(otherApplication));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getByIdForCandidate(candidateId, jobApplicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("deleteById deletes job application when candidate owns it and status allows deletion")
    void deleteByIdDeletesJobApplication() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        application.setId(jobApplicationId);
        candidate.setJobApplications(List.of(application));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        jobApplicationService.deleteById(candidateId, jobApplicationId);

        verify(candidateRepository).findById(candidateId);
        verify(jobApplicationRepository).deleteById(jobApplicationId);
        verifyNoMoreInteractions(candidateRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("deleteById throws ResourceNotFoundException when candidate does not exist")
    void deleteByIdThrowsResourceNotFoundExceptionForMissingCandidate() {
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.deleteById(candidateId, jobApplicationId)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("deleteById throws ResourceNotFoundException when job application does not belong to candidate")
    void deleteByIdThrowsResourceNotFoundExceptionForMissingApplication() {
        JobApplication otherApplication = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        otherApplication.setId(otherJobApplicationId);
        candidate.setJobApplications(List.of(otherApplication));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.deleteById(candidateId, jobApplicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @ParameterizedTest(name = "deleteById throws ConflictException when status is {0}")
    @EnumSource(value = JobApplicationStatus.class, names = {"OFFERED", "ACCEPTED", "REJECTED"})
    @DisplayName("Throws ConflictException when status disallows deletion")
    void deleteByIdThrowsConflictExceptionForDisallowedStatus(JobApplicationStatus status) {
        JobApplication application = new JobApplication(
                LocalDate.now(), status, null, null, candidate
        );
        application.setId(jobApplicationId);
        candidate.setJobApplications(List.of(application));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.deleteById(candidateId, jobApplicationId)
        );
        assertEquals("Job application cannot be deleted if state is OFFERED, ACCEPTED or REJECTED", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verify(jobApplicationRepository, never()).deleteById(any());
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getUnmanagedApplicationsByJobPosting returns mapped unmanaged job applications when job posting exists and has applications")
    void getUnmanagedApplicationsByJobPostingReturnsApplications() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        JobApplicationEmployeeDto dto = new JobApplicationEmployeeDto();

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(jobApplicationRepository.findByEmployeeAndJobPostingId(null, jobPostingId)).thenReturn(List.of(application));
        when(modelMapper.map(application, JobApplicationEmployeeDto.class)).thenReturn(dto);

        List<JobApplicationEmployeeDto> result = jobApplicationService.getUnmanagedApplicationsByJobPosting(jobPostingId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobApplicationRepository).findByEmployeeAndJobPostingId(null, jobPostingId);
        verify(modelMapper).map(application, JobApplicationEmployeeDto.class);
        verifyNoMoreInteractions(jobPostingRepository, jobApplicationRepository, modelMapper);
        verifyNoInteractions(candidateRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getUnmanagedApplicationsByJobPosting throws ResourceNotFoundException when job posting does not exist")
    void getUnmanagedApplicationsByJobPostingThrowsResourceNotFoundExceptionForMissingPosting() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getUnmanagedApplicationsByJobPosting(jobPostingId)
        );
        assertEquals("Job posting not found", exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(jobApplicationRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getUnmanagedApplicationsByJobPosting throws ResourceNotFoundException when there are no unmanaged applications")
    void getUnmanagedApplicationsByJobPostingThrowsResourceNotFoundExceptionForEmptyList() {
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(jobApplicationRepository.findByEmployeeAndJobPostingId(null, jobPostingId)).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getUnmanagedApplicationsByJobPosting(jobPostingId)
        );
        assertEquals("Job applications not found", exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobApplicationRepository).findByEmployeeAndJobPostingId(null, jobPostingId);
        verifyNoMoreInteractions(jobPostingRepository, jobApplicationRepository);
        verifyNoInteractions(candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getUnmanagedApplicationById returns mapped job application when it is unmanaged")
    void getUnmanagedApplicationByIdReturnsApplication() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        JobApplicationEmployeeDto dto = new JobApplicationEmployeeDto();

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
        when(modelMapper.map(application, JobApplicationEmployeeDto.class)).thenReturn(dto);

        JobApplicationEmployeeDto result = jobApplicationService.getUnmanagedApplicationById(jobApplicationId);

        assertEquals(dto, result);

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(modelMapper).map(application, JobApplicationEmployeeDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, modelMapper);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getUnmanagedApplicationById throws ResourceNotFoundException when job application does not exist")
    void getUnmanagedApplicationByIdThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getUnmanagedApplicationById(jobApplicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getUnmanagedApplicationById throws ConflictException when job application is already managed")
    void getUnmanagedApplicationByIdThrowsConflictException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.getUnmanagedApplicationById(jobApplicationId)
        );
        assertEquals("This job application is already managed", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee returns mapped job application when employee manages it")
    void getManagedApplicationByIdForEmployeeReturnsApplication() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        JobApplicationEmployeeDto dto = new JobApplicationEmployeeDto();

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
        when(modelMapper.map(application, JobApplicationEmployeeDto.class)).thenReturn(dto);

        JobApplicationEmployeeDto result = jobApplicationService.getManagedApplicationByIdForEmployee(jobApplicationId, employeeId);

        assertEquals(dto, result);

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(modelMapper).map(application, JobApplicationEmployeeDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, modelMapper);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee throws ResourceNotFoundException when job application does not exist")
    void getManagedApplicationByIdForEmployeeThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getManagedApplicationByIdForEmployee(jobApplicationId, employeeId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee throws ConflictException when job application is not managed")
    void getManagedApplicationByIdForEmployeeThrowsConflictException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.getManagedApplicationByIdForEmployee(jobApplicationId, employeeId)
        );
        assertEquals("This job application is not managed", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee throws UnauthorizedException when another employee manages the job application")
    void getManagedApplicationByIdForEmployeeThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.getManagedApplicationByIdForEmployee(jobApplicationId, employeeId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationsByEmployee returns mapped job applications when employee manages some")
    void getManagedApplicationsByEmployeeReturnsApplications() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, null, null
        );

        JobApplicationEmployeeDto dto = new JobApplicationEmployeeDto();

        when(jobApplicationRepository.findByEmployeeId(employeeId)).thenReturn(List.of(application));
        when(modelMapper.map(any(), eq(JobApplicationEmployeeDto.class))).thenReturn(dto);

        List<JobApplicationEmployeeDto> result = jobApplicationService.getManagedApplicationsByEmployee(employeeId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(jobApplicationRepository).findByEmployeeId(employeeId);
        verify(modelMapper, times(2)).map(any(), eq(JobApplicationEmployeeDto.class));
        verifyNoMoreInteractions(jobApplicationRepository, modelMapper);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getManagedApplicationsByEmployee throws ResourceNotFoundException when employee manages no applications")
    void getManagedApplicationsByEmployeeThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findByEmployeeId(employeeId)).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getManagedApplicationsByEmployee(employeeId)
        );
        assertEquals("No managed job applications found", exception.getMessage());

        verify(jobApplicationRepository).findByEmployeeId(employeeId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("setEmployee assigns employee and updates status when application is unmanaged")
    void setEmployeeAssignsEmployeeAndUpdatesStatus() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        jobApplicationService.setEmployee(employeeId, jobApplicationId);

        assertEquals(employee, application.getEmployee());
        assertEquals(UNDER_REVIEW, application.getStatus());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository).save(application);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("setEmployee throws ResourceNotFoundException when employee does not exist")
    void setEmployeeThrowsResourceNotFoundExceptionForMissingEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.setEmployee(employeeId, jobApplicationId)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("setEmployee throws ResourceNotFoundException when job application does not exist")
    void setEmployeeThrowsResourceNotFoundExceptionForMissingApplication() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.setEmployee(employeeId, jobApplicationId)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("setEmployee throws ConflictException when job application is already managed")
    void setEmployeeThrowsConflictException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.setEmployee(employeeId, jobApplicationId)
        );
        assertEquals("This application is already managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus updates status when application is managed by employee and change is allowed")
    void updateApplicationStatusUpdatesStatus() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        jobApplicationService.updateApplicationStatus(employeeId, jobApplicationId, INTERVIEW_SCHEDULED);

        assertEquals(INTERVIEW_SCHEDULED, application.getStatus());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository).save(application);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ResourceNotFoundException when employee does not exist")
    void updateApplicationStatusThrowsResourceNotFoundExceptionForMissingEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, jobApplicationId, REJECTED)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ResourceNotFoundException when job application does not exist")
    void updateApplicationStatusThrowsResourceNotFoundExceptionForMissingApplication() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, jobApplicationId, REJECTED)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ConflictException when application is not managed")
    void updateApplicationStatusThrowsConflictExceptionForUnmanagedApplication() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, jobApplicationId, REJECTED)
        );
        assertEquals("This application is not managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws UnauthorizedException when another employee manages the job application")
    void updateApplicationStatusThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, jobApplicationId, REJECTED)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ConflictException when application status is final")
    void updateApplicationStatusThrowsConflictExceptionForFinalStatus() {
        JobApplication application = new JobApplication(
                LocalDate.now(), ACCEPTED, null, employee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, jobApplicationId, REJECTED)
        );
        assertEquals("You cannot edit this application's status", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ConflictException when status change is not allowed")
    void updateApplicationStatusThrowsConflictExceptionForDisallowedTransition() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, employee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, jobApplicationId, ACCEPTED)
        );
        assertEquals("Status change not allowed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication returns candidate profile when application is managed by employee")
    void getCandidateProfileForApplicationReturnsProfile() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, candidate
        );

        CandidateDto dto = new CandidateDto();
        dto.setId(candidateId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
        when(candidateService.findById(candidateId)).thenReturn(dto);

        CandidateDto result = jobApplicationService.getCandidateProfileForApplication(employeeId, jobApplicationId);

        assertEquals(dto, result);

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(candidateService).findById(candidateId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository, candidateService);
        verifyNoInteractions(jobPostingRepository, candidateRepository, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws ResourceNotFoundException when employee does not exist")
    void getCandidateProfileForApplicationThrowsResourceNotFoundExceptionForMissingEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, jobApplicationId)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws ResourceNotFoundException when job application does not exist")
    void getCandidateProfileForApplicationThrowsResourceNotFoundExceptionForMissingApplication() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, jobApplicationId)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws ConflictException when application is not managed")
    void getCandidateProfileForApplicationThrowsConflictException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, jobApplicationId)
        );
        assertEquals("This application is not managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws UnauthorizedException when another employee manages the job application")
    void getCandidateProfileForApplicationThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, jobApplicationId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume returns resume when application is managed by employee")
    void loadResumeReturnsResource() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, candidate
        );

        Resource resource = new ByteArrayResource("PDF content".getBytes());

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
        when(candidateService.loadResume(candidateId)).thenReturn(resource);

        Resource result = jobApplicationService.loadResume(employeeId, jobApplicationId);

        assertEquals(resource, result);

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(candidateService).loadResume(candidateId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository, candidateService);
        verifyNoInteractions(jobPostingRepository, candidateRepository, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ResourceNotFoundException when employee does not exist")
    void loadResumeThrowsResourceNotFoundExceptionForMissingEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.loadResume(employeeId, jobApplicationId)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ResourceNotFoundException when job application does not exist")
    void loadResumeThrowsResourceNotFoundExceptionForMissingApplication() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.loadResume(employeeId, jobApplicationId)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ConflictException when application is not managed")
    void loadResumeThrowsConflictException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.loadResume(employeeId, jobApplicationId)
        );
        assertEquals("This application is not managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws UnauthorizedException when another employee manages the job application")
    void loadResumeThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.loadResume(employeeId, jobApplicationId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }
}
