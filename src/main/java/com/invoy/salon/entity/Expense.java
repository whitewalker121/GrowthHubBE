package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Expense extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "paid_by", length = 30)
    private String paidBy;  // cash, upi, card, bank_transfer

    @Column(name = "has_receipt", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean hasReceipt = false;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'pending'")
    private String status = "pending";  // pending, approved, rejected

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_staff_id")
    private Staff approvedBy;

    private String notes;
}
