package com.joey.stanley.group.project.feedback_api.dtos;

import com.joey.stanley.group.project.feedback_api.entity.Feedback;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackResponse {

    private String providerName;
    private int rating;
    private String comment;

    public static FeedbackResponse from(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setProviderName(feedback.getProviderName());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComment());
        return response;
    }
}
