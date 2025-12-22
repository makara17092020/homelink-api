package com.homelink.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;

    @Builder.Default
    private String type = "Bearer"; // Now the builder will respect this!

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
}