package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
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

    /**
     * Legacy role column, kept for backward compatibility.
     * Automatically updated when roles collection changes.
     */
    @Column(name = "role", nullable = false)
    private String role;

    /**
     * Collection of roles for Spring Security.
     * Roles must be prefixed with 'ROLE_' (e.g., ROLE_USER, ROLE_AGENT, ROLE_ADMIN).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Convert List<String> roles into GrantedAuthority objects for Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ensure authorities use the 'ROLE_' prefix so Spring Security's hasRole checks
        // (which look for 'ROLE_<name>') work correctly regardless of how roles are stored.
        return roles.stream()
            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

    /**
     * Keep legacy `role` column in sync with `roles` collection.
     * Uses the first role in the list.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.role = this.roles.isEmpty() ? null : this.roles.get(0);
    }

    // ---- UserDetails implementation ----
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // ---- Convenience methods ----

    /**
     * Add a role if not already present.
     * Automatically updates legacy 'role' column.
     */
    public void addRole(String roleName) {
        if (!roles.contains(roleName)) {
            roles.add(roleName);
            setRoles(roles); // sync legacy column
        }
    }

    /**
     * Remove a role.
     * Automatically updates legacy 'role' column.
     */
    public void removeRole(String roleName) {
        if (roles.contains(roleName)) {
            roles.remove(roleName);
            setRoles(roles); // sync legacy column
        }
    }

    /**
     * Check if user has a role.
     */
    public boolean hasRole(String roleName) {
        return roles.contains(roleName);
    }
}
