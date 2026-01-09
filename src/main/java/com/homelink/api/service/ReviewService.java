package com.homelink.api.service;

import com.homelink.api.dto.request.CreateReviewRequest;
import com.homelink.api.dto.response.ReviewResponse;

public interface ReviewService {
    // Update this to accept the rentalPostId as the first argument
    ReviewResponse createReview(Long rentalPostId, CreateReviewRequest request, String username);
}