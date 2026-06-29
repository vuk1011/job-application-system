package com.vuk.spring_webapp.transfer.dto;

import com.vuk.spring_webapp.domain.user.Sex;
import lombok.Data;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.user.Candidate}.
 *
 * @author Vuk Perovic
 */
@Data
public class CandidateDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Sex sex;
    private String phone;
    private String address;
    private String email;
}
