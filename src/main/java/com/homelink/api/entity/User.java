package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

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

    // Legacy single-column role kept for compatibility with existing DB schema
    @Column(name = "role", nullable = false)
    private String role;

    // New many-to-many mapping to the `roles` table. We keep migrations that
    // migrate data from the old `user_roles` element-collection table into
    // the new join table `user_role_map`.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role_map",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private java.util.Set<Role> roles = new java.util.HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }

    // Backwards-compatible setter: accept list of role names (legacy code paths)
    public void setRoles(java.util.List<String> roleNames) {
        if (roleNames == null) {
            this.roles = new java.util.HashSet<>();
        } else {
            // Create transient Role objects by name. After save, JPA should
            // be able to resolve actual Role entities when merged with proper
            // Role repository usage in service layer.
            this.roles = roleNames.stream().map(Role::new).collect(java.util.stream.Collectors.toSet());
        }
        if (this.roles.isEmpty()) {
            this.role = null;
        } else {
            this.role = this.roles.iterator().next().getName();
        }
    }

    // Preferred setter for new code paths
    public void setRoles(java.util.Set<Role> roles) {
        this.roles = roles != null ? roles : new java.util.HashSet<>();
        if (this.roles.isEmpty()) {
            this.role = null;
        } else {
            this.role = this.roles.iterator().next().getName();
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