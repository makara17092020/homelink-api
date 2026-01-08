package com.homelink.api.service;

import com.homelink.api.dto.AuthResponse;
import com.homelink.api.dto.RegisterRequest;
import com.homelink.api.entity.User;
import com.homelink.api.exception.BadRequestException;
import com.homelink.api.exception.ResourceNotFoundException;
import com.homelink.api.exception.UserAlreadyExistsException;
import com.homelink.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        // 1. Check if username exists -> Throw 409 Conflict
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }

        // 2. Check if email exists -> Throw 409 Conflict
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }
        
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        
        // Use clean role names without "ROLE_" prefix to match your security logic
        String defaultRole = "USER";
        user.setRole(defaultRole); 
        user.setRoles(List.of(defaultRole)); 
        
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return mapToAuthResponse(savedUser, token);
    }

    public AuthResponse login(String username, String password) {
        // 1. Find user -> Throw 404 Not Found if missing
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 2. Verify password -> Throw 400 Bad Request if incorrect
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
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
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }
}