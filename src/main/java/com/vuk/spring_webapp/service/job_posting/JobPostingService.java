package com.vuk.spring_webapp.service.job_posting;

import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import com.vuk.spring_webapp.transfer.dto.JobPostingDto;
import com.vuk.spring_webapp.transfer.request.CreateJobPostingRequest;
import com.vuk.spring_webapp.transfer.request.UpdateJobPostingRequest;

import java.util.List;

/**
 * Service for managing job postings.
 *
 * <p>Employees can view, create, edit and delete job postings.</p>
 * <p>Candidates can only view job postings.</p>
 *
 * @author Vuk Perovic
 */
public interface JobPostingService {

    /**
     * Fetches a job posting with specified id.
     *
     * @param id job posting's unique identifier
     * @return {@link JobPostingDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job posting with specified ID
     */
    JobPostingDto findById(Long id);

    /**
     * Fetches a list of job postings associated with employee's company.
     *
     * @return list of {@link JobPostingDto}
     */
    List<JobPostingDto> findAll();

    /**
     * Fetches a list of job postings that are in status {@link JobPostingStatus#PUBLISHED}.
     *
     * @return list of {@link JobPostingDto}
     */
    List<JobPostingDto> findAllPublished();

    JobPostingDto create(CreateJobPostingRequest request);

    void deleteById(Long id);

    void updateById(Long id, UpdateJobPostingRequest request);
}
