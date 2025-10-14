package com.vuk.spring_webapp.controller.employee;

import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.ResumeNotUploadedException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.service.employee.EmployeeService;
import com.vuk.spring_webapp.service.job_application.JobApplicationService;
import com.vuk.spring_webapp.transfer.request.ManageJobApplicationRequest;
import com.vuk.spring_webapp.transfer.request.UpdateJobApplicationStatusRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/employees/job-applications")
public class EmployeeJobApplicationController {

    private final EmployeeService employeeService;
    private final JobApplicationService jobApplicationService;

    private Long getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return employeeService.getIdByEmail(email);
    }

    @GetMapping("/job-posting/{jobPostingId}")
    public ResponseEntity<ApiResponse> getApplicationsByJobPostingId(@PathVariable Long jobPostingId) {
        try {
            var response = jobApplicationService.getUnmanagedApplicationsByJobPosting(jobPostingId);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved unmanaged job applications for job posting", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponse> getApplicationById(@PathVariable Long applicationId) {
        try {
            var response = jobApplicationService.getUnmanagedApplicationById(applicationId);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved unmanaged job application", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (ConflictException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/managed")
    public ResponseEntity<ApiResponse> getManagedApplications() {
        try {
            var response = jobApplicationService.getManagedApplicationsByEmployee(getId());
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved managed job applications", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/managed")
    public ResponseEntity<ApiResponse> addApplicationToManaged(@RequestBody ManageJobApplicationRequest request) {
        try {
            jobApplicationService.setEmployee(getId(), request.getApplicationId());
            return ResponseEntity.ok(new ApiResponse("Successfully added job application to the list of managed applications", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (ConflictException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/managed/{applicationId}")
    public ResponseEntity<ApiResponse> getManagedApplicationById(@PathVariable Long applicationId) {
        try {
            var response = jobApplicationService.getManagedApplicationByIdForEmployee(applicationId, getId());
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved managed job application", response));
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

    @PutMapping("/managed/{applicationId}")
    public ResponseEntity<ApiResponse> updateApplicationStatus(@PathVariable Long applicationId, @RequestBody UpdateJobApplicationStatusRequest request) {
        try {
            jobApplicationService.updateApplicationStatus(getId(), applicationId, request.getStatus());
            return ResponseEntity.ok(new ApiResponse("Successfully updated application's status", null));
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

    @GetMapping("/managed/{applicationId}/candidate/profile")
    public ResponseEntity<ApiResponse> getCandidateProfileForApplication(@PathVariable Long applicationId) {
        try {
            var response = jobApplicationService.getCandidateProfileForApplication(getId(), applicationId);
            return ResponseEntity.ok(new ApiResponse("Successfully retrieved candidates profile", response));
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

    @GetMapping("/managed/{applicationId}/candidate/resume")
    public ResponseEntity<Resource> getCandidateResumeForApplication(@PathVariable Long applicationId) {
        try {
            Resource resume = jobApplicationService.loadResume(getId(), applicationId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(CONTENT_DISPOSITION, "inline; filename=\"resume.pdf\"")
                    .body(resume);
        } catch (ResourceNotFoundException | ResumeNotUploadedException e) {
            return ResponseEntity.status(NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        } catch (ConflictException e) {
            return ResponseEntity.status(CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }
}
