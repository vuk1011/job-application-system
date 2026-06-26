package com.vuk.spring_webapp.service.offer;

import com.vuk.spring_webapp.transfer.dto.OfferDto;
import com.vuk.spring_webapp.transfer.request.CreateOfferRequest;
import com.vuk.spring_webapp.transfer.request.UpdateOfferRequest;

import java.util.List;

public interface OfferService {

    List<OfferDto> findAllForEmployee(Long employeeId, Long jobApplicationId);

    List<OfferDto> findAllForCandidate(Long employeeId, Long jobApplicationId);

    void createOffer(Long employeeId, CreateOfferRequest request);

    void deleteOffer(Long employeeId, Long offerId);

    void updateOffer(Long candidateId, UpdateOfferRequest request);
}
