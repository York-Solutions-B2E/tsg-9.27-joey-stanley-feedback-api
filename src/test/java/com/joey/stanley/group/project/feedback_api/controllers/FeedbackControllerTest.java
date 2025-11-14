package com.joey.stanley.group.project.feedback_api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackRequest;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackResponse;
import com.joey.stanley.group.project.feedback_api.entity.Feedback;
import com.joey.stanley.group.project.feedback_api.services.FeedbackService;
import com.joey.stanley.group.project.feedback_api.services.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(value = FeedbackController.class)
public class FeedbackControllerTest {

    // Technically deprecated, but I can't find docs for a working
    // arrangement which simply uses `@Mock` instead.
    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private MockMvc mockMvc;

    private static String asJsonString(Object obj) throws JsonProcessingException {
        return (new ObjectMapper()).writeValueAsString(obj);
    }

    private static String API_ROOT = "/api/v1/feedback";
    private static String API_FEEDBACK_PARAM = "/{feedbackId}";
    private static String API_MEMBER_PARAM = "?memberId=";
    
    private static String FEEDBACK_ID_PATH = "$.id";
    private static String MEMBER_ID_PATH = "$.memberId";
    private static String PROVIDER_NAME_PATH = "$.providerName";
    private static String RATING_PATH = "$.rating";
    private static String COMMENT_PATH = "$.comment";
    
    private static String MOCK_MEMBER_ID = "m-1337";
    private static String MOCK_PROVIDER_NAME = "Totally Real Doctor";
    private static int MOCK_RATING = 3;
    private static String MOCK_COMMENT = "Nice guy, but I'm suspicious about his license...";

    private static FeedbackRequest createValidFeedbackRequest() {
        FeedbackRequest request = new FeedbackRequest();
        request.setMemberId(MOCK_MEMBER_ID);
        request.setProviderName(MOCK_PROVIDER_NAME);
        request.setRating(MOCK_RATING);
        request.setComment(MOCK_COMMENT);
        return request;
    }
    
    private static Feedback createValidFeedback() {
        Feedback mockFeedback = new Feedback();
        mockFeedback.setId(UUID.randomUUID());
        mockFeedback.setMemberId(MOCK_MEMBER_ID);
        mockFeedback.setProviderName(MOCK_PROVIDER_NAME);
        mockFeedback.setRating(MOCK_RATING);
        mockFeedback.setComment(MOCK_COMMENT);
        mockFeedback.setSubmittedAt(Instant.now());
        return mockFeedback;
    }
    
    private static FeedbackResponse createValidResponse() {
        Feedback mockFeedback = createValidFeedback();
        FeedbackResponse response = FeedbackResponse.from(mockFeedback);
        return response;
    }

    @Test
    void testCreateNewFeedbackSuccess() throws Exception {
        FeedbackRequest validRequest = createValidFeedbackRequest();
        Feedback validFeedback = createValidFeedback();
        
        when(feedbackService.createFeedback(any(FeedbackRequest.class)))
                .thenReturn(validFeedback);

        MvcResult mvcResult = mockMvc.perform(post(API_ROOT)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(asJsonString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath(MEMBER_ID_PATH, is(MOCK_MEMBER_ID)))
            .andExpect(jsonPath(PROVIDER_NAME_PATH, is(MOCK_PROVIDER_NAME)))
            .andExpect(jsonPath(RATING_PATH, is(MOCK_RATING)))
            .andExpect(jsonPath(COMMENT_PATH, is(MOCK_COMMENT)))
            .andReturn();

        verify(feedbackService, times(1)).createFeedback(any(FeedbackRequest.class));
    }

    @Test
    void testCreateNewFeedbackFailure() throws Exception {
        FeedbackRequest validRequest = createValidFeedbackRequest();
        Feedback validFeedback = createValidFeedback();
        
        when(feedbackService.createFeedback(any(FeedbackRequest.class)))
                .thenThrow(new ValidationException("Who do you think you are, with your knees halfway up your legs like that?"));

        MvcResult mvcResult = mockMvc.perform(post(API_ROOT)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(asJsonString(validRequest)))
            .andExpect(status().isBadRequest())
            .andReturn();

        verify(feedbackService, times(1)).createFeedback(any(FeedbackRequest.class));
    }

    @Test
    void testFindFeedbackByIdSuccess() throws Exception {
        Feedback validFeedback = createValidFeedback();
        UUID validUUID = UUID.randomUUID();
        validFeedback.setId(validUUID);
        FeedbackResponse validResponse = FeedbackResponse.from(validFeedback);

        when(feedbackService.findFeedbackById(any(UUID.class)))
            .thenReturn(Optional.of(validResponse));

        MvcResult mvcResult = mockMvc.perform(get(API_ROOT + API_FEEDBACK_PARAM, validUUID.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath(FEEDBACK_ID_PATH).doesNotExist())
            .andExpect(jsonPath(MEMBER_ID_PATH).doesNotExist())
            .andExpect(jsonPath(PROVIDER_NAME_PATH, is(MOCK_PROVIDER_NAME)))
            .andExpect(jsonPath(RATING_PATH, is(MOCK_RATING)))
            .andExpect(jsonPath(COMMENT_PATH, is(MOCK_COMMENT)))
            .andReturn();

        verify(feedbackService, times(1)).findFeedbackById(validUUID);
    }

    @Test
    void testFindFeedbackByIdFailure() throws Exception {
        UUID validUUID = UUID.randomUUID();

        when(feedbackService.findFeedbackById(any(UUID.class)))
            .thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(get(API_ROOT + API_FEEDBACK_PARAM, validUUID.toString()))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(feedbackService, times(1)).findFeedbackById(validUUID);
    }
    
    @Test
    void testFindFeedbackByMemberIdSuccess() throws Exception {
        Feedback validFeedback = createValidFeedback();
        FeedbackResponse validResponse = FeedbackResponse.from(validFeedback);
        List<FeedbackResponse> responseList = new ArrayList<>();
        responseList.add(validResponse);

        when(feedbackService.findFeedbackByMemberId(any(String.class)))
            .thenReturn(responseList);

        MvcResult mvcResult = mockMvc.perform(get(API_ROOT + API_MEMBER_PARAM + MOCK_MEMBER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andReturn();

        verify(feedbackService, times(1)).findFeedbackByMemberId(MOCK_MEMBER_ID);
    }
}
