package com.homelink.api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RentalPostResponse {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String address;
    private String electricityCost;
    private String waterCost;
    private String agentName; // Flattened for easy UI display
    private LocalDateTime createdAt;
    private List<String> imageUrls;
}