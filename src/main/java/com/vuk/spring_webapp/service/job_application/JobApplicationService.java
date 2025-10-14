package com.vuk.spring_webapp.service.job_application;

import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import com.vuk.spring_webapp.transfer.dto.CandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationCandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationEmployeeDto;
import com.vuk.spring_webapp.transfer.request.SubmitJobApplicationRequest;
import org.springframework.core.io.Resource;

import java.util.List;

public interface JobApplicationService {

    void submitJobApplication(Long candidateId, SubmitJobApplicationRequest request);

    List<JobApplicationCandidateDto> getAllByCandidateId(Long candidateId);

    JobApplicationCandidateDto getByIdForCandidate(Long candidateId, Long applicationId);

    void deleteById(Long candidateId, Long applicationId);

    List<JobApplicationEmployeeDto> getUnmanagedApplicationsByJobPosting(Long jobPostingId);

    JobApplicationEmployeeDto getUnmanagedApplicationById(Long applicationId);

    JobApplicationEmployeeDto getManagedApplicationByIdForEmployee(Long applicationId, Long employeeId);

    List<JobApplicationEmployeeDto> getManagedApplicationsByEmployee(Long employeeId);

    void setEmployee(Long employeeId, Long applicationId);

    void updateApplicationStatus(Long employeeId, Long applicationId, JobApplicationStatus newStatus);

    CandidateDto getCandidateProfileForApplication(Long employeeId, Long applicationId);

    Resource loadResume(Long employeeId, Long applicationId);
}
