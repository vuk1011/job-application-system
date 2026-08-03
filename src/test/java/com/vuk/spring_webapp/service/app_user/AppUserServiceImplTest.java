package com.vuk.spring_webapp.service.app_user;

import com.vuk.spring_webapp.domain.user.AppUser;
import com.vuk.spring_webapp.domain.user.Role;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.repository.AppUserRepository;
import com.vuk.spring_webapp.security.JwtUtil;
import com.vuk.spring_webapp.transfer.response.LoginSuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserServiceImpl Unit Tests")
class AppUserServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AppUserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;
    @Mock
    private AppUser appUser;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    private String email;
    private String password;
    private Role role;
    private Long userId;
    private String firstName;
    private String token;

    @BeforeEach
    void setUp() {
        email = "aleksandar123@gmail.com";
        password = "secret123";
        role = Role.EMPLOYEE;
        userId = 1L;
        firstName = "Aleksandar";
        token = "secret-token123";
    }

    @Test
    @DisplayName("authenticateUser returns LoginSuccessResponse when credentials are valid")
    void authenticateUserWhenValidCredentialsReturnsLoginSuccessResponse() {
        when(userRepository.existsByEmailAndRole(email, role)).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(appUser);
        when(appUser.getId()).thenReturn(userId);
        when(appUser.getFirstName()).thenReturn(firstName);
        when(jwtUtil.generateToken(userId, email, role)).thenReturn(token);

        LoginSuccessResponse response = appUserService.authenticateUser(email, password, role);

        assertNotNull(response);
        assertEquals(token, response.getJwt());
        assertEquals(firstName, response.getFirstName());

        verify(userRepository).existsByEmailAndRole(email, role);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(email);
        verify(jwtUtil).generateToken(userId, email, role);
    }

    @Test
    @DisplayName("authenticateUser throws UnauthorizedException when user does not exist by email and role")
    void authenticateUserWhenUserDoesNotExistThrowsUnauthorizedException() {
        when(userRepository.existsByEmailAndRole(email, role)).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> appUserService.authenticateUser(email, password, role));

        assertEquals("Unauthorized", exception.getMessage());
        verify(userRepository).existsByEmailAndRole(email, role);
        verifyNoInteractions(authenticationManager, jwtUtil);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("authenticateUser propagates exception thrown by AuthenticationManager on bad credentials")
    void authenticateUserWhenAuthenticationFailsPropagatesException() {
        when(userRepository.existsByEmailAndRole(email, role)).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        assertThrows(org.springframework.security.authentication.BadCredentialsException.class,
                () -> appUserService.authenticateUser(email, password, role));

        verify(userRepository).existsByEmailAndRole(email, role);
        verify(userRepository, never()).findByEmail(anyString());
        verifyNoInteractions(jwtUtil);
    }
}