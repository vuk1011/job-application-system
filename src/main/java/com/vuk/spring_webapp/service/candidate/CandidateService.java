package com.vuk.spring_webapp.service.candidate;

import com.vuk.spring_webapp.transfer.dto.CandidateDto;
import com.vuk.spring_webapp.transfer.request.RegisterCandidateRequest;
import com.vuk.spring_webapp.transfer.request.UpdateCandidateProfileRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for creating and managing candidate profiles.
 *
 * <p>It includes necessary file operations regarding candidate resume.</p>
 *
 * @author Vuk Perovic
 */
public interface CandidateService {

    /**
     * Registers (creates) a new candidate account in the system.
     *
     * <p>First, checks if provided email's in use.</p>
     *
     * @param request as specified by {@link RegisterCandidateRequest}, contains user data and credentials
     * @throws com.vuk.spring_webapp.exception.EmailInUseException if provided email is in use
     */
    void register(RegisterCandidateRequest request);

    /**
     * Returns candidate's uploaded resume that is a PDF file.
     *
     * @param candidateId candidate's unique identifier
     * @return {@link Resource} represents the PDF file
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException  if there's no candidate with specified ID
     * @throws com.vuk.spring_webapp.exception.ResumeNotUploadedException if the candidate hasn't yet uploaded a resume
     */
    Resource loadResume(Long candidateId);

    /**
     * Uploads candidate's new resume.
     *
     * <p>Previously uploaded resume will be overwritten.</p>
     *
     * @param candidateId candidate's unique identifier
     * @param file        file representation as specified in {@link MultipartFile}
     * @throws IllegalArgumentException                                  if the file sent isn't a PDF file
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no candidate with specified ID
     * @throws IOException                                               if reading the file's bytes fails
     */
    void updateResume(Long candidateId, MultipartFile file) throws IOException;

    /**
     * Deletes previously uploaded resume file.
     *
     * @param candidateId candidate's unique identifier
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no candidate with specified ID
     */
    void deleteResume(Long candidateId);

    /**
     * Fetches candidate's account data.
     *
     * @param candidateId candidate's unique identifier
     * @return {@link CandidateDto} containing candidate data
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no candidate with specified ID
     */
    CandidateDto findById(Long candidateId);

    /**
     * Fetches candidate's unique identifier based on provided email address.
     *
     * <p>Important for authorization check at the controller level.</p>
     *
     * @param email candidate's email address
     * @return candidate's unique identifier
     */
    Long getIdByEmail(String email);

    /**
     * Updates candidate's personal data.
     *
     * <p>Data that can be updated includes:</p>
     * <ul>
     *     <li>first name</li>
     *     <li>last name</li>
     *     <li>sex</li>
     *     <li>phone</li>
     *     <li>address</li>
     * </ul>
     *
     * @param candidateId candidate's unique identifier
     * @param request     as specified by {@link UpdateCandidateProfileRequest}
     * @return {@link CandidateDto} containing candidate data
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no candidate with specified ID
     */
    CandidateDto updateProfileInfo(Long candidateId, UpdateCandidateProfileRequest request);
}
