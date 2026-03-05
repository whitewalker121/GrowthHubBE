package com.growthhub.salon.dto;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private Long staffId;
}
