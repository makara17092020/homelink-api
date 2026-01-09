package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "rental_posts")
@Getter 
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class RentalPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "electricity_cost") // Added explicit column mapping
    private Double electricityCost; 
    
    @Column(name = "water_cost") // Added explicit column mapping
    private Double waterCost;

    @Column(nullable = false)
    @Builder.Default 
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    // Inside RentalPost.java
    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PropertyImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // HELPER METHOD: This ensures both sides of the relationship are linked correctly
    public void addImage(PropertyImage image) {
        images.add(image);
        image.setRentalPost(this); // This links the ID
    }
}