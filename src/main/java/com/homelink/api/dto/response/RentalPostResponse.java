package com.homelink.api.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RentalPostResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private BigDecimal price;
    private String electricityCost;
    private String waterCost;
    private String agentName; // From User entity
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private Double averageRating;
}