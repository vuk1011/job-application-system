package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.interview.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
