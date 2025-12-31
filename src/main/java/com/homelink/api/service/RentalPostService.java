package com.homelink.api.service;

import com.homelink.api.dto.request.RentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import java.util.List;

public interface RentalPostService {
    RentalPostResponse save(RentalPostRequest request);
    List<RentalPostResponse> findAll();
    RentalPostResponse findById(Long id);
    RentalPostResponse update(Long id, RentalPostRequest request);
    void delete(Long id);
}