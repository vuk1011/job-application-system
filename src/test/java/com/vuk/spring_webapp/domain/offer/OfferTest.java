package com.vuk.spring_webapp.domain.offer;

import com.vuk.spring_webapp.domain.job_application.JobApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Offer Unit Tests")
class OfferTest {

    private static final Long ID = 1L;
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

    @Test
    @DisplayName("toString includes all basic fields")
    void toStringIncludesAllBasicFields() {
        Offer offer = new Offer(NAME, null);
        offer.setId(ID);

        String result = offer.toString();

        assertTrue(result.contains("id=" + ID));
        assertTrue(result.contains(", name='" + NAME + '\''));
        assertTrue(result.contains(", accepted=null"));
    }

    @Test
    @DisplayName("toString includes ID for associated entities")
    void toStringIncludesIdForAssociatedEntities() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(ID);
        Offer offer = new Offer(NAME, jobApplication);

        String result = offer.toString();

        assertTrue(result.contains("jobApplicationId=" + ID));
    }

    @Test
    @DisplayName("toString handles null associated entities without throwing an exception")
    void toStringHandlesNullAssociatedEntities() {
        Offer offer = new Offer();

        assertDoesNotThrow(offer::toString);

        String result = offer.toString();

        assertTrue(result.contains("jobApplicationId=null"));
    }

    @Test
    @DisplayName("equals returns true when offers have the same ID")
    void equalsReturnsTrueWhenSameId() {
        Offer offer1 = new Offer();
        Offer offer2 = new Offer();
        offer1.setId(1L);
        offer2.setId(1L);

        assertEquals(offer1, offer2);
    }

    @Test
    @DisplayName("equals returns false when offers don't have the same ID")
    void equalsReturnsFalseWhenDifferentId() {
        Offer offer1 = new Offer();
        Offer offer2 = new Offer();
        offer1.setId(1L);
        offer2.setId(2L);

        assertNotEquals(offer1, offer2);
    }

    @Test
    @DisplayName("hashCode computes value based on ID only")
    void hashCodeComputesBasedOnId() {
        Offer offer1 = new Offer();
        Offer offer2 = new Offer();
        offer1.setId(1L);
        offer2.setId(1L);

        assertEquals(offer1.hashCode(), offer2.hashCode());
    }
}