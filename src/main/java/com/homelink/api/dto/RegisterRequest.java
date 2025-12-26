package com.homelink.api.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String username;
    private String password;
    private String email;
    // roles removed from client input. Server will assign default role USER.
}
