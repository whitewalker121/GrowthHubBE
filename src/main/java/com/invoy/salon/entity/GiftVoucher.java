package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gift_vouchers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GiftVoucher extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "issued_to_name", length = 100)
    private String issuedToName;

    @Column(name = "original_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalValue;

    @Column(name = "remaining_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingValue;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private String status = "active";  // active, partial, redeemed, expired

    private String occasion;
}
