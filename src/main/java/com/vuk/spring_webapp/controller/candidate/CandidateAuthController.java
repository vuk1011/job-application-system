package com.vuk.spring_webapp.controller.candidate;

import com.vuk.spring_webapp.domain.user.Role;
import com.vuk.spring_webapp.exception.EmailInUseException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.service.app_user.AppUserService;
import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.transfer.request.LoginRequest;
import com.vuk.spring_webapp.transfer.request.RegisterCandidateRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/candidates/auth")
public class CandidateAuthController {

    private final AppUserService userService;
    private final CandidateService candidateService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateCandidate(@RequestBody LoginRequest request) {
        try {
            var response = userService.authenticateUser(request.getEmail(), request.getPassword(), Role.CANDIDATE);
            return ResponseEntity.ok(new ApiResponse("Login successful", response));
        } catch (UnauthorizedException | AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerCandidate(@RequestBody RegisterCandidateRequest request) {
        try {
            candidateService.register(request);
            return ResponseEntity.ok(new ApiResponse("Registration successful", null));
        } catch (EmailInUseException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
