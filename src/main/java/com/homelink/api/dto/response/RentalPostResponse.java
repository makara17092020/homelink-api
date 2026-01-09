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
    private Double electricityCost;
    private Double waterCost;
    private String agentName;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private Double averageRating;
    private Boolean active; 
    private Integer totalRatings; 
    private List<ReviewResponse> reviews; 
}