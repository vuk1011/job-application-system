package com.vuk.spring_webapp.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginSuccessResponse {
    String jwt;
    String firstName;
}
