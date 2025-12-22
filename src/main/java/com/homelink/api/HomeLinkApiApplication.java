package com.homelink.api;

import com.homelink.api.entity.Role;
import com.homelink.api.entity.User;
import com.homelink.api.repository.UserRepository;
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
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("adminpass123"));  // Change this!
                admin.setEmail("admin@homelink.com");
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}