package com.project.scalingauthjwt.domain.user.service;

import com.project.scalingauthjwt.common.exception.ConflictException;
import com.project.scalingauthjwt.common.exception.ResourceNotFoundException;
import com.project.scalingauthjwt.domain.role.entity.Role;
import com.project.scalingauthjwt.domain.role.service.RoleService;
import com.project.scalingauthjwt.domain.user.entity.User;
import com.project.scalingauthjwt.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String rawPassword) {
        validateUsernameAndEmailUniqueness(username, email);

        User user = new User(
                username,
                email,
                passwordEncoder.encode(rawPassword),
                true
        );

        Role userRole = roleService.getRequiredRole("USER");
        user.addRole(userRole);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getRequiredUserWithRolesByUsername(String username) {
        return userRepository.findWithRolesByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private void validateUsernameAndEmailUniqueness(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already exists: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already exists: " + email);
        }
    }
}