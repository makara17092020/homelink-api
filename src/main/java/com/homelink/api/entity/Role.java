package com.homelink.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    public static final String ADMIN = "ADMIN";
    public static final String AGENT = "AGENT";
    public static final String USER = "USER";
    
    // For legacy support if your code uses Role.ROLE_AGENT
    // public static final String ROLE_AGENT = "ROLE_AGENT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}