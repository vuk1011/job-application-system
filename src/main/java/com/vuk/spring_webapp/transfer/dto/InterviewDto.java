package com.vuk.spring_webapp.transfer.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.interview.Interview}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code id (Long)}</li>
 *     <li>{@code title (String)}</li>
 *     <li>{@code description (String)}</li>
 *     <li>{@code timeScheduled (LocalDateTime)}</li>
 * </ul>
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
