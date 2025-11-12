package com.joey.stanley.group.project.feedback_api.dtos;

import com.joey.stanley.group.project.feedback_api.entity.Feedback;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackRequest {

    private String memberId;
    private String providerName;
    private int rating;
    private String comment;

    public Feedback toEntity() {
        Feedback feedback = new Feedback();
        feedback.setMemberId(this.getMemberId());
        feedback.setProviderName(this.getProviderName());
        feedback.setRating(this.getRating());
        feedback.setComment(this.getComment());
        return feedback;
    }
}
