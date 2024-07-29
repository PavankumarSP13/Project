package com.mea.feedbackservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback-service")
public class FeedbackController {
    @GetMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request) {
        return new ResponseEntity<String>("Feedback Service is up!!!!",HttpStatus.OK);
    }
}
