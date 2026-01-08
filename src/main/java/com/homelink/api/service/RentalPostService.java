package com.homelink.api.service;

import com.homelink.api.dto.request.CreateRentalPostRequest;
import com.homelink.api.dto.request.UpdateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;

public interface RentalPostService {
    RentalPostResponse createPost(CreateRentalPostRequest request, String username);

    RentalPostResponse updatePost(Long id, UpdateRentalPostRequest request, String username);
}