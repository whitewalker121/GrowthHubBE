package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.StaffStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StaffSummary {
    private UUID id;
    private String fullName;
    private String role;
    private String avatarInitials;
    private StaffStatus status;
}
