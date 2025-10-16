package com.vuk.spring_webapp.service.job_posting;

import com.vuk.spring_webapp.domain.job_posting.JobPosting;
import com.vuk.spring_webapp.domain.job_posting.JobPostingStatus;
import com.vuk.spring_webapp.exception.ConflictException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.repository.JobPostingRepository;
import com.vuk.spring_webapp.transfer.dto.JobPostingDto;
import com.vuk.spring_webapp.transfer.request.CreateJobPostingRequest;
import com.vuk.spring_webapp.transfer.request.UpdateJobPostingRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final ModelMapper modelMapper;

    @Override
    public JobPostingDto findById(Long id) {
        return jobPostingRepository.findById(id)
                .map(posting -> modelMapper.map(posting, JobPostingDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + id));
    }

    @Override
    public List<JobPostingDto> findAll() {
        return jobPostingRepository
                .findAll()
                .stream()
                .map(posting -> modelMapper.map(posting, JobPostingDto.class))
                .toList();
    }

    @Override
    public List<JobPostingDto> findAllByStatus(JobPostingStatus status) {
        return jobPostingRepository
                .findAllByStatus(status)
                .stream()
                .map(posting -> modelMapper.map(posting, JobPostingDto.class))
                .toList();
    }

    @Override
    public JobPostingDto create(CreateJobPostingRequest request) {
        if (request.getDateOfExpiration().before(new Date())) {
            throw new ConflictException("Invalid date of expiration");
        }

        JobPosting jobPosting = new JobPosting(
                request.getTitle(),
                request.getDescription(),
                new Date(),
                request.getDateOfExpiration(),
                JobPostingStatus.PUBLISHED
        );
        jobPosting = jobPostingRepository.save(jobPosting);
        return modelMapper.map(jobPosting, JobPostingDto.class);
    }

    @Override
    public void deleteById(Long id) {
        jobPostingRepository.findById(id)
                .ifPresentOrElse(jobPostingRepository::delete, () -> {
                    throw new ResourceNotFoundException("Job posting not found with id " + id);
                });
    }

    @Override
    public void updateById(Long id, UpdateJobPostingRequest request) {
        if (request.getDateOfExpiration().before(new Date())) {
            throw new ConflictException("Expiration date cannot be set before current time");
        }

        jobPostingRepository.findById(id).map(jobPosting -> {
            jobPosting.setTitle(request.getTitle());
            jobPosting.setDescription(request.getDescription());
            jobPosting.setDateOfExpiration(request.getDateOfExpiration());
            JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
            return modelMapper.map(updatedJobPosting, JobPostingDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Job posting not found with id " + id));
    }
}
