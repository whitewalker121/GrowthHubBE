//package com.growthhub.salon.security;
//
//import com.growthhub.salon.entity.User;
//import com.growthhub.salon.repository.UserRepository;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.*;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.*;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.util.*;
//
//// ─────────────────────────────────────────────────────────────
//// JWT UTILITY
//// ─────────────────────────────────────────────────────────────
//
//@Component
//@Slf4j
//public class JwtUtil {
//
//    @Value("${app.jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${app.jwt.expiration-ms}")
//    private long jwtExpirationMs;
//
//    @Value("${app.jwt.refresh-expiration-ms}")
//    private long refreshExpirationMs;
//
//    private SecretKey key() {
//        byte[] bytes = Decoders.BASE64.decode(
//            Base64.getEncoder().encodeToString(jwtSecret.getBytes()));
//        return Keys.hmacShaKeyFor(bytes);
//    }
//
//    public String generateAccessToken(String email, String role) {
//        return Jwts.builder()
//                .subject(email)
//                .claim("role", role)
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(key())
//                .compact();
//    }
//
//    public String generateRefreshToken(String email) {
//        return Jwts.builder()
//                .subject(email)
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
//                .signWith(key())
//                .compact();
//    }
//
//    public String extractEmail(String token) {
//        return parseClaims(token).getSubject();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            parseClaims(token);
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            log.warn("Invalid JWT token: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    private Claims parseClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(key())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
//// CUSTOM USER DETAILS
//// ─────────────────────────────────────────────────────────────
//
//@RequiredArgsConstructor
//class SalonUserDetails implements UserDetails {
//
//    private final User user;
//
//    @Override public String getUsername() { return user.getEmail(); }
//    @Override public String getPassword() { return user.getPasswordHash(); }
//    @Override public boolean isEnabled() { return Boolean.TRUE.equals(user.getIsActive()); }
//    @Override public boolean isAccountNonExpired()  { return true; }
//    @Override public boolean isAccountNonLocked()   { return true; }
//    @Override public boolean isCredentialsNonExpired() { return true; }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
//    }
//
//    public User getUser() { return user; }
//}
//
//// ─────────────────────────────────────────────────────────────
//// USER DETAILS SERVICE
//// ─────────────────────────────────────────────────────────────
//
//@Service
//@RequiredArgsConstructor
//class SalonUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//        return new SalonUserDetails(user);
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
//// JWT AUTH FILTER
//// ─────────────────────────────────────────────────────────────
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final SalonUserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain) throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        if (header == null || !header.startsWith("Bearer ")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String token = header.substring(7);
//        if (!jwtUtil.validateToken(token)) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            String email = jwtUtil.extractEmail(token);
//            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//                var auth = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        } catch (Exception e) {
//            log.error("Cannot set user authentication: {}", e.getMessage());
//        }
//
//        chain.doFilter(request, response);
//    }
//}
