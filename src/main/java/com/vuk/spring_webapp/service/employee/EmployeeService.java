package com.vuk.spring_webapp.service.employee;

import com.vuk.spring_webapp.transfer.request.RegisterEmployeeRequest;

/**
 * Service for creating and managing employee profiles.
 *
 * @author Vuk Perovic
 */
public interface EmployeeService {

    /**
     * Registers (creates) a new employee account in the system.
     *
     * <p>Checks if provided email's in use and if the company exists.</p>
     *
     * @param request as specified in {@link RegisterEmployeeRequest}, contains company and personal data and credentials
     * @throws IllegalArgumentException                                  if no company ID was provided
     * @throws com.vuk.spring_webapp.exception.EmailInUseException       if provided email is in use
     * @throws com.vuk.spring_webapp.exception.ResourceNotFoundException if there's no company with specified ID
     */
    void register(RegisterEmployeeRequest request);

    /**
     * Fetches employee's unique identifier based on provided email address.
     *
     * <p>Important for authorization check at the controller level.</p>
     *
     * @param email employee's email address
     * @return employee's unique identifier
     */
    Long getIdByEmail(String email);
}
