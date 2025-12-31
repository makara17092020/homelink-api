package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Role entity persisted in the `roles` table. Kept static constants for
 * convenience when referring to role names in code.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Role(String name) { this.name = name; }

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_AGENT = "ROLE_AGENT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
}
