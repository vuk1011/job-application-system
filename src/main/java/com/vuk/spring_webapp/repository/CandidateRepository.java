package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.user.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Candidate findByEmail(String email);
}
