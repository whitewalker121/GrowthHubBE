package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.AppUser;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.AppUserRepository;
import com.growthhub.salon.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Transactional
public class AuthServiceImpl {

    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest req) {
        AppUser user = userRepo.findByUsername(req.getUsername())
            .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!user.getIsActive())
            throw new BusinessException("Account is deactivated. Please contact admin.");

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new BusinessException("Invalid username or password");

        String token = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        return LoginResponse.builder()
            .token(token).tokenType("Bearer")
            .username(user.getUsername()).role(user.getRole())
            .staffId(user.getStaff() != null ? user.getStaff().getId() : null)
            .build();
    }

    public AppUser createUser(String username, String rawPassword, String email, String role, Long staffId) {
        if (userRepo.existsByUsername(username))
            throw new DuplicateResourceException("Username already taken: " + username);
        if (userRepo.existsByEmail(email))
            throw new DuplicateResourceException("Email already registered: " + email);

        return userRepo.save(AppUser.builder()
            .username(username).password(passwordEncoder.encode(rawPassword))
            .email(email).role(role).isActive(true)
            .build());
    }
}
