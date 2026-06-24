package com.vuk.spring_webapp.service.interview;

import com.vuk.spring_webapp.transfer.dto.InterviewDto;
import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;

import java.util.List;

public interface InterviewService {

    List<InterviewDto> findAllForEmployee(Long employeeId, Long jobApplicationId);

    List<InterviewDto> findAllForCandidate(Long candidateId, Long jobApplicationId);

    void createInterview(Long employeeId, CreateInterviewRequest request);

    void deleteInterview(Long employeeId, Long interviewId);
}
