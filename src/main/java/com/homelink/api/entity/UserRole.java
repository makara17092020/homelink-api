package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
// Use a distinct table name to avoid colliding with the ElementCollection-based
// `user_roles` table used to store simple String role values.
@Table(name = "user_role_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many roles can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many users can have the same role
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    // Optional convenience constructor
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.assignedAt = LocalDateTime.now();
    }
}