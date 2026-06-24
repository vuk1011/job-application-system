package com.vuk.spring_webapp.controller.candidate;

import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.service.interview.InterviewService;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/candidates/interviews")
public class CandidateInterviewController {

    private final InterviewService interviewService;
    private final CandidateService candidateService;

    private Long getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return candidateService.getIdByEmail(email);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getInterviews(@RequestParam Long jobApplicationId) {
        try {
            var response = interviewService.findAllForCandidate(getId(), jobApplicationId);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved interviews for job application", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
