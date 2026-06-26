package com.vuk.spring_webapp.controller.candidate;

import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.service.offer.OfferService;
import com.vuk.spring_webapp.transfer.request.UpdateOfferRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

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
        try {
            var response = offerService.findAllForCandidate(getId(), jobApplicationId);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved offers for job application", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{offerId}")
    public ResponseEntity<ApiResponse> updateOffer(@PathVariable Long offerId, @RequestBody UpdateOfferRequest request) {
        try {
            offerService.updateOffer(getId(), offerId, request);
            return ResponseEntity.ok(new ApiResponse("Successfully updated offer", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        } catch (ConflictException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
