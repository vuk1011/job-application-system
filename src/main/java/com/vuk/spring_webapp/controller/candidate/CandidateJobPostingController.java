package com.vuk.spring_webapp.controller.candidate;

import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import com.vuk.spring_webapp.service.job_posting.JobPostingService;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/candidates/job-postings")
public class CandidateJobPostingController {

    private final JobPostingService jobPostingService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPublished() {
        try {
            var response = jobPostingService.findAllPublished();
            return ResponseEntity.ok(new ApiResponse("Job postings", response));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
