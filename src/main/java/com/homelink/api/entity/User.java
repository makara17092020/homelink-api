package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    // Legacy single-column role (now the primary way to store role)
    @Column(name = "role", nullable = false)
    private String role;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Use the single 'role' for authorities
        if (this.role != null && !this.role.isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority(this.role));
        }
        return Collections.emptyList();
    }

    // Optional: If legacy code expects setRoles(List<String>), adapt it to set the single role
    public void setRoles(java.util.List<String> roleNames) {
        if (roleNames != null && !roleNames.isEmpty()) {
            this.role = roleNames.get(0); // Take the first role (or handle multiple as comma-separated if needed)
        } else {
            this.role = null;
        }
    }

    public java.util.List<String> getRoles() {
    return this.role != null ? java.util.List.of(this.role) : java.util.Collections.emptyList();
}

    @Override
    public boolean isAccountNonExpired() { return true; }
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}