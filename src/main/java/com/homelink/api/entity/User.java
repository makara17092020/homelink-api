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
     * Main role column in 'users' table.
     */
    @Column(name = "role", nullable = false)
    private String role;

    /**
     * Collection table 'user_roles' for multiple roles.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 1. Load from the 'user_roles' table
        if (roles != null && !roles.isEmpty()) {
            authorities.addAll(roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }

        // 2. Load from the 'users.role' column (fallback)
        if (this.role != null && !this.role.isEmpty()) {
            boolean alreadyExists = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals(this.role));
            if (!alreadyExists) {
                authorities.add(new SimpleGrantedAuthority(this.role));
            }
        }

        return authorities;
    }

    public void addRole(String roleName) {
        if (this.roles == null) this.roles = new ArrayList<>();
        if (!this.roles.contains(roleName)) {
            this.roles.add(roleName);
        }
        this.role = roleName; // Keep main column in sync
    }

    // Standard UserDetails methods
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}