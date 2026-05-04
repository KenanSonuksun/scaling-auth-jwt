package com.project.scalingauthjwt.domain.auth.dto;

import java.time.LocalDateTime;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private LocalDateTime expiresAt;
    private String issuedByInstance;

    public LoginResponse(String accessToken,
                         String tokenType,
                         LocalDateTime expiresAt,
                         String issuedByInstance) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
        this.issuedByInstance = issuedByInstance;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getIssuedByInstance() {
        return issuedByInstance;
    }
}