package com.vuk.spring_webapp.service.interview;

import com.vuk.spring_webapp.transfer.dto.InterviewDto;
import com.vuk.spring_webapp.transfer.request.CreateInterviewRequest;

import java.util.List;

/**
 * Service for working with interviews.
 *
 * @author Vuk Perovic
 */
public interface InterviewService {

    /**
     * Fetches a list of interviews for a job application the employee manages.
     *
     * @param employeeId       employee's unique identifier
     * @param jobApplicationId job application's unique identifier
     * @return list of {@link InterviewDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application isn't yet managed by any employee
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if another employee is managing the job application
     */
    List<InterviewDto> findAllForEmployee(Long employeeId, Long jobApplicationId);

    /**
     * Fetches a list of interviews for a candidate's job application.
     *
     * @param candidateId      candidate's unique identifier
     * @param jobApplicationId job application's unique identifier
     * @return list of {@link InterviewDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if the job application belongs to another candidate
     */
    List<InterviewDto> findAllForCandidate(Long candidateId, Long jobApplicationId);

    /**
     * Creates (schedules) a new interview for a job application.
     *
     * <p>Upon scheduling the interview, the job application status is set to
     * {@link com.vuk.spring_webapp.domain.job_application.JobApplicationStatus#INTERVIEW_SCHEDULED}.</p>
     *
     * @param employeeId employee's unique identifier
     * @param request    as specified in {@link CreateInterviewRequest}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application isn't yet managed by any employee
     *                                                                   , or invalid {@code timeScheduled} was passed through the request,
     *                                                                   or the job application's status doesn't allow scheduling an interview
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if another employee is managing the job application
     */
    void createInterview(Long employeeId, CreateInterviewRequest request);

    /**
     * Deletes an interview.
     *
     * @param employeeId  employee's unique identifier
     * @param interviewId interview's unique identifier
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no interview with specified ID
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if another employee is managing the associated
     *                                                                   job application
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the interview's {@code timeScheduled}
     *                                                                   has passed
     */
    void deleteInterview(Long employeeId, Long interviewId);
}
