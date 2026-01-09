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
import java.util.stream.Collectors;

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

        // 2. Build Parent Entity (RentalPost)
        RentalPost post = RentalPost.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .price(request.getPrice())
                .electricityCost(request.getElectricityCost()) 
                .waterCost(request.getWaterCost())
                .agent(agent)
                .active(true)
                .images(new ArrayList<>())
                .build();

        // 3. Map Child Entities (PropertyImages) and link to Parent
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                PropertyImage img = PropertyImage.builder()
                        .url(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .build();
                // Use the helper method we just added to the Entity
                post.addImage(img); 
            }
        }

        // 4. Save to Database (Ensure CascadeType.ALL is on the images list in RentalPost entity)
        RentalPost savedPost = rentalPostRepository.save(post);

        // 5. Build Response
        return mapToResponse(savedPost, 0.0, 0, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalPostResponse> getAllPosts() {
        List<RentalPost> posts = rentalPostRepository.findAll();
        
        return posts.stream().map(post -> {
            // Fetch Review Statistics
            Double averageRating = reviewRepository.getAverageRatingByRentalPostId(post.getId());
            
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

            double roundedAvg = (averageRating != null) ? Math.round(averageRating * 10.0) / 10.0 : 0.0;
            
            return mapToResponse(post, roundedAvg, reviewDTOs.size(), reviewDTOs);
        }).toList();
    }

    // Helper method to keep code clean
    private RentalPostResponse mapToResponse(RentalPost post, Double avg, Integer total, List<ReviewResponse> reviews) {
        return RentalPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .address(post.getAddress())
                .price(post.getPrice())
                .electricityCost(post.getElectricityCost())
                .waterCost(post.getWaterCost())
                .active(post.getActive() != null ? post.getActive() : true)
                .agentName(post.getAgent().getFullName() != null ? post.getAgent().getFullName() : post.getAgent().getUsername())
                .imageUrls(post.getImages().stream().map(PropertyImage::getUrl).collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .averageRating(avg)
                .totalRatings(total)
                .reviews(reviews)
                .build();
    }
}