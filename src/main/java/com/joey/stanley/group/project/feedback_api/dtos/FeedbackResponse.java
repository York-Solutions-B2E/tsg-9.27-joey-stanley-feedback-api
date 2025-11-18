package com.joey.stanley.group.project.feedback_api.dtos;

import com.joey.stanley.group.project.feedback_api.entity.Feedback;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackResponse {
    private UUID id;
    private String memberId;
    private String providerName;
    private int rating;
    private String comment;
    private Instant submittedAt;

    public static FeedbackResponse from(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setMemberId(feedback.getMemberId());
        response.setProviderName(feedback.getProviderName());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComment());
        response.setSubmittedAt(feedback.getSubmittedAt());
        return response;
    }
}
