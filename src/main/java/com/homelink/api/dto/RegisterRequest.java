package com.homelink.api.dto;

import com.homelink.api.entity.Role;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String username;
    private String password;
    private String email;
    private Role role; // RENTER OR AGENT (ADMIN seeded)
}
