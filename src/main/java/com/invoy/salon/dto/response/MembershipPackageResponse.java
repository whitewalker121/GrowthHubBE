package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MembershipPackageResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer validityDays;
    private Integer includedCount;
    private BigDecimal bonusWallet;
    private BigDecimal discountPct;
    private String colorHex;
    private String status;
    private List<String> includedServices;
    private Integer sortOrder;
    private long totalSold;
    private BigDecimal totalRevenue;
    private Instant createdAt;
}
