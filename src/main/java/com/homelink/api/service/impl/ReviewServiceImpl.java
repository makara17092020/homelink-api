package com.homelink.api.service.impl;

import com.homelink.api.dto.request.CreateReviewRequest;
import com.homelink.api.dto.response.ReviewResponse;
import com.homelink.api.entity.Review;
import com.homelink.api.entity.RentalPost;
import com.homelink.api.entity.User;
import com.homelink.api.exception.BadRequestException;
import com.homelink.api.exception.ResourceNotFoundException;
import com.homelink.api.repository.RentalPostRepository;
import com.homelink.api.repository.ReviewRepository;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RentalPostRepository rentalPostRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, String username) {
        // 1. Find User
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Find Rental Post
        RentalPost rentalPost = rentalPostRepository.findById(request.getRentalPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental post not found"));

        // 3. Check if user already reviewed this post
        reviewRepository.findByUserIdAndRentalPostId(user.getId(), rentalPost.getId())
                .ifPresent(review -> {
                    throw new BadRequestException("You have already reviewed this property");
                });

        // 4. Build Review Entity
        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .rentalPost(rentalPost)
                .build();

        // 5. Save Review
        Review savedReview = reviewRepository.save(review);

        // 6. Build Response
        return ReviewResponse.builder()
                .id(savedReview.getId())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .userName(user.getFullName() != null ? user.getFullName() : user.getUsername())
                .rentalPostId(savedReview.getRentalPost().getId())
                .build();
    }
}