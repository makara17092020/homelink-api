package com.homelink.api.service.impl;

import com.homelink.api.dto.request.CreateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import com.homelink.api.entity.PropertyImage;
import com.homelink.api.entity.RentalPost;
import com.homelink.api.entity.User;
import com.homelink.api.repository.RentalPostRepository;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.RentalPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalPostServiceImpl implements RentalPostService {

    private final RentalPostRepository rentalPostRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RentalPostResponse createPost(CreateRentalPostRequest request, String username) {
        User agent = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Build the entity
        RentalPost post = RentalPost.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .price(request.getPrice())
                .electricityCost(request.getElectricityCost()) // Must match Entity field
                .waterCost(request.getWaterCost())           // Must match Entity field
                .agent(agent)
                .images(new ArrayList<>())
                .build();

        // Add Images
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                PropertyImage image = PropertyImage.builder()
                        .url(request.getImageUrls().get(i))
                        .sortOrder(i)
                        .property(post)
                        .build();
                post.getImages().add(image);
            }
        }

        RentalPost savedPost = rentalPostRepository.save(post);

        return RentalPostResponse.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .description(savedPost.getDescription())
                .address(savedPost.getAddress())
                .price(savedPost.getPrice())
                .electricityCost(savedPost.getElectricityCost())
                .waterCost(savedPost.getWaterCost())
                .agentName(agent.getFullName() != null ? agent.getFullName() : agent.getUsername())
                .imageUrls(savedPost.getImages().stream().map(PropertyImage::getUrl).collect(Collectors.toList()))
                .createdAt(savedPost.getCreatedAt())
                .build();
    }
}