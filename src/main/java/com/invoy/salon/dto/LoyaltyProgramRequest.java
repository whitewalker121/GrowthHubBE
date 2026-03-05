package com.growthhub.salon.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoyaltyProgramRequest {
    private String name;
    private String type;
    private String description;
    private BigDecimal pointsPerRupee;
    private BigDecimal valuePerPoint;
    private Integer minRedeemPoints;
    private BigDecimal maxRedeemPct;
    private Integer bonusOnSignup;
    private BigDecimal multiplier;
    private String tierRequired;
    private Integer bonusPoints;
    private Integer referrerPoints;
    private Integer refereePoints;
    private String applicableTo;
    private String status;
}
