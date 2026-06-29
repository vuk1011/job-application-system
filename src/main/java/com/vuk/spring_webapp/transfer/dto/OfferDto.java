package com.vuk.spring_webapp.transfer.dto;

import lombok.Data;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.offer.Offer}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code id (Long)}</li>
 *     <li>{@code name (String)}</li>
 *     <li>{@code accepted (Boolean)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class OfferDto {
    private Long id;
    private String name;
    private Boolean accepted;
}
