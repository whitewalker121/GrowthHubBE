package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Column(nullable = false)
    private LocalDate date;

    @Column(precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 14, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "discount_type", length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'flat'")
    private String discountType = "flat";  // flat or percent

    @Column(name = "gst_amount", precision = 14, scale = 2)
    private BigDecimal gstAmount;

    @Column(precision = 14, scale = 2)
    private BigDecimal total;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;  // cash, card, upi, wallet, mixed

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'paid'")
    private String status = "paid";  // paid, pending, cancelled

    @Column(name = "loyalty_points_earned", columnDefinition = "INT DEFAULT 0")
    private Integer loyaltyPointsEarned = 0;

    @Column(name = "loyalty_points_redeemed", columnDefinition = "INT DEFAULT 0")
    private Integer loyaltyPointsRedeemed = 0;

    private String notes;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items;
}
