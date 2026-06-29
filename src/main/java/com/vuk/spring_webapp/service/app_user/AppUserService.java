package com.vuk.spring_webapp.service.app_user;

import com.vuk.spring_webapp.domain.user.Role;
import com.vuk.spring_webapp.transfer.response.LoginSuccessResponse;

/**
 * Service for authenticating application users.
 *
 * @author Vuk Perovic
 */
public interface AppUserService {

    /**
     * Authenticates a user with the given credentials and role.
     *
     * <p>If valid credentials and role are provided, a JWT will get generated.</p>
     *
     * @param email    the user's email address
     * @param password the user's raw password
     * @param role     the role the user is authenticating as
     * @return {@link LoginSuccessResponse} containing the JWT and user's first name
     * @throws com.vuk.spring_webapp.exception.UnauthorizedException               if a user with provided email and role
     *                                                                             doesn't exist
     * @throws org.springframework.security.authentication.BadCredentialsException if incorrect credentials are passed,
     *                                                                             including email and password
     */
    LoginSuccessResponse authenticateUser(String email, String password, Role role);
}
