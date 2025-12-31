package com.homelink.api.service.impl;

import com.homelink.api.dto.request.CreateUserRequest;
import com.homelink.api.dto.request.UpdateUserRequest;
import com.homelink.api.dto.response.UserResponse;
import com.homelink.api.entity.User;
import com.homelink.api.exception.BadRequestException;
import com.homelink.api.exception.ResourceNotFoundException;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "AGENT", "USER");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        List<String> roles = normalizeRoles(request.getRoles());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);
        user.setRole(roles.get(0)); // legacy column sync

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public Page<UserResponse> getAll(int page, int size) {
        return userRepository
                .findAll(PageRequest.of(page, size))
                .map(UserResponse::from);
    }

    @Override
    public UserResponse getById(Long id) {
        return UserResponse.from(findUser(id));
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findUser(id);

        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getRoles() != null) {
            List<String> roles = normalizeRoles(request.getRoles());
            user.setRoles(roles);
            user.setRole(roles.get(0));
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    private List<String> normalizeRoles(String[] roles) {
        if (roles == null || roles.length == 0) {
            return List.of("USER");
        }

        for (String role : roles) {
            if (!ALLOWED_ROLES.contains(role)) {
                throw new BadRequestException("Invalid role: " + role);
            }
        }
        return List.of(roles);
    }
}
