package com.homelink.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReviewResponse {

    private UUID id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String userName; // Or fullName if preferred
    private Long rentalPostId;
}