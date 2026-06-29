package com.vuk.spring_webapp.service.offer;

import com.vuk.spring_webapp.transfer.dto.OfferDto;
import com.vuk.spring_webapp.transfer.request.CreateOfferRequest;
import com.vuk.spring_webapp.transfer.request.UpdateOfferRequest;

import java.util.List;

/**
 * Service for managing offers.
 *
 * <p>Some operations can affect a {@link com.vuk.spring_webapp.domain.job_application.JobApplication}'s status.</p>
 *
 * @author Vuk Perovic
 */
public interface OfferService {

    /**
     * Fetches a list of offers for a job application managed by the employee.
     *
     * @param employeeId       employee's unique identifier
     * @param jobApplicationId job application's unique identifier
     * @return list of {@link OfferDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application isn't yet managed
     *                                                                   by any employee
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if the job application's managed
     *                                                                   by another employee
     */
    List<OfferDto> findAllForEmployee(Long employeeId, Long jobApplicationId);

    /**
     * Fetches a list of offers for the candidate's job application.
     *
     * @param candidateId      candidate's unique identifier
     * @param jobApplicationId job application's unique identifier
     * @return list of {@link OfferDto}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if the job application belongs to another candidate
     */
    List<OfferDto> findAllForCandidate(Long candidateId, Long jobApplicationId);

    /**
     * Creates an offer for a job application the employee manages.
     *
     * <p>On success, job application's status is set to
     * {@link com.vuk.spring_webapp.domain.job_application.JobApplicationStatus#OFFERED}.</p>
     *
     * @param employeeId employee's unique identifier
     * @param request    as specified in {@link CreateOfferRequest}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no job application with specified ID
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application isn't managed by any
     *                                                                   employee or the job application's status
     *                                                                   doesn't allow the action
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if the job application's managed
     *                                                                   by another employee
     */
    void createOffer(Long employeeId, CreateOfferRequest request);

    /**
     * Deletes an offer for a job application the employee manages.
     *
     * <p>The offer has to have property {@code accepted} not set (null).</p>
     *
     * @param employeeId employee's unique identifier
     * @param offerId    offer's unique identifier
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no offer with specified ID
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if the job application's managed
     *                                                                   by another employee
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the offer has already been accepted or rejected
     * @see com.vuk.spring_webapp.domain.offer.Offer
     */
    void deleteOffer(Long employeeId, Long offerId);

    /**
     * Updates an offer for the candidate's job application.
     *
     * <p>An offer can get either accepted or rejected through this action.</p>
     * <p>On success, the job application can change in two ways:</p>
     * <ol>
     *     <li>Its status is set to {@link com.vuk.spring_webapp.domain.job_application.JobApplicationStatus#ACCEPTED}</li>
     *     <li>Its status is set to {@link com.vuk.spring_webapp.domain.job_application.JobApplicationStatus#REJECTED}</li>
     * </ol>
     *
     * @param candidateId candidate's unique identifier
     * @param offerId     offer's unique identifier
     * @param request     as specified in {@link UpdateOfferRequest}, sets value for field {@code accepted}
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no offer with specified ID
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException     if the offer belongs to another candidate
     * @throws com.vuk.spring_webapp.exception.ConflictException         if the job application status
     *                                                                   doesn't allow the change
     */
    void updateOffer(Long candidateId, Long offerId, UpdateOfferRequest request);
}
