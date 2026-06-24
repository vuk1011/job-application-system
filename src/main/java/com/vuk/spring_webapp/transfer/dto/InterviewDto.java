package com.vuk.spring_webapp.transfer.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime timeScheduled;
}
