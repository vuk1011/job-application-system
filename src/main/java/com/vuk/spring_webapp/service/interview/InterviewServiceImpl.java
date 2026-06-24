package com.vuk.spring_webapp.service.interview;

import com.vuk.spring_webapp.domain.interview.Interview;
import com.vuk.spring_webapp.domain.job_application.JobApplicationStatusUtil;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.repository.InterviewRepository;
import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.INTERVIEW_SCHEDULED;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public void createInterview(Long employeeId, CreateInterviewRequest request) {
        var application = jobApplicationRepository.findById(request.getJobApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This job application is not managed");
        }
        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }
        if (!JobApplicationStatusUtil.isStatusChangeAllowed(
                application.getStatus(),
                INTERVIEW_SCHEDULED
        )) {
            throw new ConflictException("Interview cannot be scheduled from current status");
        }
        if (request.getTimeScheduled().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Interview cannot be scheduled for the past");
        }

        application.setStatus(INTERVIEW_SCHEDULED);
        jobApplicationRepository.save(application);

        Interview interview = new Interview(
                request.getTitle(),
                request.getDescription(),
                request.getTimeScheduled(),
                application
        );
        interviewRepository.save(interview);
    }
}
