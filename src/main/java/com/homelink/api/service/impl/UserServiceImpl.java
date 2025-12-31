package com.homelink.api.service.impl;

import com.homelink.api.dto.BecomeAgentResponse;
import com.homelink.api.dto.request.CreateUserRequest;
import com.homelink.api.dto.request.UpdateUserRequest;
import com.homelink.api.dto.response.UserResponse;
import com.homelink.api.entity.Role;
import com.homelink.api.entity.User;
import com.homelink.api.exception.BadRequestException;
import com.homelink.api.exception.ResourceNotFoundException;
import com.homelink.api.repository.RoleRepository;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public BecomeAgentResponse becomeAgent(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role agentRole = roleRepository.findByName(Role.AGENT)
                .orElseThrow(() -> new RuntimeException("ROLE_AGENT not found"));

        if (!user.getRoles().contains(agentRole.getName())) {
            user.addRole(agentRole.getName());
            userRepository.save(user);
        }

        List<Role> roleEntities = roleRepository.findByNameIn(user.getRoles());
        long iat = Instant.now().getEpochSecond();
        long exp = iat + (60 * 60 * 24); 

        return BecomeAgentResponse.builder()
                .primaryRoleId(agentRole.getId())
                .roleIds(roleEntities.stream().map(Role::getId).toList())
                .roles(user.getRoles())
                .userId(user.getId())
                .sub(user.getUsername())
                .iat(iat)
                .exp(exp)
                .build();
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(Role.USER));
        user.setRole(Role.USER);
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public Page<UserResponse> getAll(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).map(UserResponse::from);
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.from(user);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}