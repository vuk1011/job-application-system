package com.vuk.spring_webapp.controller.employee;

import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.service.employee.EmployeeService;
import com.vuk.spring_webapp.service.interview.InterviewService;
import com.vuk.spring_webapp.service.job_application.JobApplicationService;
import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/employees/interviews")
public class EmployeeInterviewController {

    private final InterviewService interviewService;
    private final JobApplicationService jobApplicationService;
    private final EmployeeService employeeService;

    private Long getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return employeeService.getIdByEmail(email);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createInterview(@RequestBody CreateInterviewRequest request) {
        try {
            interviewService.createInterview(getId(), request);
            return ResponseEntity.ok(new ApiResponse("Successfully created interview", null));
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
