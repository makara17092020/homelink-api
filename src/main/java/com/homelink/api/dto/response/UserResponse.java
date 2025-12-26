package com.homelink.api.dto.response;

import com.homelink.api.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String[] roles;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }
}








