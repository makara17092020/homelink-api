package com.homelink.api.controller;

import com.homelink.api.dto.request.CreateReviewRequest;
import com.homelink.api.dto.response.ReviewResponse;
import com.homelink.api.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Endpoint: POST /api/reviews/1
    @PostMapping("/{rentalPostId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long rentalPostId,
            @Valid @RequestBody CreateReviewRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // This call now matches the updated Service Interface
        ReviewResponse response = reviewService.createReview(rentalPostId, request, username);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}