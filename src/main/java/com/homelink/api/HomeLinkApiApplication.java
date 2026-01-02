package com.homelink.api;

import com.homelink.api.entity.User;
import com.homelink.api.entity.Role;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@SpringBootApplication
public class HomeLinkApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeLinkApiApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            for (String rn : List.of(Role.ADMIN, Role.AGENT, Role.USER)) {
                roleRepository.findByName(rn).orElseGet(() -> roleRepository.save(new Role(rn)));
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("adminpass123"));
                admin.setEmail("admin@homelink.com");
                admin.setRole(Role.ADMIN);
                admin.setRoles(List.of(Role.ADMIN));
                userRepository.save(admin);
            }
        };
    }
}