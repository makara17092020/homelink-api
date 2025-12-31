package com.homelink.api.service.impl;

import com.homelink.api.dto.request.RentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import com.homelink.api.entity.RentalPost;
import com.homelink.api.entity.User;
import com.homelink.api.repository.RentalPostRepository;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.RentalPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalPostServiceImpl implements RentalPostService {

    private final RentalPostRepository rentalPostRepository;
    private final UserRepository userRepository;

    @Override
    public RentalPostResponse save(RentalPostRequest request) {
        User agent = userRepository.findById(request.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        RentalPost post = new RentalPost();
        mapRequestToEntity(request, post);
        post.setAgent(agent);

        return mapToResponse(rentalPostRepository.save(post));
    }

    @Override
    public List<RentalPostResponse> findAll() {
        return rentalPostRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RentalPostResponse findById(Long id) {
        RentalPost post = rentalPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return mapToResponse(post);
    }

    @Override
    public RentalPostResponse update(Long id, RentalPostRequest request) {
        RentalPost post = rentalPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        mapRequestToEntity(request, post);
        post.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponse(rentalPostRepository.save(post));
    }

    @Override
    public void delete(Long id) {
        rentalPostRepository.deleteById(id);
    }

    private void mapRequestToEntity(RentalPostRequest request, RentalPost post) {
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setPrice(request.getPrice());
        post.setAddress(request.getAddress());
        post.setElectricityCost(request.getElectricityCost());
        post.setWaterCost(request.getWaterCost());
    }

    private RentalPostResponse mapToResponse(RentalPost post) {
        RentalPostResponse res = new RentalPostResponse();
        res.setId(post.getId());
        res.setTitle(post.getTitle());
        res.setDescription(post.getDescription());
        res.setPrice(post.getPrice());
        res.setAddress(post.getAddress());
        res.setElectricityCost(post.getElectricityCost());
        res.setWaterCost(post.getWaterCost());
        res.setCreatedAt(post.getCreatedAt());
        if (post.getAgent() != null) {
            res.setAgentName(post.getAgent().getFullName());
        }
        return res;
    }
}