package com.joey.stanley.group.project.feedback_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class FeedbackServiceTest {

    @MockitoBean
    private FeedbackRepository feedbackRepository;

    @MockitoBean
    private FeedbackEventPublisher feedbackEventPublisher;

    @Autowired
    private FeedbackService feedbackService;

    private static String MOCK_MEMBER_ID = "m-1337";
    private static String MOCK_PROVIDER_NAME = "Totally Real Doctor";
    private static int MOCK_RATING = 3;
    private static String MOCK_COMMENT = "Nice guy, but I'm suspicious about his license...";

    //Save to Repo, Publish an event, & return saved feedback entity
    @Test
    void createFeedback_returnsFeedbackResponse_whenRequestIsValid() throws Exception {
        //build valid request
        FeedbackRequest validRequest = new FeedbackRequest();
        validRequest.setMemberId(MOCK_MEMBER_ID);
        validRequest.setProviderName(MOCK_PROVIDER_NAME);
        validRequest.setRating(MOCK_RATING);
        validRequest.setComment(MOCK_COMMENT);

        //build object DB should return
        Feedback expectedFeedback = validRequest.toEntity();
        expectedFeedback.setId(UUID.randomUUID());
        expectedFeedback.setSubmittedAt(Instant.now());

        //Stub repo. "If service calls saveAndFlush with ANY feedback obj, then return expectedFeedback"
        when(feedbackRepository.saveAndFlush(any(Feedback.class)))
            .thenReturn(expectedFeedback);

        //Call actual service method we're testing
        FeedbackResponse feedbackResponse = feedbackService.createFeedback(validRequest);
        //Feedback feedback = feedbackService.createFeedback(validRequest);

        //Verify in logs that these methods were actually called
        verify(feedbackRepository).saveAndFlush(any(Feedback.class));
        verify(feedbackEventPublisher).publishFeedbackEvent(any(FeedbackSubmittedEvent.class));

        //Confirming feedback object matches what we expect
        assertNotNull(feedbackResponse, "Feedback after save is null");
        assertNotNull(feedbackResponse.getId(), "Feedback ID after save is null");
        assertEquals(MOCK_MEMBER_ID, feedbackResponse.getMemberId(), "memberId mismatch after save");
        assertEquals(MOCK_PROVIDER_NAME, feedbackResponse.getProviderName(), "providerName mismatch after save");
        assertEquals(MOCK_RATING, feedbackResponse.getRating(), "rating mismatch after save");
        assertEquals(MOCK_COMMENT, feedbackResponse.getComment(), "comment mismatch after save");
        assertNotNull(feedbackResponse.getSubmittedAt(), "Submission time is null");
    }

    // Creates a non-whitespace junk string of some length
    private static String createJunk(int length) {
        String junk = "";
        for (int i = 0; i < length; i++) {
            junk = junk.concat("A");
        }
        return junk;
    }

    private void assertValidationException(FeedbackRequest feedbackRequest, String fieldName) {
        try {
            feedbackService.createFeedback(feedbackRequest);
            fail("Expected ValidationException to be thrown");
        } catch (ValidationException ex) {
            assertNotNull(ex.getMessage(), "Validation Exception is null");
            assertTrue(
                    ex.getMessage().startsWith("Field '" + fieldName + "' "),
                    "Wrong error message thrown; expected to start with: Field '" + fieldName + "' "
            );
        }
    }

    @Test
    void createFeedback_throwsValidationException_whenMemberIdIsNull() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(null);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        assertValidationException(invalidRequest, "memberId");
    }

    @Test
    void createFeedback_throwsValidationException_whenMemberIdExceedsMaxLength() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(createJunk(37));
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        assertValidationException(invalidRequest, "memberId");
    }

    @Test
    void createFeedback_throwsValidationException_whenProviderNameIsNull() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(null);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        assertValidationException(invalidRequest, "providerName");
    }

    @Test
    void createFeedback_throwsValidationException_whenProviderNameExceedsMaxLength() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(createJunk(81));
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(MOCK_COMMENT);

        assertValidationException(invalidRequest, "providerName");
    }

    @Test
    void createFeedback_throwsValidationException_whenRatingIsBelowMin() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(0);
        invalidRequest.setComment(MOCK_COMMENT);

        assertValidationException(invalidRequest, "rating");
    }

    @Test
    void createFeedback_throwsValidationException_whenRatingIsAboveMax() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(6);
        invalidRequest.setComment(MOCK_COMMENT);

        assertValidationException(invalidRequest, "rating");
    }

    @Test
    void createFeedback_throwsValidationException_whenCommentExceedsMaxLength() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest();
        invalidRequest.setMemberId(MOCK_MEMBER_ID);
        invalidRequest.setProviderName(MOCK_PROVIDER_NAME);
        invalidRequest.setRating(MOCK_RATING);
        invalidRequest.setComment(createJunk(201));

        assertValidationException(invalidRequest, "comment");
    }

    @Test
    void findFeedbackById_returnsFeedbackResponse_whenFeedbackExists() throws Exception {
        Feedback expectedFeedback = new Feedback();
        expectedFeedback.setId(UUID.randomUUID());
        expectedFeedback.setMemberId(MOCK_MEMBER_ID);
        expectedFeedback.setProviderName(MOCK_PROVIDER_NAME);
        expectedFeedback.setRating(MOCK_RATING);
        expectedFeedback.setComment(MOCK_COMMENT);
        expectedFeedback.setSubmittedAt(Instant.now());

        when(feedbackRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(expectedFeedback));

        Optional<FeedbackResponse> feedback = feedbackService.findFeedbackById(expectedFeedback.getId());

        assertTrue(feedback.isPresent());
        verify(feedbackRepository).findById(any(UUID.class));
    }

    @Test
    void findFeedbackById_returnsEmptyOptional_whenFeedbackDoesNotExist() throws Exception {
        when(feedbackRepository.findById(any(UUID.class)))
            .thenReturn(Optional.empty());

        Optional<FeedbackResponse> feedback = feedbackService.findFeedbackById(UUID.randomUUID());

        assertTrue(!feedback.isPresent());
        verify(feedbackRepository).findById(any(UUID.class));
    }

    @Test
    void findFeedbackByMemberId_returnsNonEmptyList_whenFeedbackExistsForMember() throws Exception {
        //Arrange (Create mock feedback that would be coming from DB)
        Feedback mockFeedback = new Feedback();
        mockFeedback.setId(UUID.randomUUID());
        mockFeedback.setMemberId(MOCK_MEMBER_ID);
        mockFeedback.setProviderName(MOCK_PROVIDER_NAME);
        mockFeedback.setRating(MOCK_RATING);
        mockFeedback.setComment(MOCK_COMMENT);
        mockFeedback.setSubmittedAt(Instant.now());

        //Arrange (Should return a non-empty list)
        when(feedbackRepository.findByMemberId(MOCK_MEMBER_ID))
                .thenReturn(List.of(mockFeedback));

        //Act (call service)
        List<FeedbackResponse> result = feedbackService.findFeedbackByMemberId(MOCK_MEMBER_ID);

        //Assert(Verify 1 result, and that it matches)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockFeedback.getRating(), result.get(0).getRating());
        assertEquals(mockFeedback.getProviderName(), result.get(0).getProviderName());
        assertEquals(mockFeedback.getComment(), result.get(0).getComment());

        verify(feedbackRepository).findByMemberId(MOCK_MEMBER_ID);
    }

    @Test
    void findFeedbackByMemberId_returnsEmptyList_whenNoFeedbackExistsForMember() throws Exception {
        //Arrange (empty list)
        when(feedbackRepository.findByMemberId(MOCK_MEMBER_ID))
                .thenReturn(List.of());

        //Act (call service)
        List<FeedbackResponse> result = feedbackService.findFeedbackByMemberId(MOCK_MEMBER_ID);

        //Assert (verify result is empty)
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(feedbackRepository).findByMemberId(MOCK_MEMBER_ID);
    }


}
