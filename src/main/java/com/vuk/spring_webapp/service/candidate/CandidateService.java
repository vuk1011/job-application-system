package com.vuk.spring_webapp.service.candidate;

import com.vuk.spring_webapp.transfer.dto.CandidateDto;
import com.vuk.spring_webapp.transfer.request.RegisterCandidateRequest;
import com.vuk.spring_webapp.transfer.request.UpdateCandidateProfileRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CandidateService {

    void register(RegisterCandidateRequest request);

    Resource loadResume(Long candidateId);

    void updateResume(Long candidateId, MultipartFile file) throws IOException;

    void deleteResume(Long candidateId);

    CandidateDto findById(Long candidateId);

    Long getIdByEmail(String email);

    CandidateDto updateProfileInfo(Long candidateId, UpdateCandidateProfileRequest request);
}
