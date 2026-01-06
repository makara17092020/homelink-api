package com.homelink.api.service;

import com.homelink.api.dto.request.CreateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import java.util.List;

public interface RentalPostService {
    RentalPostResponse createPost(CreateRentalPostRequest request, String username);
    
    List<RentalPostResponse> getAllPosts();
}