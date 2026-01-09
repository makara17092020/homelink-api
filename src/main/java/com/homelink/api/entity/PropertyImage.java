package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "property_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    
    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    // FIX: Change this from property_id back to rental_post_id 
    // because the Error log says "rental_post_id" violates not-null constraint.
    @JoinColumn(name = "rental_post_id", nullable = false) 
    private RentalPost rentalPost;
}