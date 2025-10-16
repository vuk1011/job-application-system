package com.vuk.spring_webapp.service.job_posting;

import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import com.vuk.spring_webapp.transfer.dto.JobPostingDto;
import com.vuk.spring_webapp.transfer.request.CreateJobPostingRequest;
import com.vuk.spring_webapp.transfer.request.UpdateJobPostingRequest;

import java.util.List;

public interface JobPostingService {

    JobPostingDto findById(Long id);

    List<JobPostingDto> findAll();

    List<JobPostingDto> findAllByStatus(JobPostingStatus status);

    JobPostingDto create(CreateJobPostingRequest request);

    void deleteById(Long id);

    void updateById(Long id, UpdateJobPostingRequest request);
}
