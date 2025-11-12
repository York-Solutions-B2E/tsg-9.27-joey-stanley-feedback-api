package com.joey.stanley.group.project.feedback_api.repository;

import com.joey.stanley.group.project.feedback_api.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByMemberId(String memberId);
}
