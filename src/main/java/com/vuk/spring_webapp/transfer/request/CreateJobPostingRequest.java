package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateJobPostingRequest {
    private String title;
    private String description;
    private LocalDate dateOfExpiration;
}
