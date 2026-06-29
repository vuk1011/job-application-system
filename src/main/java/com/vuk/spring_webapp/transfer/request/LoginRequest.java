package com.vuk.spring_webapp.transfer.request;

import lombok.Data;

/**
 * Request object for logging into the system.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code email (String)}</li>
 *     <li>{@code password (String)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}
