package com.vuk.spring_webapp.controller.candidate;

import com.vuk.spring_webapp.exception.ApplicationExistsException;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.JobPostingClosedException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.service.job_application.JobApplicationService;
import com.vuk.spring_webapp.transfer.request.SubmitJobApplicationRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/candidates/job-applications")
public class CandidateJobApplicationController {

    private final CandidateService candidateService;
    private final JobApplicationService jobApplicationService;

    private Long getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return candidateService.getIdByEmail(email);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> submitApplication(@RequestBody SubmitJobApplicationRequest request) {
        try {
            jobApplicationService.submitJobApplication(getId(), request);
            return ResponseEntity.ok(new ApiResponse("Successfully submitted an application", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (ApplicationExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (JobPostingClosedException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllApplications() {
        try {
            var response = jobApplicationService.getAllByCandidateId(getId());
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved all applications", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponse> getApplication(@PathVariable Long applicationId) {
        try {
            var response = jobApplicationService.getByIdForCandidate(getId(), applicationId);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved the application", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<ApiResponse> withdrawApplication(@PathVariable Long applicationId) {
        try {
            jobApplicationService.deleteById(getId(), applicationId);
            return ResponseEntity.ok(new ApiResponse("Successfully withdrawn an application", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (ConflictException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
