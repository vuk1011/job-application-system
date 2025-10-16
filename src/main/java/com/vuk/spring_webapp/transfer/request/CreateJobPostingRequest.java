package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

import java.util.Date;

@Data
public class CreateJobPostingRequest {
    private String title;
    private String description;
    private Date dateOfExpiration;
}
