package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.user.Sex;
import lombok.Data;

import java.util.Date;

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
    private Date dateOfBirth;
    private Date dateOfHire;
}
