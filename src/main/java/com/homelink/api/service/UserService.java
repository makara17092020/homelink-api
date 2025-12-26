package com.homelink.api.service;

import com.homelink.api.dto.request.CreateUserRequest;
import com.homelink.api.dto.request.UpdateUserRequest;
import com.homelink.api.dto.response.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    Page<UserResponse> getAll(int page, int size);

    UserResponse getById(Long id);

    UserResponse update(Long id, UpdateUserRequest request);

    void delete(Long id);
}
