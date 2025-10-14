package com.vuk.spring_webapp.service.candidate;

import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.exception.EmailInUseException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.ResumeNotUploadedException;
import com.vuk.spring_webapp.repository.AppUserRepository;
import com.vuk.spring_webapp.repository.CandidateRepository;
import com.vuk.spring_webapp.transfer.dto.CandidateDto;
import com.vuk.spring_webapp.transfer.request.RegisterCandidateRequest;
import com.vuk.spring_webapp.transfer.request.UpdateCandidateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final AppUserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public void register(RegisterCandidateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailInUseException("Email is already in use");
        }
        Candidate candidate = new Candidate(
                request.getFirstName(),
                request.getLastName(),
                request.getSex(),
                request.getPhone(),
                request.getAddress(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(candidate);
    }

    @Override
    public Resource loadResume(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        byte[] resumeData = candidate.getResume();
        if (resumeData == null || resumeData.length == 0) {
            throw new ResumeNotUploadedException("Resume not uploaded");
        }
        return new ByteArrayResource(resumeData);
    }

    @Override
    public void updateResume(Long candidateId, MultipartFile file) throws IOException {
        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF resumes are allowed");
        }
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        candidate.setResume(file.getBytes());
        candidateRepository.save(candidate);
    }

    @Override
    public void deleteResume(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        candidate.setResume(null);
        candidateRepository.save(candidate);
    }

    @Override
    public CandidateDto findById(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(candidate -> modelMapper.map(candidate, CandidateDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
    }

    @Override
    public Long getIdByEmail(String email) {
        return candidateRepository.findByEmail(email).getId();
    }

    @Override
    public CandidateDto updateProfileInfo(Long candidateId, UpdateCandidateProfileRequest request) {
        return candidateRepository.findById(candidateId).map(candidate -> {
            candidate.setFirstName(request.getFirstName());
            candidate.setLastName(request.getLastName());
            candidate.setSex(request.getSex());
            candidate.setPhone(request.getPhone());
            candidate.setAddress(request.getAddress());
            candidate = candidateRepository.save(candidate);
            return modelMapper.map(candidate, CandidateDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
    }
}
