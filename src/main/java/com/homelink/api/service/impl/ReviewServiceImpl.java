package com.homelink.api.service.impl;

import com.homelink.api.dto.request.CreateReviewRequest;
import com.homelink.api.dto.response.ReviewResponse;
import com.homelink.api.entity.RentalPost;
import com.homelink.api.entity.Review;
import com.homelink.api.entity.User;
import com.homelink.api.repository.RentalPostRepository;
import com.homelink.api.repository.ReviewRepository;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RentalPostRepository rentalPostRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(Long rentalPostId, CreateReviewRequest request, String username) {
        // 1. Find User
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Find Property by ID from URL
        RentalPost post = rentalPostRepository.findById(rentalPostId)
                .orElseThrow(() -> new RuntimeException("Rental property not found"));

        // 3. Validation: One review per user per property
        reviewRepository.findByUserIdAndRentalPostId(user.getId(), rentalPostId)
                .ifPresent(r -> {
                    throw new RuntimeException("You have already reviewed this property");
                });

        // 4. Save
        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .rentalPost(post)
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.builder()
                .id(savedReview.getId())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .userName(user.getFullName() != null ? user.getFullName() : user.getUsername())
                .rentalPostId(post.getId())
                .build();
    }
}