package com.joey.stanley.group.project.feedback_api.dtos;

import com.joey.stanley.group.project.feedback_api.services.ValidationException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String message;

    public static ErrorResponse from(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        return errorResponse;
    }

    public static ErrorResponse from(String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        return errorResponse;
    }
}
