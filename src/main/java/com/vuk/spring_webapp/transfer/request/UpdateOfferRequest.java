package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

/**
 * Request object for updating (accepting/rejecting) an {@link com.vuk.spring_webapp.domain.offer.Offer}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code accepted (boolean)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class UpdateOfferRequest {
    private boolean accepted;
}
