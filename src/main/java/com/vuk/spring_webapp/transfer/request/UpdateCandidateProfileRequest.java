package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.user.Sex;
import lombok.Data;

@Data
public class UpdateCandidateProfileRequest {
    private String firstName;
    private String lastName;
    private Sex sex;
    private String phone;
    private String address;
}
