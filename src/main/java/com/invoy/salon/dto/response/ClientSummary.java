package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.ClientTier;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ClientSummary {
    private UUID id;
    private String name;
    private String phone;
    private String avatarInitials;
    private ClientTier membershipType;
    private Integer loyaltyPoints;
    private BigDecimal walletBalance;
    private LocalDate lastVisitAt;
}
