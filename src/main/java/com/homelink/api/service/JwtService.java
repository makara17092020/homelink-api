package com.homelink.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import com.homelink.api.entity.Role;
import com.homelink.api.entity.User;
import com.homelink.api.repository.RoleRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT Service for generating and validating tokens.
 * Stores roles in the claims as "roles".
 */
@Service
public class JwtService {

    private final RoleRepository roleRepository;

    public JwtService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // ⚠️ In production, use environment variable or config file
    private static final String SECRET_KEY = "your-very-secure-secret-key-here-512bits+changeThisToEnvVar!";
    private static final long EXPIRATION_TIME = 86400000L; // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ------------------- Token generation -------------------

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Store roles in token
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        // If we have our domain User, include userId and roleIds
        if (userDetails instanceof User u) {
            claims.put("userId", u.getId());

                // Map role names to role IDs where available.
                // Normalize stored role names to the canonical form used in Role.name
                List<Long> roleIds = u.getRoles().stream()
                    .map(rName -> rName.startsWith("ROLE_") ? rName : "ROLE_" + rName)
                    .map(normalized -> roleRepository.findByName(normalized)
                        .map(Role::getId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            claims.put("roleIds", roleIds);
            // include a primaryRoleId (first one) if present
            if (!roleIds.isEmpty()) claims.put("primaryRoleId", roleIds.get(0));
        }

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // ------------------- Extract claims -------------------

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("roles");
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Extracts the userId claim (if present) from the token.
     */
    public Optional<Long> extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object id = claims.get("userId");
        if (id instanceof Number) return Optional.of(((Number) id).longValue());
        if (id instanceof String) {
            try { return Optional.of(Long.parseLong((String) id)); } catch (NumberFormatException ex) { }
        }
        return Optional.empty();
    }

    /**
     * Extracts roleIds claim (if present) from the token.
     */
    public List<Long> extractRoleIds(String token) {
        Claims claims = extractAllClaims(token);
        Object r = claims.get("roleIds");
        if (r instanceof List<?>) {
            return ((List<?>) r).stream()
                    .map(obj -> {
                        if (obj instanceof Number) return ((Number) obj).longValue();
                        try { return Long.parseLong(obj.toString()); } catch (NumberFormatException ex) { return null; }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    // ------------------- Token validation -------------------

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
