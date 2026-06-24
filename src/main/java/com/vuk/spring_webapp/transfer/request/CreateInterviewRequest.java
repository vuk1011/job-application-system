package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateInterviewRequest {
    private String title;
    private String description;
    private LocalDateTime timeScheduled;
    private Long jobApplicationId;
}
