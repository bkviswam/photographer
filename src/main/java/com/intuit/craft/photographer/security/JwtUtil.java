package com.intuit.craft.photographer.security;

import com.intuit.craft.photographer.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final JWTConfig jwtConfig;

    @Autowired
    public JwtUtil(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // Generate the signing key from the secret key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    // Generate a JWT token with userId and role as claims
    public String generateToken(Long userId, Role role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))  // Store userId as the subject (sub claim)
                .claim("role", role.name())           // Store role as a claim
                .setIssuedAt(new Date())              // Token issue time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)) // 8-hour expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign with the secret key
                .compact();
    }

    // Extract all claims from the token
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract the userId from the token
    public Long extractUserId(String token) {
        try {
            String userIdString = extractClaims(token).getSubject();  // Get subject (userId as String)
            if (userIdString == null || userIdString.isEmpty()) {
                throw new RuntimeException("UserId is missing in the token");
            }
            return Long.parseLong(userIdString);  // Parse to Long
        } catch (Exception e) {
            throw new RuntimeException("Invalid token or userId extraction failed", e);
        }
    }

    // Check if the token is valid based on userId
    public boolean isTokenValid(String token, Long userId) {
        try {
            return extractUserId(token).equals(userId) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;  // Invalid token or userId mismatch
        }
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}