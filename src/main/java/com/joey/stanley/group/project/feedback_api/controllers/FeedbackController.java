package com.joey.stanley.group.project.feedback_api.controllers;

import com.joey.stanley.group.project.feedback_api.dtos.ErrorResponse;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackRequest;
import com.joey.stanley.group.project.feedback_api.dtos.FeedbackResponse;
import com.joey.stanley.group.project.feedback_api.services.FeedbackService;
import com.joey.stanley.group.project.feedback_api.services.ValidationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Operation(
            summary = "Create new feedback",
            description = "Creates a feedback entry for a member, and returns the created feedback object and response."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Feedback successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request: validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping(value="/feedback")
    public ResponseEntity<Object> createNewFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        try {
            FeedbackResponse createdFeedback = feedbackService.createFeedback(feedbackRequest);
            URI location = URI.create("/api/v1/feedback/" + createdFeedback.getId().toString());
            return ResponseEntity.created(location).body(createdFeedback);
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(ErrorResponse.from(ex));
        }
    }

    @Operation(
            summary = "Get feedback by ID",
            description = "Fetch a single feedback entry using its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FeedbackResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Feedback not found"
            )
    })
    @GetMapping(value="/feedback/{feedbackId}")
    public ResponseEntity<FeedbackResponse> findFeedbackById(@PathVariable UUID feedbackId) {
        Optional<FeedbackResponse> response = feedbackService.findFeedbackById(feedbackId);

        if (response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Get feedback by member ID",
            description = "Returns a list of all feedback entries submitted by the specified memberId."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of feedback entries",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = FeedbackResponse.class)
                            )
                    )
            )
    })
    @GetMapping(value="/feedback")
    public ResponseEntity<List<FeedbackResponse>> findFeedbackByMemberId(@RequestParam String memberId) {
        List<FeedbackResponse> responses = feedbackService.findFeedbackByMemberId(memberId);
        
        return ResponseEntity.ok(responses);
    }

}
