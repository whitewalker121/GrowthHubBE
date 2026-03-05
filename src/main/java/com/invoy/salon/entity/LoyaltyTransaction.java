package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_transactions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoyaltyTransaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "transaction_type", length = 20, nullable = false)
    private String transactionType;  // earned, redeemed, bonus, expired, manual

    @Column(name = "points", nullable = false)
    private Integer points;

    private String description;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
}
