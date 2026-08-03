package com.vuk.spring_webapp.service.interview;

import com.vuk.spring_webapp.domain.interview.Interview;
import com.vuk.spring_webapp.domain.job_application.JobApplication;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.domain.user.Sex;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.repository.InterviewRepository;
import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.transfer.dto.InterviewDto;
import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterviewServiceImpl Unit Tests")
class InterviewServiceImplTest {

    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private JobApplicationRepository jobApplicationRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    private Long employeeId;
    private Long otherEmployeeId;
    private Employee employee;
    private Employee otherEmployee;
    private Long candidateId;
    private Long otherCandidateId;
    private Candidate candidate;
    private Candidate otherCandidate;
    private Long jobApplicationId;
    private JobApplication jobApplication;
    private Long interviewId;
    private Interview interview;
    private CreateInterviewRequest request;

    @BeforeEach
    void setUp() {
        employeeId = 1L;
        otherEmployeeId = 2L;
        employee = new Employee(
                "Ana", "Antić", Sex.FEMALE, "381000000", "Ulica 1", "ana@gmail.com",
                "secret123-encoded", "1010001100001", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        employee.setId(employeeId);
        otherEmployee = new Employee(
                "Marko", "Marković", Sex.MALE, "38163000000", "Ulica 2", "markovic@yahoo.com",
                "secret321-encoded", "2003000600001", LocalDate.of(2000, 3, 20),
                LocalDate.of(2019, 6, 1), null
        );
        otherEmployee.setId(otherEmployeeId);

        candidateId = 1L;
        otherCandidateId = 2L;
        candidate = new Candidate(
                "Uroš", "Protić", Sex.MALE, "38165223344", "Bulevar 123", "proticu@gmail.com", "secret123-encoded"
        );
        candidate.setId(candidateId);
        otherCandidate = new Candidate(
                "Alisa", "Li", Sex.FEMALE, "38165010101", "Dunavska 13", "alisa@yahoo.com", "password-encoded"
        );
        otherCandidate.setId(otherCandidateId);

        jobApplicationId = 1L;
        jobApplication = new JobApplication(
                LocalDate.now(), null, null, employee, null
        );

        interviewId = 1L;
        interview = new Interview(
                "Technical Interview", "First round", LocalDateTime.now().plusDays(1), jobApplication
        );

        request = new CreateInterviewRequest();
        request.setJobApplicationId(jobApplicationId);
        request.setTitle("Technical Interview");
        request.setDescription("First round");
        request.setTimeScheduled(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("findAllForEmployee returns mapped interview list when employee manages the job application")
    void findAllForEmployeeReturnsInterviews() {
        jobApplication.setStatus(INTERVIEW_SCHEDULED);

        InterviewDto interviewDto = new InterviewDto();
        interviewDto.setId(interview.getId());
        interviewDto.setTitle(interview.getTitle());
        interviewDto.setDescription(interview.getDescription());
        interviewDto.setTimeScheduled(interview.getTimeScheduled());

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));
        when(interviewRepository.findByJobApplicationId(jobApplicationId)).thenReturn(List.of(interview));
        when(modelMapper.map(interview, InterviewDto.class)).thenReturn(interviewDto);

        List<InterviewDto> result = interviewService.findAllForEmployee(employeeId, jobApplicationId);

        assertEquals(1, result.size());
        assertEquals(interviewDto, result.getFirst());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(interviewRepository).findByJobApplicationId(jobApplicationId);
        verify(modelMapper).map(interview, InterviewDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForEmployee throws ResourceNotFoundException when job application does not exist")
    void findAllForEmployeeThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> interviewService.findAllForEmployee(employeeId, jobApplicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForEmployee throws ConflictException when job application is not managed")
    void findAllForEmployeeThrowsConflictException() {
        jobApplication.setEmployee(null);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> interviewService.findAllForEmployee(employeeId, jobApplicationId)
        );
        assertEquals("This job application is not managed", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForEmployee throws UnauthorizedException when another employee manages the job application")
    void findAllForEmployeeThrowsUnauthorizedException() {
        jobApplication.setEmployee(otherEmployee);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> interviewService.findAllForEmployee(employeeId, jobApplicationId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForCandidate returns mapped interview list when candidate is tied to the job application")
    void findAllForCandidateReturnsInterviews() {
        jobApplication.setCandidate(candidate);

        InterviewDto interviewDto = new InterviewDto();
        interviewDto.setId(interview.getId());
        interviewDto.setTitle(interview.getTitle());
        interviewDto.setDescription(interview.getDescription());
        interviewDto.setTimeScheduled(interview.getTimeScheduled());

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));
        when(interviewRepository.findByJobApplicationId(jobApplicationId)).thenReturn(List.of(interview));
        when(modelMapper.map(interview, InterviewDto.class)).thenReturn(interviewDto);

        List<InterviewDto> result = interviewService.findAllForCandidate(candidateId, jobApplicationId);

        assertEquals(1, result.size());
        assertEquals(interviewDto, result.getFirst());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(interviewRepository).findByJobApplicationId(jobApplicationId);
        verify(modelMapper).map(interview, InterviewDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForCandidate throws ResourceNotFoundException when job application does not exist")
    void findAllForCandidateThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> interviewService.findAllForCandidate(candidateId, jobApplicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForCandidate throws UnauthorizedException when job application belongs to another candidate")
    void findAllForCandidateThrowsUnauthorizedException() {
        jobApplication.setCandidate(otherCandidate);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> interviewService.findAllForCandidate(candidateId, jobApplicationId)
        );
        assertEquals("You're unauthorized for this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("createInterview schedules interview and updates status when application is managed and status allows it")
    void createInterviewSchedulesInterview() {
        jobApplication.setStatus(UNDER_REVIEW);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        interviewService.createInterview(employeeId, request);

        assertEquals(INTERVIEW_SCHEDULED, jobApplication.getStatus());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository).save(jobApplication);

        ArgumentCaptor<Interview> interviewCaptor = ArgumentCaptor.forClass(Interview.class);
        verify(interviewRepository).save(interviewCaptor.capture());

        Interview savedInterview = interviewCaptor.getValue();
        assertEquals(request.getTitle(), savedInterview.getTitle());
        assertEquals(request.getDescription(), savedInterview.getDescription());
        assertEquals(request.getTimeScheduled(), savedInterview.getTimeScheduled());
        assertEquals(jobApplication, savedInterview.getJobApplication());

        verifyNoMoreInteractions(jobApplicationRepository, interviewRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("createInterview throws ResourceNotFoundException when job application does not exist")
    void createInterviewThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> interviewService.createInterview(employeeId, request)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("createInterview throws ConflictException when job application is not managed")
    void createInterviewThrowsConflictExceptionForUnmanagedApplication() {
        jobApplication.setEmployee(null);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> interviewService.createInterview(employeeId, request)
        );
        assertEquals("This job application is not managed", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("createInterview throws UnauthorizedException when another employee manages the job application")
    void createInterviewThrowsUnauthorizedException() {
        jobApplication.setStatus(UNDER_REVIEW);
        jobApplication.setEmployee(otherEmployee);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> interviewService.createInterview(employeeId, request)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("createInterview throws ConflictException when status does not allow scheduling an interview")
    void createInterviewThrowsConflictExceptionForDisallowedStatus() {
        jobApplication.setStatus(OFFERED);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> interviewService.createInterview(employeeId, request)
        );
        assertEquals("Interview cannot be scheduled from current status", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("createInterview throws ConflictException when interview is scheduled in the past")
    void createInterviewThrowsConflictExceptionForPastSchedule() {
        jobApplication.setStatus(UNDER_REVIEW);
        request.setTimeScheduled(LocalDateTime.now().minusDays(1));

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(jobApplication));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> interviewService.createInterview(employeeId, request)
        );
        assertEquals("Interview cannot be scheduled for the past", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteInterview deletes interview when employee manages it and it has not passed yet in time")
    void deleteInterviewDeletesInterview() {
        jobApplication.setStatus(INTERVIEW_SCHEDULED);

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));

        interviewService.deleteInterview(employeeId, interviewId);

        verify(interviewRepository).findById(interviewId);
        verify(interviewRepository).delete(interview);
        verifyNoMoreInteractions(interviewRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteInterview throws ResourceNotFoundException when interview does not exist")
    void deleteInterviewThrowsResourceNotFoundException() {
        when(interviewRepository.findById(interviewId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> interviewService.deleteInterview(employeeId, interviewId)
        );
        assertEquals("Interview not found", exception.getMessage());

        verify(interviewRepository).findById(interviewId);
        verify(interviewRepository, never()).delete(any());
        verifyNoMoreInteractions(interviewRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteInterview throws UnauthorizedException when another employee manages the associated job application")
    void deleteInterviewThrowsUnauthorizedException() {
        jobApplication.setStatus(INTERVIEW_SCHEDULED);
        jobApplication.setEmployee(otherEmployee);

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> interviewService.deleteInterview(employeeId, interviewId)
        );
        assertEquals("Another employee is managing the associated job application for the interview", exception.getMessage());

        verify(interviewRepository).findById(interviewId);
        verify(interviewRepository, never()).delete(any());
        verifyNoMoreInteractions(interviewRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteInterview throws ConflictException when interview's scheduled time has passed")
    void deleteInterviewThrowsConflictException() {
        jobApplication.setStatus(INTERVIEW_SCHEDULED);
        interview.setTimeScheduled(LocalDateTime.now().minusDays(1));

        when(interviewRepository.findById(interviewId)).thenReturn(Optional.of(interview));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> interviewService.deleteInterview(employeeId, interviewId)
        );
        assertEquals("Interview cannot be deleted after it took place", exception.getMessage());

        verify(interviewRepository).findById(interviewId);
        verify(interviewRepository, never()).delete(any());
        verifyNoMoreInteractions(interviewRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }
}