package com.homelink.api.service;

import com.homelink.api.dto.request.CreateReviewRequest;
import com.homelink.api.dto.response.ReviewResponse;

public interface ReviewService {

    ReviewResponse createReview(CreateReviewRequest request, String username);
}