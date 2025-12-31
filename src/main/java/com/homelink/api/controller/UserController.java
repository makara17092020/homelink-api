package com.homelink.api.controller;

import com.homelink.api.dto.BecomeAgentResponse;
import com.homelink.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Self-promotion: logged-in user becomes AGENT
    @PostMapping("/become-agent")
    public ResponseEntity<BecomeAgentResponse> becomeAgent(Authentication authentication) {

        String username = authentication.getName(); // from JWT

        return ResponseEntity.ok(
                userService.becomeAgent(username)
        );
    }
}

