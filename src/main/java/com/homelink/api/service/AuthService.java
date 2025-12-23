package com.homelink.api.service;

import com.homelink.api.dto.AuthResponse;
import com.homelink.api.dto.RegisterRequest;
import com.homelink.api.entity.User;
import com.homelink.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken");
        }
        
        User user = new User();
        user.setFullName(request.getFullName()); // Map the name here
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        
        // If client provided roles use them; otherwise default to USER
        if (request.getRoles() != null && request.getRoles().length > 0) {
            user.setRoles(java.util.Arrays.asList(request.getRoles()));
        } else {
            user.setRoles(java.util.List.of("USER"));
        }
        
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return mapToAuthResponse(savedUser, token);
    }

    public AuthResponse login(String username, String password) {
        // 1. Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // 3. Generate Token
        String token = jwtService.generateToken(user);

        // 4. Return Detailed Response
        return mapToAuthResponse(user, token);
    }

    /**
     * Helper method to convert User entity and Token into AuthResponse DTO
     */
    private AuthResponse mapToAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .fullName(user.getFullName()) // Include in response
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }
}