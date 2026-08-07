package com.vuk.spring_webapp.service.offer;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import com.vuk.spring_webapp.domain.offer.Offer;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.domain.user.Sex;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.repository.OfferRepository;
import com.vuk.spring_webapp.transfer.dto.OfferDto;
import com.vuk.spring_webapp.transfer.request.CreateOfferRequest;
import com.vuk.spring_webapp.transfer.request.UpdateOfferRequest;
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
import java.util.List;
import java.util.Optional;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferServiceImpl Unit Tests")
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private JobApplicationRepository jobApplicationRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Long employeeId;
    private Long otherEmployeeId;
    private Employee employee;
    private Employee otherEmployee;
    private Long jobApplicationId;
    private Long candidateId;
    private Long otherCandidateId;
    private Candidate candidate;
    private Candidate otherCandidate;
    private Long offerId;

    @BeforeEach
    void setUp() {
        employeeId = 1L;
        otherEmployeeId = 2L;

        employee = new Employee(
                "Jana", "Simić", Sex.FEMALE, "38162001122", "Sarajevska 10", "jana@company.com",
                "encodedPassword", "NID123456", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        employee.setId(employeeId);
        otherEmployee = new Employee(
                "Maja", "Simić", Sex.FEMALE, "38162001123", "Sarajevska 11", "maja@company.com",
                "encodedPassword", "NID123400", LocalDate.of(1990, 5, 15),
                LocalDate.of(2020, 1, 10), null
        );
        otherEmployee.setId(otherEmployeeId);

        jobApplicationId = 1L;

        candidateId = 1L;
        otherCandidateId = 2L;

        candidate = new Candidate(
                "Jovan", "Šarić", Sex.MALE, "38164334001", "Savska 13", "jovan11@gmail.com", "encodedPassword"
        );
        candidate.setId(candidateId);
        otherCandidate = new Candidate(
                "Jovana", "Šarić", Sex.FEMALE, "38164334002", "Savska 13", "jovana11@gmail.com", "encodedPassword"
        );
        otherCandidate.setId(otherCandidateId);

        offerId = 1L;
    }

    @Test
    @DisplayName("findAllForEmployee returns mapped offer list when employee manages the job application")
    void findAllForEmployeeReturnsOffers() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, employee, null
        );

        Offer offer = new Offer("Senior Developer Offer", application);
        OfferDto dto = new OfferDto();
        dto.setId(1L);
        dto.setName("Senior Developer Offer");
        dto.setAccepted(null);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
        when(offerRepository.findByJobApplicationId(jobApplicationId)).thenReturn(List.of(offer));
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(dto);

        List<OfferDto> result = offerService.findAllForEmployee(employeeId, jobApplicationId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(offerRepository).findByJobApplicationId(jobApplicationId);
        verify(modelMapper).map(offer, OfferDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, offerRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForEmployee throws ResourceNotFoundException when job application does not exist")
    void findAllForEmployeeThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> offerService.findAllForEmployee(employeeId, jobApplicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForEmployee throws ConflictException when job application is not managed")
    void findAllForEmployeeThrowsConflictException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.findAllForEmployee(employeeId, jobApplicationId)
        );
        assertEquals("This job application is not managed", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForEmployee throws UnauthorizedException when another employee manages the job application")
    void findAllForEmployeeThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, otherEmployee, null
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> offerService.findAllForEmployee(employeeId, jobApplicationId)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForCandidate returns mapped offer list when candidate owns the job application")
    void findAllForCandidateReturnsOffers() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, null, candidate
        );

        Offer offer = new Offer("Senior Developer Offer", application);
        OfferDto dto = new OfferDto();
        dto.setId(1L);
        dto.setName("Senior Developer Offer");
        dto.setAccepted(null);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));
        when(offerRepository.findByJobApplicationId(jobApplicationId)).thenReturn(List.of(offer));
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(dto);

        List<OfferDto> result = offerService.findAllForCandidate(candidateId, jobApplicationId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(offerRepository).findByJobApplicationId(jobApplicationId);
        verify(modelMapper).map(offer, OfferDto.class);
        verifyNoMoreInteractions(jobApplicationRepository, offerRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForCandidate throws ResourceNotFoundException when job application does not exist")
    void findAllForCandidateThrowsResourceNotFoundException() {
        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> offerService.findAllForCandidate(candidateId, jobApplicationId)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("findAllForCandidate throws UnauthorizedException when job application belongs to another candidate")
    void findAllForCandidateThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, null, otherCandidate
        );

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> offerService.findAllForCandidate(candidateId, jobApplicationId)
        );
        assertEquals("You're unauthorized for this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("createOffer creates offer and updates status when transition is allowed")
    void createOfferCreatesOfferAndUpdatesStatus() {
        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, employee, null
        );

        CreateOfferRequest request = new CreateOfferRequest();
        request.setName("Senior Developer Offer");
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        offerService.createOffer(employeeId, request);

        assertEquals(OFFERED, application.getStatus());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verify(jobApplicationRepository).save(application);

        ArgumentCaptor<Offer> captor = ArgumentCaptor.forClass(Offer.class);
        verify(offerRepository).save(captor.capture());

        Offer saved = captor.getValue();
        assertEquals("Senior Developer Offer", saved.getName());
        assertEquals(application, saved.getJobApplication());

        verifyNoMoreInteractions(jobApplicationRepository, offerRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("createOffer throws ResourceNotFoundException when job application does not exist")
    void createOfferThrowsResourceNotFoundException() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> offerService.createOffer(employeeId, request)
        );
        assertEquals("Job application not found", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("createOffer throws ConflictException when job application is not managed")
    void createOfferThrowsConflictExceptionForUnmanagedApplication() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, null
        );

        CreateOfferRequest request = new CreateOfferRequest();
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.createOffer(employeeId, request)
        );
        assertEquals("This job application is not managed", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("createOffer throws UnauthorizedException when another employee manages the job application")
    void createOfferThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, otherEmployee, null
        );

        CreateOfferRequest request = new CreateOfferRequest();
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> offerService.createOffer(employeeId, request)
        );
        assertEquals("Another employee is managing this job application", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("createOffer throws ConflictException when status change to OFFERED is not allowed")
    void createOfferThrowsConflictExceptionForInvalidStatusChange() {
        JobApplication application = new JobApplication(
                LocalDate.now(), UNDER_REVIEW, null, employee, null
        );

        CreateOfferRequest request = new CreateOfferRequest();
        request.setJobApplicationId(jobApplicationId);

        when(jobApplicationRepository.findById(jobApplicationId)).thenReturn(Optional.of(application));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.createOffer(employeeId, request)
        );
        assertEquals("Offer cannot be created in current status", exception.getMessage());

        verify(jobApplicationRepository).findById(jobApplicationId);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoInteractions(offerRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteOffer deletes offer when employee manages it and it is not yet accepted or rejected")
    void deleteOfferDeletesOffer() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, employee, null
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        offerService.deleteOffer(employeeId, offerId);

        verify(offerRepository).findById(offerId);
        verify(offerRepository).delete(offer);
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteOffer throws ResourceNotFoundException when offer does not exist")
    void deleteOfferThrowsResourceNotFoundException() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> offerService.deleteOffer(employeeId, offerId)
        );
        assertEquals("Offer not found", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verify(offerRepository, never()).delete(any());
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteOffer throws UnauthorizedException when another employee manages the associated job application")
    void deleteOfferThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, otherEmployee, null
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> offerService.deleteOffer(employeeId, offerId)
        );
        assertEquals("Another employee is managing the associated job application for the offer", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verify(offerRepository, never()).delete(any());
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("deleteOffer throws ConflictException when offer has already been accepted or rejected")
    void deleteOfferThrowsConflictException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, employee, null
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(true);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.deleteOffer(employeeId, offerId)
        );
        assertEquals("Offer cannot be deleted after it got accepted or rejected", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verify(offerRepository, never()).delete(any());
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("updateOffer accepts offer and updates application status to ACCEPTED when allowed")
    void updateOfferAcceptsOfferAndUpdatesStatus() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, null, candidate
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(true);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        offerService.updateOffer(candidateId, offerId, request);

        assertEquals(true, offer.getAccepted());
        assertEquals(ACCEPTED, application.getStatus());

        verify(offerRepository).findById(offerId);
        verify(offerRepository).save(offer);
        verify(jobApplicationRepository).save(application);
        verifyNoMoreInteractions(offerRepository, jobApplicationRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("updateOffer rejects offer and updates application status to REJECTED when allowed")
    void updateOfferRejectsOfferAndUpdatesStatus() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, null, candidate
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(false);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        offerService.updateOffer(candidateId, offerId, request);

        assertEquals(false, offer.getAccepted());
        assertEquals(REJECTED, application.getStatus());

        verify(offerRepository).findById(offerId);
        verify(offerRepository).save(offer);
        verify(jobApplicationRepository).save(application);
        verifyNoMoreInteractions(offerRepository, jobApplicationRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("updateOffer throws ResourceNotFoundException when offer does not exist")
    void updateOfferThrowsResourceNotFoundException() {
        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(true);

        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> offerService.updateOffer(candidateId, offerId, request)
        );
        assertEquals("Offer not found", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("updateOffer throws UnauthorizedException when offer belongs to another candidate")
    void updateOfferThrowsUnauthorizedException() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, null, otherCandidate
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(true);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> offerService.updateOffer(candidateId, offerId, request)
        );
        assertEquals("You're unauthorized for this job application", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("updateOffer throws ConflictException when application status is final")
    void updateOfferThrowsConflictExceptionForFinalApplicationStatus() {
        JobApplication application = new JobApplication(
                LocalDate.now(), ACCEPTED, null, null, candidate
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(true);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.updateOffer(candidateId, offerId, request)
        );
        assertEquals("Offer cannot be updated in application's final status", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("updateOffer throws ConflictException when offer has already been accepted or rejected")
    void updateOfferThrowsConflictExceptionForAlreadyDecidedOffer() {
        JobApplication application = new JobApplication(
                LocalDate.now(), OFFERED, null, null, candidate
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(true);

        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(false);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.updateOffer(candidateId, offerId, request)
        );
        assertEquals("Offer cannot be updated after it got accepted or rejected", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("updateOffer throws ConflictException when status change to ACCEPTED is not allowed")
    void updateOfferThrowsConflictExceptionForInvalidAcceptTransition() {
        JobApplication application = new JobApplication(
                LocalDate.now(), INTERVIEW_SCHEDULED, null, null, candidate
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(true);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.updateOffer(candidateId, offerId, request)
        );
        assertEquals("Offer cannot be updated when job application is in current status", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }

    @Test
    @DisplayName("updateOffer throws ConflictException when status change to REJECTED is not allowed")
    void updateOfferThrowsConflictExceptionForInvalidRejectTransition() {
        JobApplication application = new JobApplication(
                LocalDate.now(), SUBMITTED, null, null, candidate
        );
        Offer offer = new Offer("Senior Developer Offer", application);
        offer.setAccepted(null);

        UpdateOfferRequest request = new UpdateOfferRequest();
        request.setAccepted(false);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offer));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> offerService.updateOffer(candidateId, offerId, request)
        );
        assertEquals("Offer cannot be updated when job application is in current status", exception.getMessage());

        verify(offerRepository).findById(offerId);
        verifyNoMoreInteractions(offerRepository);
        verifyNoInteractions(jobApplicationRepository, modelMapper);
    }
}
