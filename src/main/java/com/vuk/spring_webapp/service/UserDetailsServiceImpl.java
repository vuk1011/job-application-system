package com.vuk.spring_webapp.service;

import com.vuk.spring_webapp.domain.user.AppUser;
import com.vuk.spring_webapp.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        String roleName = "ROLE_" + user.getRole().name();
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
