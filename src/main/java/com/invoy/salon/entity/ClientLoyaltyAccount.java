package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "client_loyalty_accounts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientLoyaltyAccount extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @Column(name = "points_balance", columnDefinition = "INT DEFAULT 0")
    private Integer pointsBalance = 0;

    @Column(name = "total_points_earned", columnDefinition = "INT DEFAULT 0")
    private Integer totalPointsEarned = 0;

    @Column(name = "total_points_redeemed", columnDefinition = "INT DEFAULT 0")
    private Integer totalPointsRedeemed = 0;

    @Column(name = "tier", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'Basic'")
    private String tier = "Basic";

    @Column(name = "join_date")
    private LocalDate joinDate;
}
