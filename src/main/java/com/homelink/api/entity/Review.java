package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;  // 1-5

    private String comment;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private RentalPost post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    

    private LocalDateTime createdAt = LocalDateTime.now();
}