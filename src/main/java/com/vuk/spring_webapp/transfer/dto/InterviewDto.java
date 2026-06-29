package com.vuk.spring_webapp.transfer.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.interview.Interview}.
 *
 * @author Vuk Perovic
 */
@Data
public class InterviewDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime timeScheduled;
}
