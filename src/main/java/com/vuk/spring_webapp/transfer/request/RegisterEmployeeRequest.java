package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.user.Sex;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterEmployeeRequest {
    private String firstName;
    private String lastName;
    private Sex sex;
    private String phone;
    private String address;
    private String email;
    private String password;
    private String nationalId;
    private LocalDate dateOfBirth;
    private LocalDate dateOfHire;
}
