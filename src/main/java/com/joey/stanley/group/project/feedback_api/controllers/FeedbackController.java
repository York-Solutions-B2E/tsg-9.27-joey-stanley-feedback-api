package com.joey.stanley.group.project.feedback_api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = { "http://react-frontend:80", "http://localhost:80", "http://react-frontend:5173", "http://localhost:5173" })
public class FeedbackController {

    //

    public FeedbackController() {
        // TODO: Add service
    }

    @PostMapping(value="/feedback")
    public ResponseEntity<Void> postNewFeedback(@RequestBody SomeEnityData entity) {
        //TODO: process POST request
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //TODO: Send back DTO
    @GetMapping(value="/feedback/{id}")
    public ResponseEntity<Void> getFeedbackById(@PathVariable UUID feedbackId) {
        //TODO

        //TODO: Return 404 if not found
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //TODO: Send back DTO list
    @GetMapping(value="/feedback")
    public ResponseEntity<Void> getFeedbackByMemberId(@RequestParam String memberId) {
        //TODO
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value="/health")
    public SomeData getServiceHealth() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
