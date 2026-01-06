package com.homelink.api.config;

import com.homelink.api.filter.JwtAuthenticationFilter;
import com.homelink.api.repository.UserRepository;
import com.homelink.api.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public SecurityConfig(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // Note: Ensure you have a CorsConfigurationSource bean if using a Frontend
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. PUBLIC ENDPOINTS (No Token Required)
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/**", "/api/rental-posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/post/**").permitAll() // Allow seeing reviews for a post
                
                // 2. ADMIN ONLY
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                
                // 3. AGENT ONLY (Creating/Managing Posts)
                .requestMatchers(HttpMethod.POST, "/api/properties/**").hasAuthority("AGENT")
                .requestMatchers("/api/agent/**").hasAuthority("AGENT")
                
                // 4. USER/AUTHENTICATED (Posting Reviews/Profile)
                .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated()
                .requestMatchers("/api/users/become-agent").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                
                // 5. EVERYTHING ELSE
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthFilter(UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}