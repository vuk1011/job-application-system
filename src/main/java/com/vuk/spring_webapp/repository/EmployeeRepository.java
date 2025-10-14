package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.user.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByEmail(String email);
}
