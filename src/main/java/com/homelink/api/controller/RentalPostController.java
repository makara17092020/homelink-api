package com.homelink.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homelink.api.dto.request.CreateRentalPostRequest;
import com.homelink.api.dto.request.UpdateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import com.homelink.api.service.RentalPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class RentalPostController {

    private final RentalPostService rentalPostService;

    @PostMapping
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<RentalPostResponse> create(
            @Valid @RequestBody CreateRentalPostRequest request,
            Authentication authentication
    ) {
        // authentication.getName() returns the username from the JWT
        RentalPostResponse response = rentalPostService.createPost(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<RentalPostResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRentalPostRequest request,
            Authentication authentication
    ) {
        RentalPostResponse response = rentalPostService.updatePost(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
}