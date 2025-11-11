package com.joey.stanley.group.project.feedback_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.joey.stanley.group.project.feedback_api.dtos.FeedbackRequest;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackResponse;
import com.joey.stanley.group.project.feedback_api.entity.Feedback;
import com.joey.stanley.group.project.feedback_api.services.ValidationException;

import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    //

    public FeedbackService() {
        // Constructor
    }

    public Feedback createFeedback(FeedbackRequest request) throws ValidationException {
        throw new ValidationException("Not implemented lol"); //TODO
    }

    public Optional<FeedbackResponse> findFeedbackById(UUID id) {
        return Optional.empty(); //TODO
    }

    public List<FeedbackResponse> findFeedbackByMemberId(String memberId) {
        return new ArrayList<>();
    }
}
