package com.homelink.api.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class UpdateRentalPostRequest {
    private String title;
    private String description;
    private String address;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String electricityCost;
    private String waterCost;

    private List<String> imageUrls;
}
