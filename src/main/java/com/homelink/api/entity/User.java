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
     * This is your main role column in the 'users' table.
     * Most of your data is likely here.
     */
    @Column(name = "role", nullable = false)
    private String role;

    /**
     * This maps to the separate 'user_roles' table.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * THE FIX: This method now checks both the List and the single Column.
     * This prevents the "Access Denied" error if one of them is empty.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 1. Add roles from the collection table (user_roles)
        if (roles != null && !roles.isEmpty()) {
            authorities.addAll(roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }

        // 2. Add the role from the main users table if not already present
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
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        if (!this.roles.contains(roleName)) {
            this.roles.add(roleName);
        }
        // Keep the legacy column updated
        this.role = roleName;
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