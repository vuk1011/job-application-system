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
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    @DisplayName("submitJobApplication saves job application when candidate has not applied yet and posting is open")
    void submitJobApplicationSavesApplication() {
        Long candidateId = 1L;
        Long jobPostingId = 10L;

        Candidate candidate = new Candidate(
                "Nikola", "Nikolic", Sex.MALE, "381000111", "Ulica 1", "nikola@gmail.com", "encodedPassword"
        );

        SubmitJobApplicationRequest request = new SubmitJobApplicationRequest();
        request.setJobPostingId(jobPostingId);

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)).thenReturn(false);
        when(jobPosting.getStatus()).thenReturn(PUBLISHED);

        jobApplicationService.submitJobApplication(candidateId, request);

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
        Long candidateId = 1L;
        Long jobPostingId = 10L;

        SubmitJobApplicationRequest request = new SubmitJobApplicationRequest();
        request.setJobPostingId(jobPostingId);

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, request)
        );
        assertEquals("Job posting not found", exception.getMessage());

        verify(jobPostingRepository).findById(jobPostingId);
        verifyNoMoreInteractions(jobPostingRepository);
        verifyNoInteractions(candidateRepository, jobApplicationRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("submitJobApplication throws ResourceNotFoundException when candidate does not exist")
    void submitJobApplicationThrowsResourceNotFoundExceptionForMissingCandidate() {
        Long candidateId = 1L;
        Long jobPostingId = 10L;

        SubmitJobApplicationRequest request = new SubmitJobApplicationRequest();
        request.setJobPostingId(jobPostingId);

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, request)
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
        Long candidateId = 1L;
        Long jobPostingId = 10L;

        Candidate candidate = new Candidate(
                "Jovan", "Filipovic", Sex.MALE, "38164333111", "Kralja Milana 30", "jovan@gmail.com", "encodedPassword"
        );

        SubmitJobApplicationRequest request = new SubmitJobApplicationRequest();
        request.setJobPostingId(jobPostingId);

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)).thenReturn(true);

        ApplicationExistsException exception = assertThrows(
                ApplicationExistsException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, request)
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
        Long candidateId = 1L;
        Long jobPostingId = 10L;

        Candidate candidate = new Candidate(
                "Aleksa", "Aleksic", Sex.MALE, "38162123321", "Ulica 33", "aleksaa@yahoo.com", "encodedPassword"
        );

        SubmitJobApplicationRequest request = new SubmitJobApplicationRequest();
        request.setJobPostingId(jobPostingId);

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)).thenReturn(false);
        when(jobPosting.getStatus()).thenReturn(CLOSED);

        JobPostingClosedException exception = assertThrows(
                JobPostingClosedException.class,
                () -> jobApplicationService.submitJobApplication(candidateId, request)
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
        Long candidateId = 1L;

        Candidate candidate = new Candidate(
                "Jovan", "Pajic", Sex.MALE, "38162444555", "Ulica 22", "jovanp@gmail.com", "encodedPassword"
        );

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
        Long candidateId = 1L;

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
        Long candidateId = 1L;
        Long applicationId = 100L;

        Candidate candidate = new Candidate(
                "Vukasin", "Milic", Sex.MALE, "38166123456", "Bulevar 123", "vukasin@gmail.com", "encodedPassword"
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        application.setId(applicationId);
        candidate.setJobApplications(List.of(application));

        JobApplicationCandidateDto dto = new JobApplicationCandidateDto();

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(modelMapper.map(application, JobApplicationCandidateDto.class)).thenReturn(dto);

        JobApplicationCandidateDto result = jobApplicationService.getByIdForCandidate(candidateId, applicationId);

        assertEquals(dto, result);

        verify(candidateRepository).findById(candidateId);
        verify(modelMapper).map(application, JobApplicationCandidateDto.class);
        verifyNoMoreInteractions(candidateRepository, modelMapper);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getByIdForCandidate throws ResourceNotFoundException when candidate does not exist")
    void getByIdForCandidateThrowsResourceNotFoundExceptionForMissingCandidate() {
        Long candidateId = 1L;
        Long applicationId = 100L;

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getByIdForCandidate(candidateId, applicationId)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getByIdForCandidate throws ResourceNotFoundException when job application does not belong to candidate")
    void getByIdForCandidateThrowsResourceNotFoundExceptionForMissingApplication() {
        Long candidateId = 1L;
        Long applicationId = 100L;
        Long otherApplicationId = 200L;

        Candidate candidate = new Candidate(
                "Nikola", "Nikolic", Sex.MALE, "38166000333", "Ulica 11", "nikolan@gmail.com", "encodedPassword"
        );

        JobApplication otherApplication = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        otherApplication.setId(otherApplicationId);
        candidate.setJobApplications(List.of(otherApplication));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getByIdForCandidate(candidateId, applicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("deleteById deletes job application when candidate owns it and status allows deletion")
    void deleteByIdDeletesJobApplication() {
        Long candidateId = 1L;
        Long applicationId = 100L;

        Candidate candidate = new Candidate(
                "Ivan", "Ivanovic", Sex.MALE, "38163111222", "Ulica 1", "ivan1@gmail.com", "encodedPassword"
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        application.setId(applicationId);
        candidate.setJobApplications(List.of(application));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        jobApplicationService.deleteById(candidateId, applicationId);

        verify(candidateRepository).findById(candidateId);
        verify(jobApplicationRepository).deleteById(applicationId);
        verifyNoMoreInteractions(candidateRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("deleteById throws ResourceNotFoundException when candidate does not exist")
    void deleteByIdThrowsResourceNotFoundExceptionForMissingCandidate() {
        Long candidateId = 1L;
        Long applicationId = 100L;

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.deleteById(candidateId, applicationId)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("deleteById throws ResourceNotFoundException when job application does not belong to candidate")
    void deleteByIdThrowsResourceNotFoundExceptionForMissingApplication() {
        Long candidateId = 1L;
        Long applicationId = 100L;
        Long otherApplicationId = 200L;

        Candidate candidate = new Candidate(
                "Iva", "Ivic", Sex.FEMALE, "38166555111", "Francuska 3", "iva@gmail.com", "encodedPassword"
        );

        JobApplication otherApplication = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        otherApplication.setId(otherApplicationId);
        candidate.setJobApplications(List.of(otherApplication));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.deleteById(candidateId, applicationId)
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
        Long candidateId = 1L;
        Long applicationId = 100L;

        Candidate candidate = new Candidate(
                "Ivan", "Ivanovic", Sex.MALE, "38162123123", "Ulica 50", "ivan22@gmail.com", "encodedPassword"
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), status, null, null, candidate
        );
        application.setId(applicationId);
        candidate.setJobApplications(List.of(application));

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.deleteById(candidateId, applicationId)
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
        Long jobPostingId = 10L;

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
        Long jobPostingId = 10L;

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
        Long jobPostingId = 10L;

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
        Long applicationId = 100L;

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        JobApplicationEmployeeDto dto = new JobApplicationEmployeeDto();

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(modelMapper.map(application, JobApplicationEmployeeDto.class)).thenReturn(dto);

        JobApplicationEmployeeDto result = jobApplicationService.getUnmanagedApplicationById(applicationId);

        assertEquals(dto, result);

        verify(jobApplicationRepository).findById(applicationId);
        verify(modelMapper).map(application, JobApplicationEmployeeDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, modelMapper);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getUnmanagedApplicationById throws ResourceNotFoundException when job application does not exist")
    void getUnmanagedApplicationByIdThrowsResourceNotFoundException() {
        Long applicationId = 100L;

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getUnmanagedApplicationById(applicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getUnmanagedApplicationById yhrows ConflictException when job application is already managed")
    void getUnmanagedApplicationByIdThrowsConflictException() {
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Jelena", "Rajic", Sex.FEMALE, "38163333123", "Ulica 31", "jelena@gmail.com",
                "encodedPassword", "3004998213001", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.getUnmanagedApplicationById(applicationId)
        );
        assertEquals("This job application is already managed", exception.getMessage());

        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee returns mapped job application when employee manages it")
    void getManagedApplicationByIdForEmployeeReturnsApplication() {
        Long applicationId = 100L;
        Long employeeId = 1L;

        Employee employee = new Employee(
                "Ana", "Antic", Sex.FEMALE, "38162123123", "Ulica 1", "ana@yahoo.com",
                "encodedPassword", "1010001210189", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        JobApplicationEmployeeDto dto = new JobApplicationEmployeeDto();

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(modelMapper.map(application, JobApplicationEmployeeDto.class)).thenReturn(dto);

        JobApplicationEmployeeDto result = jobApplicationService.getManagedApplicationByIdForEmployee(applicationId, employeeId);

        assertEquals(dto, result);

        verify(jobApplicationRepository).findById(applicationId);
        verify(modelMapper).map(application, JobApplicationEmployeeDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, modelMapper);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee throws ResourceNotFoundException when job application does not exist")
    void getManagedApplicationByIdForEmployeeThrowsResourceNotFoundException() {
        Long applicationId = 100L;
        Long employeeId = 1L;

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getManagedApplicationByIdForEmployee(applicationId, employeeId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee throws ConflictException when job application is not managed")
    void getManagedApplicationByIdForEmployeeThrowsConflictException() {
        Long applicationId = 100L;
        Long employeeId = 1L;

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.getManagedApplicationByIdForEmployee(applicationId, employeeId)
        );
        assertEquals("This job application is not managed", exception.getMessage());

        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationByIdForEmployee throws UnauthorizedException when another employee manages the job application")
    void getManagedApplicationByIdForEmployeeThrowsUnauthorizedException() {
        Long applicationId = 100L;
        Long employeeId = 1L;
        Long otherEmployeeId = 2L;

        Employee otherEmployee = new Employee(
                "Uros", "Rakic", Sex.MALE, "38166444222", "Ulica 200", "uros@gmail.com",
                "encodedPassword", "1101000601101", LocalDate.of(1985, 3, 20),
                LocalDate.of(2019, 6, 1), null
        );
        ReflectionTestUtils.setField(otherEmployee, "id", otherEmployeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        // when / then
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.getManagedApplicationByIdForEmployee(applicationId, employeeId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, employeeRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getManagedApplicationsByEmployee returns mapped job applications when employee manages some")
    void getManagedApplicationsByEmployeeReturnsApplications() {
        Long employeeId = 1L;

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
        Long employeeId = 1L;

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
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Simic", Sex.FEMALE, "38162321123", "Ulica 4", "ana@firma.com",
                "encodedPassword", "1001988110321", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        jobApplicationService.setEmployee(employeeId, applicationId);

        assertEquals(employee, application.getEmployee());
        assertEquals(UNDER_REVIEW, application.getStatus());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(jobApplicationRepository).save(application);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("setEmployee throws ResourceNotFoundException when employee does not exist")
    void setEmployeeThrowsResourceNotFoundExceptionForMissingEmployee() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.setEmployee(employeeId, applicationId)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("setEmployee throws ResourceNotFoundException when job application does not exist")
    void setEmployeeThrowsResourceNotFoundExceptionForMissingApplication() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Simic", Sex.FEMALE, "38166001122", "Bulevar 44", "anaas@gmail.com",
                "encodedPassword", "0103979222004", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.setEmployee(employeeId, applicationId)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("setEmployee throws ConflictException when job application is already managed")
    void setEmployeeThrowsConflictException() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Maja", "Stanic", Sex.FEMALE, "38162123123", "Ulica 3", "maja4@gmail.com",
                "encodedPassword", "0505995200300", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        Employee existingManager = new Employee(
                "Marko", "Stanojevic", Sex.MALE, "38163123123", "Put 5", "marko6@gmail.com",
                "encodedPassword", "0505985999023", LocalDate.of(1985, 3, 20),
                LocalDate.of(2019, 6, 1), null
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, existingManager, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.setEmployee(employeeId, applicationId)
        );
        assertEquals("This application is already managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus updates status when application is managed by employee and change is allowed")
    void updateApplicationStatusUpdatesStatus() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Marija", "Matic", Sex.FEMALE, "38162321444", "Put 10", "marija1@gmail.com",
                "encodedPassword", "0606994001333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        jobApplicationService.updateApplicationStatus(employeeId, applicationId, INTERVIEW_SCHEDULED);

        assertEquals(INTERVIEW_SCHEDULED, application.getStatus());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(jobApplicationRepository).save(application);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ResourceNotFoundException when employee does not exist")
    void updateApplicationStatusThrowsResourceNotFoundExceptionForMissingEmployee() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, applicationId, REJECTED)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ResourceNotFoundException when job application does not exist")
    void updateApplicationStatusThrowsResourceNotFoundExceptionForMissingApplication() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Eva", "Katic", Sex.FEMALE, "381654440012", "Ulica 44", "eva4@yahoo.com",
                "encodedPassword", "0309999001123", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, applicationId, REJECTED)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ConflictException when application is not managed")
    void updateApplicationStatusThrowsConflictExceptionForUnmanagedApplication() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, applicationId, REJECTED)
        );
        assertEquals("This application is not managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws UnauthorizedException when another employee manages the job application")
    void updateApplicationStatusThrowsUnauthorizedException() {
        Long employeeId = 1L;
        Long otherEmployeeId = 2L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        Employee otherEmployee = new Employee(
                "Eva", "Katic", Sex.FEMALE, "381654440012", "Ulica 44", "eva4@yahoo.com",
                "encodedPassword", "0309999001123", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(otherEmployee, "id", otherEmployeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, applicationId, REJECTED)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ConflictException when application status is final")
    void updateApplicationStatusThrowsConflictExceptionForFinalStatus() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), ACCEPTED, null, employee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, applicationId, REJECTED)
        );
        assertEquals("You cannot edit this application's status", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("updateApplicationStatus throws ConflictException when status change is not allowed")
    void updateApplicationStatusThrowsConflictExceptionForDisallowedTransition() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, employee, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.updateApplicationStatus(employeeId, applicationId, ACCEPTED)
        );
        assertEquals("Status change not allowed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(jobApplicationRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication returns candidate profile when application is managed by employee")
    void getCandidateProfileForApplicationReturnsProfile() {
        Long employeeId = 1L;
        Long applicationId = 100L;
        Long candidateId = 50L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        Candidate candidate = new Candidate(
                "Nikola", "Rokvic", Sex.MALE, "381660010070", "Bulevar 23", "rokvicn@gmail.com", "encodedPassword"
        );
        ReflectionTestUtils.setField(candidate, "id", candidateId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, candidate
        );

        CandidateDto dto = new CandidateDto();
        dto.setId(candidateId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(candidateService.findById(candidateId)).thenReturn(dto);

        CandidateDto result = jobApplicationService.getCandidateProfileForApplication(employeeId, applicationId);

        assertEquals(dto, result);

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(candidateService).findById(candidateId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository, candidateService);
        verifyNoInteractions(jobPostingRepository, candidateRepository, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws ResourceNotFoundException when employee does not exist")
    void getCandidateProfileForApplicationThrowsResourceNotFoundExceptionForMissingEmployee() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, applicationId)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws ResourceNotFoundException when job application does not exist")
    void getCandidateProfileForApplicationThrowsResourceNotFoundExceptionForMissingApplication() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, applicationId)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws ConflictException when application is not managed")
    void getCandidateProfileForApplicationThrowsConflictException() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, applicationId)
        );
        assertEquals("This application is not managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("getCandidateProfileForApplication throws UnauthorizedException when another employee manages the job application")
    void getCandidateProfileForApplicationThrowsUnauthorizedException() {
        Long employeeId = 1L;
        Long otherEmployeeId = 2L;
        Long applicationId = 100L;

        Employee otherEmployee = new Employee(
                "Eva", "Katic", Sex.FEMALE, "381654440012", "Ulica 44", "eva4@yahoo.com",
                "encodedPassword", "0309999001123", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(otherEmployee, "id", otherEmployeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        Employee employee = new Employee(
                "Jane", "Smith", Sex.FEMALE, "987654321", "456 Side St", "jane@example.com",
                "encodedPassword", "NID123456", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.getCandidateProfileForApplication(employeeId, applicationId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume returns resume when application is managed by employee")
    void loadResumeReturnsResource() {
        Long employeeId = 1L;
        Long applicationId = 100L;
        Long candidateId = 50L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        Candidate candidate = new Candidate(
                "Nikola", "Rokvic", Sex.MALE, "381660010070", "Bulevar 23", "rokvicn@gmail.com", "encodedPassword"
        );
        ReflectionTestUtils.setField(candidate, "id", candidateId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, candidate
        );

        Resource resource = new ByteArrayResource("PDF content".getBytes());

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(candidateService.loadResume(candidateId)).thenReturn(resource);

        Resource result = jobApplicationService.loadResume(employeeId, applicationId);

        assertEquals(resource, result);

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verify(candidateService).loadResume(candidateId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository, candidateService);
        verifyNoInteractions(jobPostingRepository, candidateRepository, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ResourceNotFoundException when employee does not exist")
    void loadResumeThrowsResourceNotFoundExceptionForMissingEmployee() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.loadResume(employeeId, applicationId)
        );
        assertEquals("Employee not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
        verifyNoInteractions(jobApplicationRepository, jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ResourceNotFoundException when job application does not exist")
    void loadResumeThrowsResourceNotFoundExceptionForMissingApplication() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.loadResume(employeeId, applicationId)
        );
        assertEquals("Application not found", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ConflictException when application is not managed")
    void loadResumeThrowsConflictException() {
        Long employeeId = 1L;
        Long applicationId = 100L;

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> jobApplicationService.loadResume(employeeId, applicationId)
        );
        assertEquals("This application is not managed", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws UnauthorizedException when another employee manages the job application")
    void loadResumeThrowsUnauthorizedException() {
        Long employeeId = 1L;
        Long otherEmployeeId = 2L;
        Long applicationId = 100L;

        Employee otherEmployee = new Employee(
                "Eva", "Katic", Sex.FEMALE, "381654440012", "Ulica 44", "eva4@yahoo.com",
                "encodedPassword", "0309999001123", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(otherEmployee, "id", otherEmployeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        Employee employee = new Employee(
                "Ana", "Anicic", Sex.FEMALE, "381621233444", "Ulica 5", "ana7@gmail.com",
                "encodedPassword", "0101999110333", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> jobApplicationService.loadResume(employeeId, applicationId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(employeeRepository).findById(employeeId);
        verify(jobApplicationRepository).findById(applicationId);
        verifyNoMoreInteractions(employeeRepository, jobApplicationRepository);
        verifyNoInteractions(jobPostingRepository, candidateRepository, candidateService, modelMapper);
    }
}
