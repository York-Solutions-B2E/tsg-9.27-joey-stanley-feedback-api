package com.joey.stanley.group.project.feedback_api.dtos;

import com.joey.stanley.group.project.feedback_api.entity.Feedback;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
public class FeedbackSubmittedEvent {
    private String id;
    private String memberId;
    private String providerName;
    private int rating;
    private String comment;
    private Instant submittedAt;
    private int schemaVersion = 1;

    public static FeedbackSubmittedEvent fromEntityToEvent(Feedback feedback) {
        FeedbackSubmittedEvent event = new FeedbackSubmittedEvent();
        event.setId(feedback.getId().toString());
        event.setMemberId(feedback.getMemberId());
        event.setProviderName(feedback.getProviderName());
        event.setRating(feedback.getRating());
        event.setComment(feedback.getComment());
        event.setSubmittedAt(feedback.getSubmittedAt());
        event.setSchemaVersion(1);
        return event;
    }
}
