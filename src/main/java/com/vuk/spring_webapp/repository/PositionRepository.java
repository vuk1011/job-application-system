package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.position.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}
