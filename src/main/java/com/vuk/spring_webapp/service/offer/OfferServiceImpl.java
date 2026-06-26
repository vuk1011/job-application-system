package com.vuk.spring_webapp.service.offer;

import com.vuk.spring_webapp.domain.job_application.JobApplicationStatusUtil;
import com.vuk.spring_webapp.domain.offer.Offer;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.repository.OfferRepository;
import com.vuk.spring_webapp.transfer.dto.OfferDto;
import com.vuk.spring_webapp.transfer.request.CreateOfferRequest;
import com.vuk.spring_webapp.transfer.request.UpdateOfferRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<OfferDto> findAllForEmployee(Long employeeId, Long jobApplicationId) {
        var application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This job application is not managed");
        }
        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }

        var offers = offerRepository.findByJobApplicationId(jobApplicationId).stream()
                .map(offer -> modelMapper.map(offer, OfferDto.class))
                .toList();

        return offers;
    }

    @Override
    public List<OfferDto> findAllForCandidate(Long candidateId, Long jobApplicationId) {
        var application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (!application.getCandidate().getId().equals(candidateId)) {
            throw new UnauthorizedException("You're unauthorized for this job application");
        }

        var offers = offerRepository.findByJobApplicationId(jobApplicationId).stream()
                .map(offer -> modelMapper.map(offer, OfferDto.class))
                .toList();

        return offers;
    }

    @Override
    public void createOffer(Long employeeId, CreateOfferRequest request) {
        var application = jobApplicationRepository.findById(request.getJobApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This job application is not managed");
        }
        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }
        if (!JobApplicationStatusUtil.isStatusChangeAllowed(
                application.getStatus(),
                OFFERED
        )) {
            throw new ConflictException("Offer cannot be created in current status");
        }

        application.setStatus(OFFERED);
        jobApplicationRepository.save(application);

        Offer offer = new Offer(
                request.getName(),
                application
        );
        offerRepository.save(offer);
    }

    @Override
    public void deleteOffer(Long employeeId, Long offerId) {
        var offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        if (!offer.getJobApplication().getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing the associated job application for the offer");
        }
        if (offer.getAccepted() != null) {
            throw new ConflictException("Offer cannot be deleted after it got accepted or rejected");
        }

        offerRepository.delete(offer);
    }

    @Override
    public void updateOffer(Long candidateId, Long offerId, UpdateOfferRequest request) {
        var offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
        var application = offer.getJobApplication();

        if (!application.getCandidate().getId().equals(candidateId)) {
            throw new UnauthorizedException("You're unauthorized for this job application");
        }
        if (application.statusIsFinal()) {
            throw new ConflictException("Offer cannot be updated in application's final status");
        }
        if (offer.getAccepted() != null) {
            throw new ConflictException("Offer cannot be updated after it got accepted or rejected");
        }

        if (request.isAccepted()) {
            if (!JobApplicationStatusUtil.isStatusChangeAllowed(application.getStatus(), ACCEPTED)) {
                throw new ConflictException("Offer cannot be updated when job application is in current status");
            }
            offer.setAccepted(true);
            application.setStatus(ACCEPTED);
        } else {
            if (!JobApplicationStatusUtil.isStatusChangeAllowed(application.getStatus(), REJECTED)) {
                throw new ConflictException("Offer cannot be updated when job application is in current status");
            }
            offer.setAccepted(false);
            application.setStatus(REJECTED);
        }

        offerRepository.save(offer);
        jobApplicationRepository.save(application);
    }
}
