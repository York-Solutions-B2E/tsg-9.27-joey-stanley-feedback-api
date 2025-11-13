package com.joey.stanley.group.project.feedback_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.joey.stanley.group.project.feedback_api.dtos.FeedbackRequest;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackResponse;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackSubmittedEvent;
import com.joey.stanley.group.project.feedback_api.entity.Feedback;
import com.joey.stanley.group.project.feedback_api.messaging.FeedbackEventPublisher;
import com.joey.stanley.group.project.feedback_api.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;


    private final FeedbackEventPublisher feedbackEventPublisher;

    // Constructor
    public FeedbackService(FeedbackRepository feedbackRepository,
                           FeedbackEventPublisher feedbackEventPublisher) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackEventPublisher = feedbackEventPublisher;
    }

    public Feedback createFeedback(FeedbackRequest request) throws ValidationException {
        //Validation
        if(request.getMemberId() == null || request.getMemberId().length() > 36){
            throw new ValidationException("Field 'memberId' must be ≤ 36 characters or not null");
        }
        if (request.getProviderName() == null || request.getProviderName().length() > 80) {
            throw new ValidationException("Field 'providerName' must be ≤ 80 characters or not null");
        }
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ValidationException("Field 'rating' must be an integer between 1 and 5");
        }
        if (request.getComment() != null && request.getComment().length() > 200) {
            throw new ValidationException("Field 'comment' must be ≤ 200 characters");
        }
        //Save to DB
        Feedback feedback = request.toEntity();
        Feedback savedFeedback = feedbackRepository.save(feedback);

        //Create event object & send to Kafka
        FeedbackSubmittedEvent event = FeedbackSubmittedEvent.fromEntityToEvent(savedFeedback);
        feedbackEventPublisher.publishFeedbackEvent(event);

        return savedFeedback;
    }


    public Optional<FeedbackResponse> findFeedbackById(UUID id) {
        return feedbackRepository.findById(id)
                .map(feedback -> FeedbackResponse.from(feedback));
    }

    public List<FeedbackResponse> findFeedbackByMemberId(String memberId) {
        return feedbackRepository.findByMemberId(memberId)
                .stream()
                .map(feedback -> FeedbackResponse.from(feedback))
                .toList();
    }
}
