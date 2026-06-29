package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

/**
 * Request object for creating an {@link com.vuk.spring_webapp.domain.offer.Offer}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code name (String)}</li>
 *     <li>{@code jobApplicationId (Long)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class CreateOfferRequest {
    private String name;
    private Long jobApplicationId;
}
