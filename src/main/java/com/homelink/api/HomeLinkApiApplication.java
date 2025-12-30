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

@SpringBootApplication
public class HomeLinkApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeLinkApiApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Ensure canonical roles exist in the roles table (for validation or listing if needed)
            for (String rn : java.util.List.of(Role.ADMIN, Role.AGENT, Role.USER)) {
                roleRepository.findByName(rn).orElseGet(() -> roleRepository.save(new Role(rn)));
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("adminpass123")); // Change this!
                admin.setEmail("admin@homelink.com");
                // Set the single role string (no join table needed)
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}