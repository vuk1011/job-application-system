package com.vuk.spring_webapp.domain.offer;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Offer Unit Tests")
class OfferTest {

    private static final String NAME = "Initial employment offer";
    private static final JobApplication JOB_APPLICATION = new JobApplication();

    @Test
    @DisplayName("No args constructor creates an empty instance")
    void noArgsConstructorCreatesEmptyInstance() {
        Offer offer = new Offer();

        assertNull(offer.getId());
        assertNull(offer.getName());
        assertNull(offer.getAccepted());
        assertNull(offer.getJobApplication());
    }

    @Test
    @DisplayName("Parameterized constructor sets fields correctly")
    void argsConstructorSetsFields() {
        Offer offer = new Offer(NAME, JOB_APPLICATION);

        assertNull(offer.getId());
        assertNull(offer.getAccepted());

        assertEquals(NAME, offer.getName());
        assertEquals(JOB_APPLICATION, offer.getJobApplication());
    }

}