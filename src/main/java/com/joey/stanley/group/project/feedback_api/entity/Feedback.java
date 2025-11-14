package com.joey.stanley.group.project.feedback_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name="feedback")
@Check(constraints = "rating BETWEEN 1 AND 5")
public class Feedback {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 36)
    private String memberId;

    @Column(nullable = false, length = 80)
    private String providerName;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 200)
    private String comment;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant submittedAt;
}
