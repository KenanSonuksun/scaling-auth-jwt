package com.project.scalingauthjwt.domain.auth.controller;

import com.project.scalingauthjwt.common.response.InstanceInfoResponse;
import com.project.scalingauthjwt.config.properties.AppProperties;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    private final AppProperties appProperties;

    public AuthTestController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/api/public/ping")
    public InstanceInfoResponse publicPing() {
        return new InstanceInfoResponse(
                appProperties.getInstanceId(),
                null,
                false
        );
    }

    @GetMapping("/api/auth/me")
    public InstanceInfoResponse me(Authentication authentication) {
        return new InstanceInfoResponse(
                appProperties.getInstanceId(),
                authentication.getName(),
                true
        );
    }

    @GetMapping("/api/admin/ping")
    public InstanceInfoResponse adminPing(Authentication authentication) {
        return new InstanceInfoResponse(
                appProperties.getInstanceId(),
                authentication.getName(),
                true
        );
    }
}