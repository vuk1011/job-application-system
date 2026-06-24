package com.vuk.spring_webapp.controller.employee;

import com.vuk.spring_webapp.service.interview.InterviewService;
import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/employees/interviews")
public class EmployeeInterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<ApiResponse> createInterview(@RequestBody CreateInterviewRequest request) {
        return ResponseEntity.ok(new ApiResponse("ok", null));
    }
}
