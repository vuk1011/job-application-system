package com.vuk.spring_webapp.service.job_application;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.exception.*;
import com.vuk.spring_webapp.repository.CandidateRepository;
import com.vuk.spring_webapp.repository.EmployeeRepository;
import com.vuk.spring_webapp.repository.JobApplicationRepository;
import com.vuk.spring_webapp.repository.JobPostingRepository;
import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.transfer.dto.CandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationCandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationEmployeeDto;
import com.vuk.spring_webapp.transfer.request.SubmitJobApplicationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatus.*;
import static com.vuk.spring_webapp.domain.job_application.JobApplicationStatusUtil.isStatusChangeAllowed;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final CandidateRepository candidateRepository;
    private final EmployeeRepository employeeRepository;
    private final CandidateService candidateService;
    private final ModelMapper modelMapper;

    @Override
    public void submitJobApplication(Long candidateId, SubmitJobApplicationRequest request) {
        Long jobPostingId = request.getJobPostingId();

        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (jobApplicationRepository.existsByCandidateIdAndJobPostingId(candidateId, jobPostingId)) {
            throw new ApplicationExistsException("You already applied for this job posting");
        }

        if (jobPostingRepository.existsByIdAndStatus(jobPostingId, JobPostingStatus.CLOSED)) {
            throw new JobPostingClosedException("Job posting closed");
        }

        JobApplication jobApplication = new JobApplication(
                new Date(),
                JobApplicationStatus.SUBMITTED,
                jobPosting,
                null,
                candidate
        );
        jobApplicationRepository.save(jobApplication);
    }

    @Override
    public List<JobApplicationCandidateDto> getAllByCandidateId(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        return candidate.getJobApplications()
                .stream()
                .map(application -> modelMapper.map(application, JobApplicationCandidateDto.class))
                .toList();
    }

    @Override
    public JobApplicationCandidateDto getByIdForCandidate(Long candidateId, Long applicationId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        JobApplication jobApplication = candidate.getJobApplications()
                .stream()
                .filter(application -> application.getId().equals(applicationId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        return modelMapper.map(jobApplication, JobApplicationCandidateDto.class);
    }

    @Override
    public void deleteById(Long candidateId, Long applicationId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        JobApplication jobApplication = candidate.getJobApplications()
                .stream()
                .filter(application -> application.getId().equals(applicationId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        /*
        Application can be of status:
            SUBMITTED,
            UNDER_REVIEW,
            INTERVIEW_SCHEDULED,
            OFFERED,
            ACCEPTED,
            REJECTED
        It can be withdrawn (deleted) if status is SUBMITTED/UNDER_REVIEW/INTERVIEW_SCHEDULED.
        */
        JobApplicationStatus status = jobApplication.getStatus();
        if (status == OFFERED || status == ACCEPTED || status == REJECTED) {
            throw new ConflictException("Job application cannot be deleted if state is OFFERED, ACCEPTED or REJECTED");
        }

        jobApplicationRepository.deleteById(applicationId);
    }

    @Override
    public List<JobApplicationEmployeeDto> getUnmanagedApplicationsByJobPosting(Long jobPostingId) {
        jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found"));

        var applications = jobApplicationRepository.findByEmployeeAndJobPostingId(null, jobPostingId);
        if (applications.isEmpty()) {
            throw new ResourceNotFoundException("Job applications not found");
        }

        return applications.stream()
                .map(application -> modelMapper.map(application, JobApplicationEmployeeDto.class))
                .toList();
    }

    @Override
    public JobApplicationEmployeeDto getUnmanagedApplicationById(Long applicationId) {
        var application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (application.isManaged()) {
            throw new ConflictException("This job application is already managed");
        }

        return modelMapper.map(application, JobApplicationEmployeeDto.class);
    }

    @Override
    public JobApplicationEmployeeDto getManagedApplicationByIdForEmployee(Long applicationId, Long employeeId) {
        var application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This job application is not managed");
        }

        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }

        return modelMapper.map(application, JobApplicationEmployeeDto.class);
    }

    @Override
    public List<JobApplicationEmployeeDto> getManagedApplicationsByEmployee(Long employeeId) {
        var applications = jobApplicationRepository.findByEmployeeId(employeeId).stream()
                .map(application -> modelMapper.map(application, JobApplicationEmployeeDto.class))
                .toList();

        if (applications.isEmpty()) {
            throw new ResourceNotFoundException("No managed job applications found");
        }

        return applications.stream()
                .map(application -> modelMapper.map(application, JobApplicationEmployeeDto.class))
                .toList();
    }

    @Override
    public void setEmployee(Long employeeId, Long applicationId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.isManaged()) {
            throw new ConflictException("This application is already managed");
        }

        application.setEmployee(employee);
        application.setStatus(UNDER_REVIEW);
        jobApplicationRepository.save(application);
    }

    @Override
    public void updateApplicationStatus(Long employeeId, Long applicationId, JobApplicationStatus newStatus) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This application is not managed");
        }

        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }

        if (application.statusIsFinal()) {
            throw new ConflictException("You cannot edit this application's status");
        }

        JobApplicationStatus currentStatus = application.getStatus();
        if (!isStatusChangeAllowed(currentStatus, newStatus)) {
            throw new ConflictException("Status change not allowed");
        }

        application.setStatus(newStatus);
        jobApplicationRepository.save(application);
    }

    @Override
    public CandidateDto getCandidateProfileForApplication(Long employeeId, Long applicationId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This application is not managed");
        }

        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }

        return candidateService.findById(application.getCandidate().getId());
    }

    @Override
    public Resource loadResume(Long employeeId, Long applicationId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.isManaged()) {
            throw new ConflictException("This application is not managed");
        }

        if (!application.getEmployee().getId().equals(employeeId)) {
            throw new UnauthorizedException("Another employee is managing this job application");
        }

        return candidateService.loadResume(application.getCandidate().getId());
    }
}
