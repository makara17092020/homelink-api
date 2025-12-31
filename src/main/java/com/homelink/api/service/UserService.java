package com.homelink.api.service;

import com.homelink.api.dto.BecomeAgentResponse;
import com.homelink.api.entity.Role;
import com.homelink.api.entity.User;
import com.homelink.api.repository.RoleRepository;
import com.homelink.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Promote logged-in user to AGENT and return payload
    public BecomeAgentResponse becomeAgent(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role agentRole = roleRepository.findByName(Role.ROLE_AGENT)
                .orElseThrow(() -> new RuntimeException("ROLE_AGENT not found"));

        // Add ROLE_AGENT if not exists
        if (!user.getRoles().contains(agentRole.getName())) {
            user.addRole(agentRole.getName());
            userRepository.save(user);
        }

        // Convert role names -> Role entities
        List<Role> roleEntities = roleRepository.findByNameIn(user.getRoles());

        long iat = Instant.now().getEpochSecond();
        long exp = iat + (60 * 60 * 24); // 24h

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
}
