package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
