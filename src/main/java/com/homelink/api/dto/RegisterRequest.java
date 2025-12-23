package com.homelink.api.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String[] roles; // e.g. ["USER"] or ["AGENT"] (ADMIN seeded)
}
