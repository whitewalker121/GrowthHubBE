package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.ClientTier;
import com.growthhub.salon.enums.GenderType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ClientResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
    private GenderType gender;
    private String address;
    private String avatarInitials;
    private ClientTier membershipType;
    private Integer loyaltyPoints;
    private BigDecimal walletBalance;
    private Integer totalVisits;
    private BigDecimal totalSpend;
    private LocalDate lastVisitAt;
    private String notes;
    private String[] tags;
    private String referralCode;
    private Boolean isActive;
    private LocalDate joinDate;
    private Instant createdAt;
}
