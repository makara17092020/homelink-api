package com.homelink.api.service;

import com.homelink.api.entity.Role;
import com.homelink.api.entity.User;
import com.homelink.api.repository.RoleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    @Test
    void tokenContainsUserIdAndRoleIds() {
        RoleRepository roleRepo = mock(RoleRepository.class);
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(new Role(2L, "ROLE_USER")));

        JwtService jwtService = new JwtService(roleRepo);

        User user = new User();
        user.setId(123L);
        user.setUsername("joh21");
        user.setRoles(List.of("ROLE_USER"));

        String token = jwtService.generateToken(user);

        assertEquals("joh21", jwtService.extractUsername(token));
        assertTrue(jwtService.extractUserId(token).isPresent());
        assertEquals(123L, (long) jwtService.extractUserId(token).get());

        var roleIds = jwtService.extractRoleIds(token);
        assertEquals(1, roleIds.size());
        assertEquals(2L, (long) roleIds.get(0));
    }
}
