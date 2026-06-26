package com.vuk.spring_webapp.controller.candidate;

import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.service.offer.OfferService;
import com.vuk.spring_webapp.transfer.request.UpdateOfferRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/candidates/offers")
public class CandidateOfferController {

    private final OfferService offerService;
    private final CandidateService candidateService;

    private Long getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return candidateService.getIdByEmail(email);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getOffers(@RequestParam Long jobApplicationId) {
        return null;
    }

    @PutMapping("/{offerId}")
    public ResponseEntity<ApiResponse> updateOffer(@PathVariable Long offerId, @RequestBody UpdateOfferRequest request) {
        return null;
    }
}
