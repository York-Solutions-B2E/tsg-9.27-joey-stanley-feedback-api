package com.joey.stanley.group.project.feedback_api.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"feedback-submitted"})
public class FeedbackEventPublisherIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

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

        Map<String, Object> mockConsumerProps = KafkaTestUtils.consumerProps(
                                                                             "feedback-analytics-group",
                                                                             "false",
                                                                             embeddedKafka
                                                                             );

        DefaultKafkaConsumerFactory<String, FeedbackSubmittedEvent> mockFactory
            = new DefaultKafkaConsumerFactory<>(
                                                mockConsumerProps,
                                                new StringDeserializer(),
                                                new JsonDeserializer<>(FeedbackSubmittedEvent.class)
                                                );
        
        Consumer<String, FeedbackSubmittedEvent> mockConsumer = mockFactory.createConsumer();

        String topic = "feedback-submitted";

        mockConsumer.subscribe(Collections.singleton(topic));

        ConsumerRecord<String, FeedbackSubmittedEvent> record = KafkaTestUtils.getSingleRecord(
                                                                               mockConsumer,
                                                                               topic
                                                                               );

        FeedbackSubmittedEvent received = record.value();
        assertEquals(event.getId(), received.getId(), "Field 'id' field corrupted during transmission");
        assertEquals(event.getMemberId(), received.getMemberId(), "Field 'memberId' field corrupted during transmission");
        assertEquals(event.getProviderName(), received.getProviderName(), "Field 'providerName' field corrupted during transmission");
        assertEquals(event.getRating(), received.getRating(), "Field 'rating' field corrupted during transmission");
        assertEquals(event.getComment(), received.getComment(), "Field 'comment' field corrupted during transmission");
        assertEquals(event.getSubmittedAt(), received.getSubmittedAt());
    }
}
