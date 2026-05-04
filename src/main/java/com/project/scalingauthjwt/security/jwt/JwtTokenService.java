package com.project.scalingauthjwt.security.jwt;

import com.project.scalingauthjwt.config.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtTokenService {

    private final AppProperties appProperties;

    public JwtTokenService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String generateAccessToken(String username) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(appProperties.getSecurity().getAccessTokenExpirationMinutes());

        return Jwts.builder()
                .subject(username)
                .issuedAt(toDate(now))
                .expiration(toDate(expiresAt))
                .signWith(getSigningKey())
                .compact();
    }

    public LocalDateTime getAccessTokenExpiresAt() {
        return LocalDateTime.now()
                .plusMinutes(appProperties.getSecurity().getAccessTokenExpirationMinutes());
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                appProperties.getSecurity()
                        .getJwtSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}