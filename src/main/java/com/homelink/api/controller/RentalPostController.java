package com.homelink.api.controller;

import com.homelink.api.dto.request.RentalPostRequest;
import com.homelink.api.dto.response.RentalPostResponse;
import com.homelink.api.service.RentalPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allows your frontend to access the API
public class RentalPostController {

    private final RentalPostService service;

    /**
     * CREATE: Post a new rental property
     * URL: POST /api/rental-posts
     */
    @PostMapping
    public ResponseEntity<RentalPostResponse> create(@RequestBody RentalPostRequest request) {
        RentalPostResponse createdPost = service.save(request);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    /**
     * READ ALL: Get all available rental posts
     * URL: GET /api/rental-posts
     */
    @GetMapping
    public ResponseEntity<List<RentalPostResponse>> getAll() {
        List<RentalPostResponse> posts = service.findAll();
        return ResponseEntity.ok(posts);
    }

    /**
     * READ ONE: Get details of a specific post by ID
     * URL: GET /api/rental-posts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RentalPostResponse> getOne(@PathVariable Long id) {
        RentalPostResponse post = service.findById(id);
        return ResponseEntity.ok(post);
    }

    /**
     * UPDATE: Modify an existing post
     * URL: PUT /api/rental-posts/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RentalPostResponse> update(
            @PathVariable Long id, 
            @RequestBody RentalPostRequest request) {
        RentalPostResponse updatedPost = service.update(id, request);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * DELETE: Remove a post from the system
     * URL: DELETE /api/rental-posts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}