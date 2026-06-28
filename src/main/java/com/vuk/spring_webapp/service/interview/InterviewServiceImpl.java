package com.vuk.spring_webapp.service.interview;

import com.vuk.spring_webapp.domain.interview.Interview;
import com.vuk.spring_webapp.domain.job_application.JobApplicationStatusUtil;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.repository.InterviewRepository;
import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.transfer.dto.InterviewDto;
import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.INTERVIEW_SCHEDULED;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<InterviewDto> findAllForEmployee(Long employeeId, Long jobApplicationId) {
        var application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This job application is not managed");
        }
        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }

        var interviews = interviewRepository.findByJobApplicationId(jobApplicationId).stream()
                .map(interview -> modelMapper.map(interview, InterviewDto.class))
                .toList();

        return interviews;
    }

    @Override
    public List<InterviewDto> findAllForCandidate(Long candidateId, Long jobApplicationId) {
        var application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (!application.getCandidate().getId().equals(candidateId)) {
            throw new UnauthorizedException("You're unauthorized for this job application");
        }

        var interviews = interviewRepository.findByJobApplicationId(jobApplicationId).stream()
                .map(interview -> modelMapper.map(interview, InterviewDto.class))
                .toList();

        return interviews;
    }

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

    @Override
    public void deleteInterview(Long employeeId, Long interviewId) {
        var interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!interview.getJobApplication().getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing the associated job application for the interview");
        }
        if (interview.getTimeScheduled().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Interview cannot be deleted after it took place");
        }

        interviewRepository.delete(interview);
    }
}
