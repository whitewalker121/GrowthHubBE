package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "loyalty_programs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoyaltyProgram extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 20)
    private String type;  // points, multiplier, bonus, referral

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private String status = "active";

    private String description;

    // Points config
    @Column(name = "points_per_rupee", precision = 8, scale = 2)
    private BigDecimal pointsPerRupee;

    @Column(name = "value_per_point", precision = 8, scale = 4)
    private BigDecimal valuePerPoint;

    @Column(name = "min_redeem_points")
    private Integer minRedeemPoints;

    @Column(name = "max_redeem_pct", precision = 5, scale = 2)
    private BigDecimal maxRedeemPct;

    @Column(name = "bonus_on_signup")
    private Integer bonusOnSignup;

    // Multiplier config
    private BigDecimal multiplier;

    @Column(name = "tier_required", length = 20)
    private String tierRequired;

    // Bonus config
    @Column(name = "bonus_points")
    private Integer bonusPoints;

    // Referral config
    @Column(name = "referrer_points")
    private Integer referrerPoints;

    @Column(name = "referee_points")
    private Integer refereePoints;

    @Column(name = "applicable_to", length = 50)
    private String applicableTo;  // all, services, products (CSV)
}
