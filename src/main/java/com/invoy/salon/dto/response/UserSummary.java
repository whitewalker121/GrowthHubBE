package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserSummary {
    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
}
