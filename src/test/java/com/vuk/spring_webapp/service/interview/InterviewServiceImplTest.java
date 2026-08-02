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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    @DisplayName("findAllForEmployee returns mapped interview list when employee manages the job application")
    void findAllForEmployeeReturnsInterviews() {
        Long employeeId = 1L;
        Long jobApplicationId = 10L;

        Employee employee = new Employee(
                "Ana", "Antic", Sex.FEMALE, "381000000", "Ulica 1", "ana@gmail.com",
                "encodedPassword", "1010001100001", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, employee, null
        );

        Interview interview = new Interview(
                "Technical Interview", "First round", LocalDateTime.of(2026, 8, 10, 10, 0), application
        );
        InterviewDto interviewDto = new InterviewDto();
        interviewDto.setId(1L);
        interviewDto.setTitle("Technical Interview");
        interviewDto.setDescription("First round");
        interviewDto.setTimeScheduled(LocalDateTime.of(2026, 8, 10, 10, 0));

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
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
        Long employeeId = 1L;
        Long jobApplicationId = 10L;

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
        Long employeeId = 1L;
        Long jobApplicationId = 10L;

        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

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
        Long employeeId = 1L;
        Long otherEmployeeId = 2L;
        Long jobApplicationId = 10L;

        Employee otherEmployee = new Employee(
                "Marko", "Markovic", Sex.MALE, "38163000000", "Ulica 2", "markovic@yahoo.com",
                "encodedPassword", "2003000600001", LocalDate.of(2000, 3, 20),
                LocalDate.of(2019, 6, 1), null
        );
        ReflectionTestUtils.setField(otherEmployee, "id", otherEmployeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, otherEmployee, null
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

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
        Long candidateId = 1L;
        Long jobApplicationId = 10L;

        Candidate candidate = new Candidate(
                "Uros", "Protic", Sex.MALE, "38165223344", "Bulevar 123", "proticu@gmail.com", "encodedPassword"
        );
        ReflectionTestUtils.setField(candidate, "id", candidateId);

        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, null, candidate
        );

        Interview interview = new Interview(
                "Technical Interview", "First round", LocalDateTime.of(2026, 8, 10, 10, 0), application
        );
        InterviewDto interviewDto = new InterviewDto();
        interviewDto.setId(1L);
        interviewDto.setTitle("Technical Interview");
        interviewDto.setDescription("First round");
        interviewDto.setTimeScheduled(LocalDateTime.of(2026, 8, 10, 10, 0));

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
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
        Long candidateId = 1L;
        Long jobApplicationId = 10L;

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
        Long candidateId = 1L;
        Long otherCandidateId = 2L;
        Long jobApplicationId = 10L;

        Candidate otherCandidate = new Candidate(
                "Alisa", "Li", Sex.FEMALE, "38165010101", "Dunavska 13", "alisa@yahoo.com", "encodedPassword"
        );
        ReflectionTestUtils.setField(otherCandidate, "id", otherCandidateId);

        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, null, otherCandidate
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

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
        Long employeeId = 1L;
        Long jobApplicationId = 10L;

        Employee employee = new Employee(
                "Olivera", "Stanic", Sex.FEMALE, "38166001122", "Savska 1", "olivera@gmail.com",
                "encodedPassword", "1010980100000", LocalDate.of(1990, 10, 10),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        CreateInterviewRequest request = new CreateInterviewRequest();
        request.setJobApplicationId(jobApplicationId);
        request.setTitle("Technical Interview");
        request.setDescription("First round");
        request.setTimeScheduled(LocalDateTime.now().plusDays(3));

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        interviewService.createInterview(employeeId, request);

        assertEquals(INTERVIEW_SCHEDULED, application.getStatus());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository).save(application);

        ArgumentCaptor<Interview> interviewCaptor = ArgumentCaptor.forClass(Interview.class);
        verify(interviewRepository).save(interviewCaptor.capture());

        Interview savedInterview = interviewCaptor.getValue();
        assertEquals("Technical Interview", savedInterview.getTitle());
        assertEquals("First round", savedInterview.getDescription());
        assertEquals(request.getTimeScheduled(), savedInterview.getTimeScheduled());
        assertEquals(application, savedInterview.getJobApplication());

        verifyNoMoreInteractions(jobApplicationRepository, interviewRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("createInterview throws ResourceNotFoundException when job application does not exist")
    void createInterviewThrowsResourceNotFoundException() {
        Long employeeId = 1L;
        CreateInterviewRequest request = new CreateInterviewRequest();
        request.setJobApplicationId(10L);

        when(jobApplicationRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> interviewService.createInterview(employeeId, request)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(10L);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(interviewRepository, modelMapper);
    }

    @Test
    @DisplayName("createInterview throws ConflictException when job application is not managed")
    void createInterviewThrowsConflictExceptionForUnmanagedApplication() {
        Long employeeId = 1L;
        Long jobApplicationId = 10L;

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, null, null
        );

        CreateInterviewRequest request = new CreateInterviewRequest();
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

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
        Long employeeId = 1L;
        Long otherEmployeeId = 2L;
        Long jobApplicationId = 10L;

        Employee otherEmployee = new Employee(
                "Vuk", "Perovic", Sex.MALE, "38162000333", "Ustanicka 22", "vukperovic@gmail.com",
                "encodedPassword", "2010002700003", LocalDate.of(2002, 10, 20),
                LocalDate.of(2019, 6, 1), null
        );
        ReflectionTestUtils.setField(otherEmployee, "id", otherEmployeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, otherEmployee, null
        );

        CreateInterviewRequest request = new CreateInterviewRequest();
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

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
        Long employeeId = 1L;
        Long jobApplicationId = 10L;

        Employee employee = new Employee(
                "Andjela", "Simic", Sex.FEMALE, "38163777888", "Kneza Milosa 10", "asimic@gmail.com",
                "encodedPassword", "1505990200002", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, employee, null
        );

        CreateInterviewRequest request = new CreateInterviewRequest();
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

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
        Long employeeId = 1L;
        Long jobApplicationId = 10L;

        Employee employee = new Employee(
                "Milan", "Stojanovic", Sex.MALE, "38162123123", "Bulevar 123", "mstojanovic@gmail.com",
                "encodedPassword", "1009999600200", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        CreateInterviewRequest request = new CreateInterviewRequest();
        request.setJobApplicationId(jobApplicationId);
        request.setTimeScheduled(LocalDateTime.now().minusDays(1));

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

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
        Long employeeId = 1L;
        Long interviewId = 5L;

        Employee employee = new Employee(
                "Marija", "Markovic", Sex.FEMALE, "38162321123", "Ulica 10", "maja@gmail.com",
                "encodedPassword", "1010970", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, employee, null
        );
        Interview interview = new Interview(
                "Technical Interview", "First round", LocalDateTime.now().plusDays(1), application
        );

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
        Long employeeId = 1L;
        Long interviewId = 5L;

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
        Long employeeId = 1L;
        Long otherEmployeeId = 2L;
        Long interviewId = 5L;

        Employee otherEmployee = new Employee(
                "Nikola", "Nikolic", Sex.MALE, "381640001111", "Trg 23", "nikola@gmail.com",
                "encodedPassword", "1010001800890", LocalDate.of(1985, 3, 20),
                LocalDate.of(2019, 6, 1), null
        );
        ReflectionTestUtils.setField(otherEmployee, "id", otherEmployeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, otherEmployee, null
        );
        Interview interview = new Interview(
                "Technical Interview", "First round", LocalDateTime.now().plusDays(1), application
        );

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
        Long employeeId = 1L;
        Long interviewId = 5L;

        Employee employee = new Employee(
                "Milos", "Peric", Sex.MALE, "38165444123", "Sarajevska 10", "milos@gmail.com",
                "encodedPassword", "2002999780081", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        ReflectionTestUtils.setField(employee, "id", employeeId);

        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, employee, null
        );
        Interview interview = new Interview(
                "Technical Interview", "First round", LocalDateTime.now().minusDays(1), application
        );

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