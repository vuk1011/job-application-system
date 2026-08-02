package com.vuk.spring_webapp.service.candidate;

import com.vuk.spring_webapp.domain.user.Candidate;
import com.vuk.spring_webapp.domain.user.Sex;
import com.vuk.spring_webapp.exception.EmailInUseException;
import com.vuk.spring_webapp.exception.ResourceNotFoundException;
import com.vuk.spring_webapp.exception.ResumeNotUploadedException;
import com.vuk.spring_webapp.repository.AppUserRepository;
import com.vuk.spring_webapp.repository.CandidateRepository;
import com.vuk.spring_webapp.transfer.request.RegisterCandidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CandidateServiceImpl Unit Tests")
class CandidateServiceImplTest {

    @Mock
    private AppUserRepository userRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private MultipartFile file;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private RegisterCandidateRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterCandidateRequest();
        request.setFirstName("Aleksa");
        request.setLastName("Peric");
        request.setSex(Sex.MALE);
        request.setPhone("38162000111");
        request.setAddress("Bulevar 23");
        request.setEmail("aperic@example.com");
        request.setPassword("secret123");
    }

    @Test
    @DisplayName("register saves candidate with an encoded password when email's not in use")
    void registerEncodesPasswordAndSavesCandidateWhenEmailNotInUse() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        candidateService.register(request);

        ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);
        verify(userRepository).save(captor.capture());

        Candidate saved = captor.getValue();
        assertEquals("Aleksa", saved.getFirstName());
        assertEquals("Peric", saved.getLastName());
        assertEquals(Sex.MALE, saved.getSex());
        assertEquals("38162000111", saved.getPhone());
        assertEquals("Bulevar 23", saved.getAddress());
        assertEquals("aperic@example.com", saved.getEmail());
        assertEquals("encodedPassword", saved.getPassword());

        verify(passwordEncoder).encode("secret123");
        verify(userRepository).existsByEmail("aperic@example.com");
        verifyNoMoreInteractions(userRepository, passwordEncoder);
        verifyNoInteractions(candidateRepository, modelMapper);
    }

    @Test
    @DisplayName("register throws EmailInUseException when an account with the same email already exists")
    void registerThrowsEmailInUseExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        EmailInUseException exception = assertThrows(
                EmailInUseException.class,
                () -> candidateService.register(request)
        );
        assertEquals("Email is already in use", exception.getMessage());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, candidateRepository, modelMapper);
    }

    @Test
    @DisplayName("loadResume returns ByteArrayResource when the resume exists")
    void loadResumeReturnsByteArrayResourceWhenResumeExists() {
        Long candidateId = 1L;
        byte[] resumeData = "PDF content".getBytes();

        Candidate candidate = new Candidate();
        candidate.setResume(resumeData);

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        Resource result = candidateService.loadResume(candidateId);

        assertInstanceOf(ByteArrayResource.class, result);
        assertArrayEquals(resumeData, ((ByteArrayResource) result).getByteArray());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ResourceNotFoundException when the candidate isn't found")
    void loadResumeThrowsResourceNotFoundExceptionWhenCandidateNotFound() {
        Long candidateId = 1L;
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> candidateService.loadResume(candidateId)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ResumeNotUploadedException when resume is null")
    void loadResumeThrowsResumeNotUploadedExceptionWhenResumeIsNull() {
        Long candidateId = 1L;
        Candidate candidate = new Candidate();

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResumeNotUploadedException exception = assertThrows(
                ResumeNotUploadedException.class,
                () -> candidateService.loadResume(candidateId)
        );
        assertEquals("Resume not uploaded", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    @DisplayName("loadResume throws ResumeNotUploadedException when resume is empty")
    void loadResumeThrowsResumeNotUploadedExceptionWhenResumeIsEmpty() {
        Long candidateId = 1L;
        Candidate candidate = new Candidate();
        candidate.setResume(new byte[0]);

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResumeNotUploadedException exception = assertThrows(
                ResumeNotUploadedException.class,
                () -> candidateService.loadResume(candidateId)
        );
        assertEquals("Resume not uploaded", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    @DisplayName("updateResume overwrites resume when file is a PDF and candidate exists")
    void updateResumeSavesNewResume() throws IOException {
        Long candidateId = 1L;
        byte[] newResumeData = "new PDF content".getBytes();

        Candidate candidate = new Candidate();
        candidate.setResume("old PDF content".getBytes());

        when(file.getContentType()).thenReturn("application/pdf");
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(file.getBytes()).thenReturn(newResumeData);

        candidateService.updateResume(candidateId, file);

        assertArrayEquals(newResumeData, candidate.getResume());

        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository).save(candidate);
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    @DisplayName("updateResume throws IllegalArgumentException when file is not a PDF")
    void updateResumeThrowsIllegalArgumentExceptionForNonPdfFile() {
        Long candidateId = 1L;
        when(file.getContentType()).thenReturn("image/png");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> candidateService.updateResume(candidateId, file)
        );
        assertEquals("Only PDF resumes are allowed", exception.getMessage());

        verifyNoInteractions(candidateRepository, userRepository, passwordEncoder, modelMapper);
    }

    @Test
    @DisplayName("updateResume throws ResourceNotFoundException when candidate does not exist")
    void updateResumeThrowsResourceNotFoundException() {
        Long candidateId = 1L;
        when(file.getContentType()).thenReturn("application/pdf");
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> candidateService.updateResume(candidateId, file)
        );
        assertEquals("Candidate not found", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository, never()).save(any());
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(userRepository, passwordEncoder, modelMapper);
    }

    @Test
    @DisplayName("updateResume propagates IOException when reading file bytes fails")
    void updateResumePropagatesIOException() throws IOException {
        Long candidateId = 1L;
        Candidate candidate = new Candidate();

        when(file.getContentType()).thenReturn("application/pdf");
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(file.getBytes()).thenThrow(new IOException("Failed to read file"));

        IOException exception = assertThrows(
                IOException.class,
                () -> candidateService.updateResume(candidateId, file)
        );
        assertEquals("Failed to read file", exception.getMessage());

        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository, never()).save(any());
        verifyNoMoreInteractions(candidateRepository);
        verifyNoInteractions(userRepository, passwordEncoder, modelMapper);
    }
}
