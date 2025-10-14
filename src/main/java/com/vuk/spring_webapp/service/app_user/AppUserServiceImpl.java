package com.vuk.spring_webapp.service.app_user;

import com.vuk.spring_webapp.domain.user.AppUser;
import com.vuk.spring_webapp.domain.user.Role;
import com.vuk.spring_webapp.exception.UnauthorizedException;
import com.vuk.spring_webapp.repository.AppUserRepository;
import com.vuk.spring_webapp.security.JwtUtil;
import com.vuk.spring_webapp.transfer.response.LoginSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public LoginSuccessResponse authenticateUser(String email, String password, Role role) {
        if (!userRepository.existsByEmailAndRole(email, role)) {
            throw new UnauthorizedException("Unauthorized");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser user = userRepository.findByEmail(email);
        return new LoginSuccessResponse(jwtUtil.generateToken(user.getId(), userDetails.getUsername(), role));
    }
}
