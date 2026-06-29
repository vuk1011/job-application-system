package com.vuk.spring_webapp.transfer.dto;

import lombok.Data;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.offer.Offer}.
 *
 * @author Vuk Perovic
 */
@Data
public class OfferDto {
    private Long id;
    private String name;
    private Boolean accepted;
}
