package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.MembershipStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ClientMembershipResponse {
    private UUID id;
    private ClientSummary client;
    private String packageName;
    private UUID packageId;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private Integer servicesUsed;
    private Integer servicesTotal;
    private BigDecimal walletBalance;
    private MembershipStatus status;
    private Instant createdAt;
}
