package org.example.newjournal.Utils;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyRaw;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key secretKey; // will be initialized lazily

    private Key getSigningKey() {
        if (secretKey == null) {
            byte[] keyBytes = secretKeyRaw.getBytes();
            if (keyBytes.length < 32) {
                throw new IllegalArgumentException("JWT secret key must be at least 32 bytes for HS256.");
            }
            secretKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return secretKey;
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        Claims claims = extractClaims(token);
        Date expiration = claims.getExpiration();
        return expiration != null && !expiration.before(new Date()) &&
               claims.getSubject() != null && !claims.getSubject().isEmpty();
    }

    

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
