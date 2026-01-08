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
    private Long id; // Matches User ID type

    private String title;
    private String description;
    private String address;
    private BigDecimal price;
    private String electricityCost; 
    private String waterCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    // FIX: Changed mappedBy to "rentalPost" to match the field name in PropertyImage.java
    @OneToMany(mappedBy = "rentalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PropertyImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}