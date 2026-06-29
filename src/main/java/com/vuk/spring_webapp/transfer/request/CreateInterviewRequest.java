package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request object for creating an {@link com.vuk.spring_webapp.domain.interview.Interview}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code title (String)}</li>
 *     <li>{@code description (String)}</li>
 *     <li>{@code timeScheduled (LocalDateTime)}</li>
 *     <li>{@code jobApplicationId (Long)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class CreateInterviewRequest {
    private String title;
    private String description;
    private LocalDateTime timeScheduled;
    private Long jobApplicationId;
}
