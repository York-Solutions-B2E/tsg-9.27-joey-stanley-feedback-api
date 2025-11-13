package com.joey.stanley.group.project.feedback_api.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    private static Feedback createRawFeedback() {
        Feedback rawFeedback = new Feedback();
        rawFeedback.setMemberId("m-2048");
        rawFeedback.setProviderName("Dr. Unlicensed");
        rawFeedback.setRating(5);
        rawFeedback.setComment("Apparently not allowed to practice medicine, but still provided diabetic meds for me!");
        return rawFeedback;
    }
    
    @Test
    void saveSuccessTest() throws Exception {
        Feedback rawFeedback = createRawFeedback();

        Feedback cookedFeedback = feedbackRepository.save(rawFeedback);

        assertNotNull(cookedFeedback);
        assertNotNull(cookedFeedback.getId());
        assertNotNull(cookedFeedback.getSubmittedAt());
    }
}
