package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.GenderType;
import com.growthhub.salon.enums.StaffStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StaffResponse {
    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private GenderType gender;
    private LocalDate dateOfBirth;
    private String address;
    private String role;
    private LocalDate joinDate;
    private String avatarInitials;
    private StaffStatus status;
    private BigDecimal baseSalary;
    private BigDecimal commissionRate;
    private BigDecimal targetRevenue;
    private List<String> specializations;
    private String workingDays;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;
    private BigDecimal rating;
    private Integer totalClients;
    private Instant createdAt;
}
