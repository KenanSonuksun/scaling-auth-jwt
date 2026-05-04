package com.project.scalingauthjwt.domain.auth.controller;

import com.project.scalingauthjwt.config.properties.AppProperties;
import com.project.scalingauthjwt.domain.auth.dto.LoginRequest;
import com.project.scalingauthjwt.domain.auth.dto.LoginResponse;
import com.project.scalingauthjwt.domain.user.entity.User;
import com.project.scalingauthjwt.domain.user.service.UserService;
import com.project.scalingauthjwt.security.jwt.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AppProperties appProperties;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenService jwtTokenService,
                          AppProperties appProperties) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.appProperties = appProperties;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.getRequiredUserWithRolesByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new LoginResponse(
                jwtTokenService.generateAccessToken(user.getUsername()),
                "Bearer",
                jwtTokenService.getAccessTokenExpiresAt(),
                appProperties.getInstanceId()
        );
    }
}