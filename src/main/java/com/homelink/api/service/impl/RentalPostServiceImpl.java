package com.homelink.api.service.impl;

import com.homelink.api.dto.request.CreateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
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
        // 1. Find User
        User agent = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Build Entity (Explicitly setting active to true)
        RentalPost post = RentalPost.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .price(request.getPrice())
                .electricityCost(request.getElectricityCost())
                .waterCost(request.getWaterCost())
                .agent(agent)
                .active(true) // Ensure the 'active' column is not null
                .images(new ArrayList<>())
                .build();

        // 3. Map Images
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

        // 4. Save
        RentalPost savedPost = rentalPostRepository.save(post);

        // 5. Build Response
        return RentalPostResponse.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .description(savedPost.getDescription())
                .address(savedPost.getAddress())
                .price(savedPost.getPrice())
                .electricityCost(savedPost.getElectricityCost())
                .waterCost(savedPost.getWaterCost())
                .active(savedPost.getActive() != null ? savedPost.getActive() : true)
                .agentName(agent.getFullName() != null ? agent.getFullName() : agent.getUsername())
                .imageUrls(savedPost.getImages().stream().map(PropertyImage::getUrl).toList())
                .createdAt(savedPost.getCreatedAt())
                .build();
    }

    @Override
    public List<RentalPostResponse> getAllPosts() {
        List<RentalPost> posts = rentalPostRepository.findAll();
        return posts.stream().map(post -> {
            Double averageRating = reviewRepository.getAverageRatingByRentalPostId(post.getId());
            
            return RentalPostResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .description(post.getDescription())
                    .address(post.getAddress())
                    .price(post.getPrice())
                    .electricityCost(post.getElectricityCost())
                    .waterCost(post.getWaterCost())
                    .active(post.getActive() != null ? post.getActive() : true) // Safe mapping
                    .agentName(post.getAgent().getFullName() != null ? post.getAgent().getFullName() : post.getAgent().getUsername())
                    .imageUrls(post.getImages().stream().map(PropertyImage::getUrl).toList())
                    .createdAt(post.getCreatedAt())
                    .averageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null)
                    .build();
        }).toList();
    }
}