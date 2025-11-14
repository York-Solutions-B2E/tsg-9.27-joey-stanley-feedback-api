package com.joey.stanley.group.project.feedback_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import com.joey.stanley.group.project.feedback_api.dtos.FeedbackRequest;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackResponse;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackSubmittedEvent;
import com.joey.stanley.group.project.feedback_api.entity.Feedback;
import com.joey.stanley.group.project.feedback_api.messaging.FeedbackEventPublisher;
import com.joey.stanley.group.project.feedback_api.repository.FeedbackRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class FeedbackServiceTest {

    @MockBean
    private FeedbackRepository feedbackRepository;

    @MockBean
    private FeedbackEventPublisher feedbackEventPublisher;

    @Autowired
    private FeedbackService feedbackService;

    private static String MOCK_MEMBER_ID = "m-1337";
    private static String MOCK_PROVIDER_NAME = "Totally Real Doctor";
    private static int MOCK_RATING = 3;
    private static String MOCK_COMMENT = "Nice guy, but I'm suspicious about his license...";

    @Test
    void createFeedbackSuccessTest() throws Exception {
        FeedbackRequest validRequest = new FeedbackRequest();
        validRequest.setMemberId(MOCK_MEMBER_ID);
        validRequest.setProviderName(MOCK_PROVIDER_NAME);
        validRequest.setRating(MOCK_RATING);
        validRequest.setComment(MOCK_COMMENT);

        Feedback expectedFeedback = validRequest.toEntity();
        expectedFeedback.setId(UUID.randomUUID());
        expectedFeedback.setSubmittedAt(OffsetDateTime.now());

        when(feedbackRepository.saveAndFlush(any(Feedback.class)))
            .thenReturn(expectedFeedback);

        Feedback feedback = feedbackService.createFeedback(validRequest);

        verify(feedbackRepository).saveAndFlush(any(Feedback.class));
        verify(feedbackEventPublisher).publishFeedbackEvent(any(FeedbackSubmittedEvent.class));

        assertNotNull(feedback, "Feedback after save is null");
        assertNotNull(feedback.getId(), "Feedback ID after save is null");
        assertEquals(MOCK_MEMBER_ID, feedback.getMemberId(), "memberId mismatch after save");
        assertEquals(MOCK_PROVIDER_NAME, feedback.getProviderName(), "providerName mismatch after save");
        assertEquals(MOCK_RATING, feedback.getRating(), "rating mismatch after save");
        assertEquals(MOCK_COMMENT, feedback.getComment(), "comment mismatch after save");
        assertNotNull(feedback.getSubmittedAt(), "Submission time is null");
    }

    private static void assertStartsWith(String msg, String head) throws Exception {
        String errMsg = "Wrong error message thrown; expected to start with: " + head;
        assertNotNull(msg, errMsg);
        assertTrue(msg.length() >= head.length(), errMsg);

        String foundHead = msg.substring(0, head.length());

        assertEquals(head, foundHead, errMsg);
    }

    private static void assertFieldIsAddressed(ValidationException ex, String fieldName) throws Exception {
        assertStartsWith(ex.getMessage(), "Field '" + fieldName + "' ");
    }

    // Creates a non-whitespace junk string of some length
    private static String createJunk(int length) {
        String junk = "";
        for (int i = 0; i < length; i++) {
            junk = junk.concat("A");
        }
        return junk;
    }

    private void testFieldException(FeedbackRequest invalidRequest, String fieldName) throws Exception {
        try {
            Feedback feedback = feedbackService.createFeedback(invalidRequest);
            fail("This should have thrown a ValidationException");
        } catch (ValidationException ex) {
            assertFieldIsAddressed(ex, fieldName);
        }
    }

    @Test
    void createFeedbackNullMemberFailureTest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(null);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        testFieldException(invalidRequest, "memberId");
    }

    @Test
    void createFeedbackLongMemberFailureTest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(createJunk(37));
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        testFieldException(invalidRequest, "memberId");
    }

    @Test
    void createFeedbackNullProviderFailureTest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(null);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        testFieldException(invalidRequest, "providerName");
    }

    @Test
    void createFeedbackLongProviderFailureTest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(createJunk(81));
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        testFieldException(invalidRequest, "providerName");
    }

    @Test
    void createFeedbackLowRatingFailureTest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(0);
        invalidRequest.setComment(MOCK_COMMENT);

        testFieldException(invalidRequest, "rating");
    }

    @Test
    void createFeedbackHighRatingFailureTest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(6);
        invalidRequest.setComment(MOCK_COMMENT);

        testFieldException(invalidRequest, "rating");
    }

    @Test
    void createFeedbackLongCommentFailureTest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(createJunk(201));

        testFieldException(invalidRequest, "comment");
    }

    @Test
    void findFeedbackByIdSuccessTest() throws Exception {
        Feedback expectedFeedback = new Feedback();
        expectedFeedback.setId(UUID.randomUUID());
        expectedFeedback.setMemberId(MOCK_MEMBER_ID);
        expectedFeedback.setProviderName(MOCK_PROVIDER_NAME);
        expectedFeedback.setRating(MOCK_RATING);
        expectedFeedback.setComment(MOCK_COMMENT);
        expectedFeedback.setSubmittedAt(OffsetDateTime.now());

        when(feedbackRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(expectedFeedback));

        Optional<FeedbackResponse> feedback = feedbackService.findFeedbackById(expectedFeedback.getId());

        assertTrue(feedback.isPresent());
        verify(feedbackRepository).findById(any(UUID.class));
    }

    @Test
    void findFeedbackByIdFailureTest() throws Exception {
        when(feedbackRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        Optional<FeedbackResponse> feedback = feedbackService.findFeedbackById(UUID.randomUUID());

        assertTrue(!feedback.isPresent());
        verify(feedbackRepository).findById(any(UUID.class));
    }

    @Test
    void findFeedbackByMemberIdNonZeroSuccessTest() throws Exception {
        //TODO: Implement a test which verifies a non-zero-length list of
        //      FeedbackResponse is returned, when a given memberId
        //      successfully pulls a non-zero-length list of Feedback items
        //      from the repository.
        //
        //      Remember that we are using saveAndFlush() instead of save()
        //
        // practice assignment for Stanley
    }

    @Test
    void findFeedbackByMemberIdEmptySuccessTest() throws Exception {
        //TODO: Implement a test which verifies an empty list is returned,
        //      when a given memberId pulls an empty list of Feedback items
        //      from the repository.
        //
        //      Remember that we are using saveAndFlush() instead of save()
        //
        // practice assignment for Stanley
    }
}
