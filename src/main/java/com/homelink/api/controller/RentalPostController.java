package com.homelink.api.controller;

import com.homelink.api.dto.request.CreateRentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import com.homelink.api.service.RentalPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class RentalPostController {

    private final RentalPostService rentalPostService;

    @PostMapping
    // We use hasAuthority because your DB stores "AGENT", not "ROLE_AGENT"
    @PreAuthorize("hasAuthority('AGENT') or hasAuthority('ADMIN')")
    public ResponseEntity<RentalPostResponse> create(
            @Valid @RequestBody CreateRentalPostRequest request,
            Authentication authentication
    ) {
        // authentication.getName() retrieves the username from the JWT token
        RentalPostResponse response = rentalPostService.createPost(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}