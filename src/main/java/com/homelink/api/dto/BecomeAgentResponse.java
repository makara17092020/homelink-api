package com.homelink.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BecomeAgentResponse {
    private Long primaryRoleId;
    private List<Long> roleIds;
    private List<String> roles;
    private Long userId;
    private String sub;
    private Long iat;
    private Long exp;
}
