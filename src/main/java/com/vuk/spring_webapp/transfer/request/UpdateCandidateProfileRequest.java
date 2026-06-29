package com.vuk.spring_webapp.transfer.request;

import com.vuk.spring_webapp.domain.user.Sex;
import lombok.Data;

/**
 * Request object for updating the {@link com.vuk.spring_webapp.domain.user.Candidate}'s profile.
 *
 * <p>Contains fields:</p>
 * <ul>
 *     <li>{@code firstName (String)}</li>
 *     <li>{@code lastName (String)}</li>
 *     <li>{@code sex (Sex)}</li>
 *     <li>{@code phone (String)}</li>
 *     <li>{@code address (String)}</li>
 * </ul>
 *
 * @author Vuk Perovic
 */
@Data
public class UpdateCandidateProfileRequest {
    private String firstName;
    private String lastName;
    private Sex sex;
    private String phone;
    private String address;
}
