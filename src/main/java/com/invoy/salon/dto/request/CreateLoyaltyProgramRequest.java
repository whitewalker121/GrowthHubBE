package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.ClientTier;
import com.growthhub.salon.enums.LoyaltyProgramType;
import com.growthhub.salon.enums.RedeemType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoyaltyProgramRequest {
    @NotBlank
    @Size(max = 150)
    private String name;
    private String description;
    @NotNull
    private LoyaltyProgramType programType;

    @DecimalMin("0")
    private BigDecimal pointsPerRupee = BigDecimal.ONE;
    @DecimalMin("0")
    private BigDecimal valuePerPoint = BigDecimal.valueOf(0.5);
    @Min(0)
    private Integer minRedeemPoints = 100;
    @Min(0)
    @Max(100)
    private Integer maxRedeemPct = 20;
    @Min(0)
    private Integer bonusOnSignup = 0;
    @DecimalMin("1")
    private BigDecimal multiplier;
    private ClientTier tierRequired;
    @Min(0)
    private Integer bonusPoints;
    @Min(0)
    private Integer referrerPoints;
    @Min(0)
    private Integer refereePoints;
    private RedeemType applicableTo = RedeemType.ALL;
}
