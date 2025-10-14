package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
