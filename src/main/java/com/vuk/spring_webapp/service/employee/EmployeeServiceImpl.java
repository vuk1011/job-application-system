package com.vuk.spring_webapp.service.employee;

import com.vuk.spring_webapp.domain.user.Employee;
import com.vuk.spring_webapp.exception.EmailInUseException;
import com.vuk.spring_webapp.repository.AppUserRepository;
import com.vuk.spring_webapp.repository.EmployeeRepository;
import com.vuk.spring_webapp.transfer.request.RegisterEmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final AppUserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterEmployeeRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailInUseException("Email is already in use");
        }
        Employee employee = new Employee(
                request.getFirstName(),
                request.getLastName(),
                request.getSex(),
                request.getPhone(),
                request.getAddress(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNationalId(),
                request.getDateOfBirth(),
                request.getDateOfHire()
        );
        userRepository.save(employee);
    }

    @Override
    public Long getIdByEmail(String email) {
        return employeeRepository.findByEmail(email).getId();
    }
}
