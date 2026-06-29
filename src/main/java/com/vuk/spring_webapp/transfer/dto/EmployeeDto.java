package com.vuk.spring_webapp.transfer.dto;

import com.vuk.spring_webapp.domain.user.Sex;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data transfer object representing {@link com.vuk.spring_webapp.domain.user.Employee}.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code id (Long)}</li>
 *     <li>{@code firstName (String)}</li>
 *     <li>{@code lastName (String)}</li>
 *     <li>{@code sex (Sex)}</li>
 *     <li>{@code phone (String)}</li>
 *     <li>{@code address (String)}</li>
 *     <li>{@code email (String)}</li>
 *     <li>{@code nationalId (String)}</li>
 *     <li>{@code dateOfBirth (LocalDate)}</li>
 *     <li>{@code dateOfHire (LocalDate)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 * @see Sex
 */
@Data
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Sex sex;
    private String phone;
    private String address;
    private String email;
    private String nationalId;
    private LocalDate dateOfBirth;
    private LocalDate dateOfHire;
}
