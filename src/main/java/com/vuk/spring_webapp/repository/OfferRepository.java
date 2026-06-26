package com.vuk.spring_webapp.repository;

import com.vuk.spring_webapp.domain.offer.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByJobApplicationId(Long id);
}
