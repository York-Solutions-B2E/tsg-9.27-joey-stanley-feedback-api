package com.joey.stanley.group.project.feedback_api.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackSubmittedEvent;
import com.joey.stanley.group.project.feedback_api.entity.Feedback;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class FeedbackEventPublisherTest {

    @MockitoBean
    private KafkaTemplate mockKafka;
    
    @Autowired
    private FeedbackEventPublisher publisher;

    @Test
    void kafkaPublishMessageSuccessTest() throws Exception {
        Feedback validFeedback = new Feedback();
        validFeedback.setId(UUID.randomUUID());
        validFeedback.setMemberId("m-6553");
        validFeedback.setProviderName("Definitely Real Doctor");
        validFeedback.setRating(5);
        validFeedback.setComment("Wow! A real doctor this time!");
        validFeedback.setSubmittedAt(Instant.now());
        
        FeedbackSubmittedEvent event = FeedbackSubmittedEvent.fromEntityToEvent(validFeedback);
        publisher.publishFeedbackEvent(event);

        verify(mockKafka).send(eq("feedback-submitted"), any(FeedbackSubmittedEvent.class));
    }
}
