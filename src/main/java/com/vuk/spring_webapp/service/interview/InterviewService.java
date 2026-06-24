package com.vuk.spring_webapp.service.interview;

import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;

public interface InterviewService {

    void createInterview(Long employeeId, CreateInterviewRequest request);

    void deleteInterview(Long employeeId, Long interviewId);
}
