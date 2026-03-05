package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.request.*;
import com.growthhub.salon.dto.response.*;
import com.growthhub.salon.entity.AppUser;
import com.growthhub.salon.enums.UserRole;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.AppUserRepository;
import com.growthhub.salon.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        AppUser user = userRepository.findByEmail(req.getEmail());
          //  .orElseThrow(() -> new ResourceNotFoundException("User", 0l));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BusinessException("Account is deactivated. Please contact admin.");
        }

        String accessToken  = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

       // user.setre(refreshToken);
       // user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(86400L)
            .user(UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
               // .fullName(user.getFullName())
                .role(UserRole.valueOf(user.getRole()))
                .build())
            .build();
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest req) {
        if (!jwtUtil.validateToken(req.getRefreshToken())) {
            throw new BusinessException("Refresh token is invalid or expired"
                    );
        }

        String email = jwtUtil.extractEmail(req.getRefreshToken());
        AppUser user = userRepository.findByEmail(email);
       //     .orElseThrow(() -> new ResourceNotFoundException("User", email));

       /* if (!req.getRefreshToken().equals(user.getRefreshToken())) {
            throw new BusinessException("Refresh token mismatch",
                org.springframework.http.HttpStatus.UNAUTHORIZED);
        }*/

        String newAccess  = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
        String newRefresh = jwtUtil.generateRefreshToken(user.getEmail());
        //user.setRefreshToken(newRefresh);
        userRepository.save(user);

        return AuthResponse.builder()
            .accessToken(newAccess)
            .refreshToken(newRefresh)
            .tokenType("Bearer")
            .expiresIn(86400L)
            .user(UserSummary.builder()
                .id(user.getId()).email(user.getEmail())
                //.fullName(user.getFullName())
                     .role(UserRole.valueOf(user.getRole()))
                .build())
            .build();
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest req) {
        AppUser user = userRepository.findByEmail(email);
      //      .orElseThrow(() -> new ResourceNotFoundException("User", email));

      /*  if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect");
        }*/

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        // user.setRefreshToken(null);   // invalidate all sessions
        userRepository.save(user);
    }

    @Transactional
    public void logout(String email) {
       /* userRepository.findByEmail(email).ifPresent(u -> {
            u.setRefreshToken(null);
            userRepository.save(u);
        });*/
    }
}
