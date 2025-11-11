package com.joey.stanley.group.project.feedback_api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackResponse {

    private String providerName;
    private int rating;
    private String comment;
}
