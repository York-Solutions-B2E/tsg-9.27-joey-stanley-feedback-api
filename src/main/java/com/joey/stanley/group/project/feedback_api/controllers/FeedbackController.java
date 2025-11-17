package com.joey.stanley.group.project.feedback_api.controllers;

import com.joey.stanley.group.project.feedback_api.dtos.ErrorResponse;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackRequest;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackResponse;
import com.joey.stanley.group.project.feedback_api.entity.Feedback;
import com.joey.stanley.group.project.feedback_api.services.FeedbackService;
import com.joey.stanley.group.project.feedback_api.services.ValidationException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {
        "http://react-frontend:80",
        "http://localhost:80",
        "http://react-frontend:5173",
        "http://localhost:5173"
    })
public class FeedbackController {

    private FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping(value="/feedback")
    public ResponseEntity<Object> createNewFeedback(@RequestBody FeedbackRequest entity) {
        try {
            System.out.println("endpoint hit");
            Feedback created = feedbackService.createFeedback(entity);
            URI location = URI.create("/api/v1/feedback/" + created.getId().toString());
            return ResponseEntity.created(location).body(created);
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(ErrorResponse.from(ex));
        }
    }

    @GetMapping(value="/feedback/{feedbackId}")
    public ResponseEntity<Object> findFeedbackById(@PathVariable UUID feedbackId) {
        Optional<FeedbackResponse> response = feedbackService.findFeedbackById(feedbackId);

        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value="/feedback")
    public ResponseEntity<List<FeedbackResponse>> findFeedbackByMemberId(@RequestParam String memberId) {
        List<FeedbackResponse> responses = feedbackService.findFeedbackByMemberId(memberId);
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping(value="/health")
    public ResponseEntity<String> getServiceHealth() {
        // Do not report an online service while testing
        if (System.getProperty("spring.test") != null) {
            return ResponseEntity.internalServerError().body("down");
        }
        return ResponseEntity.ok("up");
    }
}
