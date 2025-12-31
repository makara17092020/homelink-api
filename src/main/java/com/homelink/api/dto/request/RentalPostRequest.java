package com.homelink.api.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RentalPostRequest {
    private String title;
    private String description;
    private Double price;
    private String address;
    private String electricityCost;
    private String waterCost;
    private Long agentId;
    // If you want to handle images during creation
    private List<String> imageUrls; 
}