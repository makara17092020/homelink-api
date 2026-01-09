package com.homelink.api.service.impl;

import com.homelink.api.dto.request.CreateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import com.homelink.api.dto.response.ReviewResponse;
import com.homelink.api.entity.PropertyImage;
import com.homelink.api.entity.RentalPost;
import com.homelink.api.entity.User;
import com.homelink.api.repository.RentalPostRepository;
import com.homelink.api.repository.ReviewRepository;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.RentalPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RentalPostServiceImpl implements RentalPostService {

    private final RentalPostRepository rentalPostRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public RentalPostResponse createPost(CreateRentalPostRequest request, String username) {
        // 1. Find User (Agent)
        User agent = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Build Entity (Mapping from Request)
        RentalPost post = RentalPost.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .price(request.getPrice())
                // Ensure these are mapped correctly from the Request DTO
                .electricityCost(request.getElectricityCost()) 
                .waterCost(request.getWaterCost())
                .agent(agent)
                .active(true)
                .images(new ArrayList<>())
                .build();

        // 3. Map Images if present
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                PropertyImage img = PropertyImage.builder()
                        .url(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .rentalPost(post)
                        .build();
                post.getImages().add(img);
            }
        }

        // 4. Save to Database
        RentalPost savedPost = rentalPostRepository.save(post);

        // 5. Build Response (Mapping from the saved Entity)
        return RentalPostResponse.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .description(savedPost.getDescription())
                .address(savedPost.getAddress())
                .price(savedPost.getPrice())
                // Check if saved value is null, fallback to request if necessary
                .electricityCost(savedPost.getElectricityCost() != null ? savedPost.getElectricityCost() : request.getElectricityCost())
                .waterCost(savedPost.getWaterCost() != null ? savedPost.getWaterCost() : request.getWaterCost())
                .active(savedPost.getActive() != null ? savedPost.getActive() : true)
                .agentName(agent.getFullName() != null ? agent.getFullName() : agent.getUsername())
                .imageUrls(savedPost.getImages().stream().map(PropertyImage::getUrl).toList())
                .createdAt(savedPost.getCreatedAt())
                .averageRating(0.0)
                .totalRatings(0)
                .reviews(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalPostResponse> getAllPosts() {
        List<RentalPost> posts = rentalPostRepository.findAll();
        
        return posts.stream().map(post -> {
            // Fetch Statistics
            Double averageRating = reviewRepository.getAverageRatingByRentalPostId(post.getId());
            
            // Map Reviews to ReviewResponse DTOs
            List<ReviewResponse> reviewDTOs = reviewRepository.findByRentalPostId(post.getId())
                .stream()
                .map(review -> ReviewResponse.builder()
                    .id(review.getId())
                    .rating(review.getRating())
                    .comment(review.getComment())
                    .createdAt(review.getCreatedAt())
                    .userName(review.getUser().getFullName() != null ? 
                              review.getUser().getFullName() : review.getUser().getUsername())
                    .rentalPostId(post.getId())
                    .build())
                .toList();

            // Build Response for each post
            return RentalPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .description(post.getDescription())
                    .address(post.getAddress())
                    .price(post.getPrice())
                    .electricityCost(post.getElectricityCost())
                    .waterCost(post.getWaterCost())
                    .active(post.getActive() != null ? post.getActive() : true)
                    .agentName(post.getAgent().getFullName() != null ? 
                               post.getAgent().getFullName() : post.getAgent().getUsername())
                    .imageUrls(post.getImages().stream().map(PropertyImage::getUrl).toList())
                    .createdAt(post.getCreatedAt())
                    .averageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0)
                    .totalRatings(reviewDTOs.size()) 
                    .reviews(reviewDTOs)
                    .build();
        }).toList();
    }
}