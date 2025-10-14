package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.user.AppUser;
import com.vuk.spring_webapp.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndRole(String email, Role role);
}
