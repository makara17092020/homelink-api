package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User entity implementing Spring Security's UserDetails.
 * Supports multiple roles via List<String> and keeps legacy 'role' column for backward compatibility.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    // Legacy single-column role kept for compatibility with existing DB schema
    @Column(name = "role", nullable = false)
    private String role;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * ADDED METHOD: Fixes the "addRole is undefined" error.
     * This adds a role to the collection and ensures the legacy column is updated.
     */
    public void addRole(String roleName) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        if (!this.roles.contains(roleName)) {
            this.roles.add(roleName);
        }
        // Update legacy column if it's currently empty
        if (this.role == null || this.role.isEmpty()) {
            this.role = roleName;
        }
    }

    /**
     * Convert List<String> roles into GrantedAuthority objects for Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Keep legacy `role` column in sync with `roles` collection.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
        if (this.roles.isEmpty()) {
            this.role = "ROLE_USER"; // Default to avoid null constraint errors
        } else {
            this.role = this.roles.get(0);
        }
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}