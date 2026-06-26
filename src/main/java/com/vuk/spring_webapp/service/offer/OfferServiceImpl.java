package com.vuk.spring_webapp.service.offer;

import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.repository.OfferRepository;
import com.vuk.spring_webapp.transfer.dto.OfferDto;
import com.vuk.spring_webapp.transfer.request.CreateOfferRequest;
import com.vuk.spring_webapp.transfer.request.UpdateOfferRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<OfferDto> findAllForEmployee(Long employeeId, Long jobApplicationId) {
        return List.of();
    }

    @Override
    public List<OfferDto> findAllForCandidate(Long employeeId, Long jobApplicationId) {
        return List.of();
    }

    @Override
    public void createOffer(Long employeeId, CreateOfferRequest request) {

    }

    @Override
    public void deleteOffer(Long employeeId, Long offerId) {

    }

    @Override
    public void updateOffer(Long candidateId, Long offerId, UpdateOfferRequest request) {

    }
}
