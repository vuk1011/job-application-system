package com.vuk.spring_webapp.service.job_application;

import com.vuk.spring_webapp.domain.job_application.JobApplicationStatus;
import com.vuk.spring_webapp.transfer.dto.CandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationCandidateDto;
import com.vuk.spring_webapp.transfer.dto.JobApplicationEmployeeDto;
import com.vuk.spring_webapp.transfer.request.SubmitJobApplicationRequest;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Service for managing job applications.
 *
 * <p>Various functionalities for both candidates and employees. Contains the core of the app's logic.</p>
 *
 * @author Vuk Perovic
 */
public interface JobApplicationService {

    /**
     * Submits (creates) a job application, for a candidate, for a job posting.
     *
     * <p>It's necessary that the candidate hasn't already applied for the same job posting and that the posting
     * hasn't expired.</p>
     *
     * @param candidateId candidate's unique identifier
     * @param request     as specified in {@link SubmitJobApplicationRequest}, contains the job posting's ID
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException  if there's no candidate or job posting
     *                                                                    with the specified ID
     * @throws com.vuk.spring_webapp.exception.ApplicationExistsException if the candidate's already applied for the same
     *                                                                    job posting
     * @throws com.vuk.spring_webapp.exception.JobPostingClosedException  if the job posting has expired i.e. has the status
     *                                                                    {@link com.vuk.spring_webapp.domain.job_posting.JobPostingStatus#CLOSED}
     */
    void submitJobApplication(Long candidateId, SubmitJobApplicationRequest request);

    /**
     * Fetches the list of candidate's job applications.
     *
     * @param candidateId candidate's unique identifier
     * @return list of {@link JobApplicationCandidateDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no candidate with specified ID
     */
    List<JobApplicationCandidateDto> getAllByCandidateId(Long candidateId);

    /**
     * Fetches candidate's job application with the specified ID.
     *
     * @param candidateId   candidate's unique identifier
     * @param applicationId job application's unique identifier
     * @return {@link JobApplicationCandidateDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no candidate or job application
     *                                                                   with specified ID
     */
    JobApplicationCandidateDto getByIdForCandidate(Long candidateId, Long applicationId);

    /**
     * Deletes the job application.
     *
     * <p>Represents the action of withdrawing the application.</p>
     *
     * @param candidateId   candidate's unique identifier
     * @param applicationId job application's unique identifier
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no candidate or job application
     *                                                                   with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application's status doesn't
     *                                                                   allow deletion
     */
    void deleteById(Long candidateId, Long applicationId);

    /**
     * Fetches the list of job applications that aren't managed by any employee, for a specified job posting.
     *
     * @param jobPostingId job posting's unique identifier
     * @return list of {@link JobApplicationEmployeeDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job posting with specified ID
     *                                                                   or if the job posting doesn't have any
     *                                                                   applications associated
     */
    List<JobApplicationEmployeeDto> getUnmanagedApplicationsByJobPosting(Long jobPostingId);

    /**
     * Fetches the job application with specified ID, that is not managed by any employee.
     *
     * @param applicationId job application's unique identifier
     * @return {@link JobApplicationEmployeeDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if there's already an employee managing
     *                                                                   the job application
     */
    JobApplicationEmployeeDto getUnmanagedApplicationById(Long applicationId);

    /**
     * Fetches the job application with specified ID, that is managed by specified employee.
     *
     * @param applicationId job application's unique identifier
     * @param employeeId    employee's unique identifier
     * @return {@link JobApplicationEmployeeDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if there's no employee managing the job application
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if there's another employee managing the job application
     */
    JobApplicationEmployeeDto getManagedApplicationByIdForEmployee(Long applicationId, Long employeeId);

    /**
     * Fetches the list of job applications that are managed by specified employee.
     *
     * @param employeeId employee's unique identifier
     * @return list of {@link JobApplicationEmployeeDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job applications the employee manages
     */
    List<JobApplicationEmployeeDto> getManagedApplicationsByEmployee(Long employeeId);

    /**
     * Assigns the specified job application to the specified employee to manage.
     *
     * <p>If successful, job application's status is set to
     * {@link JobApplicationStatus#UNDER_REVIEW}</p>
     *
     * @param employeeId    employee's unique identifier
     * @param applicationId job application's unique identifier
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no employee or job application
     *                                                                   with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application already has an employee
     *                                                                   managing it
     */
    void setEmployee(Long employeeId, Long applicationId);

    /**
     * Updates job application's status.
     *
     * @param employeeId    employee's unique identifier
     * @param applicationId job application's unique identifier
     * @param newStatus     new status
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no employee or job application
     *                                                                   with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the application's status change isn't
     *                                                                   allowed or if it's not yet managed by any employee
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if another employee's managing the job application
     * @see JobApplicationStatus
     */
    void updateApplicationStatus(Long employeeId, Long applicationId, JobApplicationStatus newStatus);

    /**
     * Fetches profile data of a candidate associated with the job application that the employee manages.
     *
     * @param employeeId    employee's unique identifier
     * @param applicationId job application's unique identifier
     * @return {@link CandidateDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no employee or job application
     *                                                                   with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application's not yet managed
     *                                                                   by any employee
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if another employee's managing the job application
     */
    CandidateDto getCandidateProfileForApplication(Long employeeId, Long applicationId);

    /**
     * Returns the uploaded resume of a candidate that's associated with a job application the employee maanges.
     *
     * @param employeeId    employee's unique identifier
     * @param applicationId job application's unique identifier
     * @return {@link Resource} represents the PDF file
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no employee or job application
     *                                                                   with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application's not yet managed
     *                                                                   by any employee
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if another employee's managing the job application
     */
    Resource loadResume(Long employeeId, Long applicationId);
}
