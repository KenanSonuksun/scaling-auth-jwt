package com.project.scalingauthjwt.config.web;

import com.project.scalingauthjwt.domain.role.entity.Role;
import com.project.scalingauthjwt.domain.role.service.RoleService;
import com.project.scalingauthjwt.domain.user.entity.User;
import com.project.scalingauthjwt.domain.user.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserSeeder {

    @Bean
    public ApplicationRunner seedAdminUser(UserRepository userRepository,
                                           RoleService roleService,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.existsByUsername("admin")) {
                return;
            }

            User admin = new User(
                    "admin",
                    "admin@example.com",
                    passwordEncoder.encode("Admin123"),
                    true
            );

            Role adminRole = roleService.getRequiredRole("ADMIN");
            admin.addRole(adminRole);

            userRepository.save(admin);
        };
    }
}