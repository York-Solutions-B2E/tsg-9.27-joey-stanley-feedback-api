package com.joey.stanley.group.project.feedback_api.messaging;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackSubmittedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEventPublisher {

    private final KafkaTemplate<String, FeedbackSubmittedEvent> kafkaTemplate;
    private static final String TOPIC = "feedback-submitted";

    public FeedbackEventPublisher(KafkaTemplate<String, FeedbackSubmittedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishFeedbackEvent(FeedbackSubmittedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
