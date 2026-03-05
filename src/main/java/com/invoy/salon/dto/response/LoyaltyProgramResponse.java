package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.ClientTier;
import com.growthhub.salon.enums.LoyaltyProgramType;
import com.growthhub.salon.enums.RedeemType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class LoyaltyProgramResponse {
    private UUID id;
    private String name;
    private String description;
    private LoyaltyProgramType programType;
    private String status;
    private BigDecimal pointsPerRupee;
    private BigDecimal valuePerPoint;
    private Integer minRedeemPoints;
    private BigDecimal maxRedeemPct;
    private Integer bonusOnSignup;
    private BigDecimal multiplier;
    private ClientTier tierRequired;
    private Integer bonusPoints;
    private Integer referrerPoints;
    private Integer refereePoints;
    private RedeemType applicableTo;
    private Instant createdAt;
}
