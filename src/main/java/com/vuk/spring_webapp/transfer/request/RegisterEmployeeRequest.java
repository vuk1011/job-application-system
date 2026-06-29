package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.user.Sex;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request object for registering (creating) an {@link com.vuk.spring_webapp.domain.user.Employee} account.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code firstName (String)}</li>
 *     <li>{@code lastName (String)}</li>
 *     <li>{@code sex (Sex)}</li>
 *     <li>{@code phone (String)}</li>
 *     <li>{@code address (String)}</li>
 *     <li>{@code email (String)}</li>
 *     <li>{@code password (String)}</li>
 *     <li>{@code nationalId (String)}</li>
 *     <li>{@code dateOfBirth (LocalDate)}</li>
 *     <li>{@code dateOfHire (LocalDate)}</li>
 *     <li>{@code companyId (Long)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class RegisterEmployeeRequest {
    private String firstName;
    private String lastName;
    private Sex sex;
    private String phone;
    private String address;
    private String email;
    private String password;
    private String nationalId;
    private LocalDate dateOfBirth;
    private LocalDate dateOfHire;
    private Long companyId;
}
