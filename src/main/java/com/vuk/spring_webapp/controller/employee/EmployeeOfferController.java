package com.vuk.spring_webapp.controller.employee;

import com.vuk.spring_webapp.service.employee.EmployeeService;
import com.vuk.spring_webapp.service.offer.OfferService;
import com.vuk.spring_webapp.transfer.request.CreateOfferRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/employees/offers")
public class EmployeeOfferController {

    private final OfferService offerService;
    private final EmployeeService employeeService;

    private Long getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return employeeService.getIdByEmail(email);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getOffers(@RequestParam Long jobApplicationId) {
        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createOffer(@RequestBody CreateOfferRequest request) {
        return null;
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<ApiResponse> deleteOffer(@PathVariable Long offerId) {
        return null;
    }
}
