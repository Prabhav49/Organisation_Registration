package com.prabhav.employee.auth;

import com.prabhav.employee.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET = "cr666N7wIV+KJ2xOQpWtcfAekL4YXd9gbnJMs8SJ9sI=";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    public String generateToken(String email) {
        return generateToken(email, Role.EMPLOYEE);
    }

    public String generateToken(String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.getRoleName());
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
            
            if (token == null || token.isEmpty()) {
                System.out.println("Token is null or empty");
                return false;
            }
            
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
                System.out.println("Removed Bearer prefix, token now: " + token.substring(0, Math.min(20, token.length())) + "...");
            }
            
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token); // Parse and validate the token
            System.out.println("Token validation successful");
            return true; // Token is valid
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            e.printStackTrace();
            return false; // Token is invalid or expired
        }
    }

    public boolean isTokenValid(String token, String email) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);  // Remove the "Bearer " part
        }
        token = token.trim();  // Remove any extra spaces

        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    public boolean hasRole(String token, Role requiredRole) {
        String tokenRole = extractRole(token);
        return tokenRole != null && tokenRole.equals(requiredRole.getRoleName());
    }

    public boolean hasAnyRole(String token, Role... roles) {
        String tokenRole = extractRole(token);
        if (tokenRole == null) return false;
        
        for (Role role : roles) {
            if (tokenRole.equals(role.getRoleName())) {
                return true;
            }
        }
        return false;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
