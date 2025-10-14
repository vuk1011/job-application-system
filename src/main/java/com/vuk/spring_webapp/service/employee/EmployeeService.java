package com.vuk.spring_webapp.service.employee;

import com.vuk.spring_webapp.transfer.dto.EmployeeDto;
import com.vuk.spring_webapp.transfer.request.RegisterEmployeeRequest;

public interface EmployeeService {

    void register(RegisterEmployeeRequest request);

    Long getIdByEmail(String email);
}
