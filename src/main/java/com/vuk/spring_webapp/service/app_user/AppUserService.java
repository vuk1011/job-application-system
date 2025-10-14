package com.vuk.spring_webapp.service.app_user;

import com.vuk.spring_webapp.domain.user.Role;
import com.vuk.spring_webapp.transfer.response.LoginSuccessResponse;

public interface AppUserService {

    LoginSuccessResponse authenticateUser(String email, String password, Role role);
}
