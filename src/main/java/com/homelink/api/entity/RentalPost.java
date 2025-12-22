package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rental_posts")
@Data
public class RentalPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private Double price;

    private String location;

    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean active = true;

    @OneToMany(mappedBy = "post")
    private List<Review> reviews;
}