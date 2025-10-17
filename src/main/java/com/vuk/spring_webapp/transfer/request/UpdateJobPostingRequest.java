package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateJobPostingRequest {
    private String title;
    private String description;
    private LocalDate dateOfExpiration;
}
