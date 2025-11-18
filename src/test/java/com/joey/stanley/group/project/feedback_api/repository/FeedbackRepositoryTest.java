package com.joey.stanley.group.project.feedback_api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.joey.stanley.group.project.feedback_api.entity.Feedback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FeedbackRepositoryTest {

    @Autowired
    private FeedbackRepository feedbackRepository;

    private static String MOCK_MEMBER_ID = "m-1024";
    private static String MOCK_PROVIDER_NAME = "Dr. Unlicensed";
    
    private static Feedback createRawFeedback() {
        Feedback rawFeedback = new Feedback();
        rawFeedback.setMemberId(MOCK_MEMBER_ID);
        rawFeedback.setProviderName(MOCK_PROVIDER_NAME);
        rawFeedback.setRating(5);
        rawFeedback.setComment("Apparently not allowed to practice medicine, but still provided diabetic meds for me!");
        return rawFeedback;
    }

    private static Feedback[] createRawFeedbackSet() {
        Feedback rawFeedbackA = createRawFeedback();

        Feedback rawFeedbackB = new Feedback();
        rawFeedbackB.setMemberId(MOCK_MEMBER_ID);
        rawFeedbackB.setProviderName(MOCK_PROVIDER_NAME);
        rawFeedbackB.setRating(1);
        rawFeedbackB.setComment("Gave me burn cream for a deep laceration. Huh???");

        return new Feedback[] { rawFeedbackA, rawFeedbackB };
    }

    private static void comparedRawAndCookedFeedback(Feedback rawFeedback, Feedback cookedFeedback) throws Exception {
        assertNotNull(cookedFeedback.getId());
        assertEquals(rawFeedback.getMemberId(), cookedFeedback.getMemberId());
        assertEquals(rawFeedback.getProviderName(), cookedFeedback.getProviderName());
        assertEquals(rawFeedback.getRating(), cookedFeedback.getRating());
        assertEquals(rawFeedback.getComment(), cookedFeedback.getComment());
        assertNotNull(cookedFeedback.getSubmittedAt());
    }

    @Test
    void repository_success_createAndSave() throws Exception {
        Feedback rawFeedback = createRawFeedback();

        Feedback cookedFeedback = feedbackRepository.saveAndFlush(rawFeedback);

        assertNotNull(cookedFeedback);
        comparedRawAndCookedFeedback(rawFeedback, cookedFeedback);
    }

    @Test
    void repository_success_createAndSaveAndFind() throws Exception {
        Feedback rawFeedback = createRawFeedback();

        feedbackRepository.saveAndFlush(rawFeedback);

        Optional<Feedback> packedCookedFeedback = feedbackRepository.findById(rawFeedback.getId());

        assertTrue(packedCookedFeedback.isPresent());

        // Confirm that nothing was damaged during recall
        Feedback cookedFeedback = packedCookedFeedback.get();

        comparedRawAndCookedFeedback(rawFeedback, cookedFeedback);
    }

    @Test
    void repository_returnTwoItems_findMemberByFeedbackId() throws Exception {
        Feedback[] testSet = createRawFeedbackSet();
        Feedback rawFeedbackA = testSet[0];
        Feedback rawFeedbackB = testSet[1];

        feedbackRepository.saveAndFlush(rawFeedbackA);
        feedbackRepository.saveAndFlush(rawFeedbackB);

        List<Feedback> recalls = feedbackRepository.findByMemberId(MOCK_MEMBER_ID);

        assertEquals(2, recalls.size());

        Feedback cookedFeedbackA = recalls.get(0);
        Feedback cookedFeedbackB = recalls.get(1);

        assertNotNull(cookedFeedbackA);
        assertNotNull(cookedFeedbackA.getId());
        assertNotNull(cookedFeedbackB);
        assertNotNull(cookedFeedbackB.getId());
        assertNotEquals(cookedFeedbackA.getId(), cookedFeedbackB.getId());

        if (cookedFeedbackA.getId().equals(rawFeedbackA.getId())) {
            comparedRawAndCookedFeedback(rawFeedbackA, cookedFeedbackA);
            comparedRawAndCookedFeedback(rawFeedbackB, cookedFeedbackB);
        }
        else {
            comparedRawAndCookedFeedback(rawFeedbackA, cookedFeedbackB);
            comparedRawAndCookedFeedback(rawFeedbackB, cookedFeedbackA);
        }

        // The comments definitely won't be the same, so
        // that's how we know we have two different data,
        // even if somehow the same one got stashed with
        // different UUIDs populating the id field.
        assertNotEquals(rawFeedbackA.getComment(), rawFeedbackB.getComment());
    }

    @Test
    void repository_returnEmptyList_findMemberByFeedbackId() throws Exception {
        List<Feedback> recalls = feedbackRepository.findByMemberId("NotAMember");

        assertEquals(0, recalls.size());
    }
}
