package com.vuk.spring_webapp.controller.candidate;

import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.service.candidate.CandidateService;
import com.vuk.spring_webapp.transfer.request.UpdateCandidateProfileRequest;
import com.vuk.spring_webapp.transfer.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/candidates/me")
public class CandidateController {

    private final CandidateService candidateService;

    private Long getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return candidateService.getIdByEmail(email);
    }

    @GetMapping("/resume")
    public ResponseEntity<Resource> getResume() {
        try {
            Resource resume = candidateService.loadResume(getId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(CONTENT_DISPOSITION, "inline; filename=\"resume.pdf\"")
                    .body(resume);
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
    }

    @PutMapping("/resume")
    public ResponseEntity<ApiResponse> uploadResume(@RequestParam MultipartFile file) {
        try {
            candidateService.updateResume(getId(), file);
            return ResponseEntity.ok(new ApiResponse("Resume uploaded successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/resume")
    public ResponseEntity<ApiResponse> deleteResume() {
        try {
            candidateService.deleteResume(getId());
            return ResponseEntity.ok(new ApiResponse("Resume deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfileInfo() {
        try {
            var response = candidateService.findById(getId());
            return ResponseEntity.ok(new ApiResponse("Profile information retrieved successfully", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfileInfo(@RequestBody UpdateCandidateProfileRequest request) {
        try {
            var response = candidateService.updateProfileInfo(getId(), request);
            return ResponseEntity.ok(new ApiResponse("Profile information updated successfully", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
