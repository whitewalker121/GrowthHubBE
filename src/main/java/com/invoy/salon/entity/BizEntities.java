///*
//package com.growthhub.salon.entity;
//
//import com.growthhub.salon.enums.*;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.*;
//import java.util.*;
//
//// ─────────────────────────────────────────────────────────────
//// INVOICE
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "invoices")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class Invoice extends BaseEntity {
//
//    @Column(name = "invoice_number", nullable = false, unique = true, length = 30)
//    private String invoiceNumber;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "client_id", nullable = false)
//    private Client client;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "appointment_id")
//    private Appointment appointment;
//
//    @Column(name = "invoice_date", nullable = false)
//    private LocalDate invoiceDate = LocalDate.now();
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal subtotal = BigDecimal.ZERO;
//
//    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
//    private BigDecimal discountAmount = BigDecimal.ZERO;
//
//    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
//    private BigDecimal discountPercent = BigDecimal.ZERO;
//
//    @Column(name = "gst_amount", nullable = false, precision = 10, scale = 2)
//    private BigDecimal gstAmount = BigDecimal.ZERO;
//
//    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
//    private BigDecimal totalAmount = BigDecimal.ZERO;
//
//    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
//    private BigDecimal amountPaid = BigDecimal.ZERO;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "payment_method", nullable = false)
//    private PaymentMethod paymentMethod = PaymentMethod.CASH;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private InvoiceStatus status = InvoiceStatus.DRAFT;
//
//    @Column(columnDefinition = "TEXT")
//    private String notes;
//
//    @Column(name = "points_earned", nullable = false)
//    private Integer pointsEarned = 0;
//
//    @Column(name = "points_redeemed", nullable = false)
//    private Integer pointsRedeemed = 0;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "voucher_id")
//    private GiftVoucher voucher;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "membership_id")
//    private ClientMembership membership;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;
//
//    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<InvoiceItem> items = new ArrayList<>();
//}
//
//// ─────────────────────────────────────────────────────────────
//// INVOICE ITEM
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "invoice_items")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class InvoiceItem extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "invoice_id", nullable = false)
//    private Invoice invoice;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "service_id")
//    private com.growthhub.salon.entity.Service service;
//
//    @Column(name = "item_name", nullable = false, length = 150)
//    private String itemName;
//
//    @Column(name = "item_type", nullable = false, length = 20)
//    private String itemType = "SERVICE";
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "staff_id")
//    private Staff staff;
//
//    @Column(nullable = false)
//    private Integer quantity = 1;
//
//    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
//    private BigDecimal unitPrice;
//
//    @Column(name = "discount_pct", nullable = false, precision = 5, scale = 2)
//    private BigDecimal discountPct = BigDecimal.ZERO;
//
//    @Column(name = "gst_percent", nullable = false, precision = 5, scale = 2)
//    private BigDecimal gstPercent = BigDecimal.valueOf(18);
//
//    @Column(name = "gst_amount", nullable = false, precision = 10, scale = 2)
//    private BigDecimal gstAmount = BigDecimal.ZERO;
//
//    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
//    private BigDecimal lineTotal;
//}
//
//// ─────────────────────────────────────────────────────────────
//// INVENTORY ITEM
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "inventory_items")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class InventoryItem extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    private InventoryCategory category;
//
//    @Column(nullable = false, length = 200)
//    private String name;
//
//    @Column(length = 100)
//    private String brand;
//
//    @Column(unique = true, length = 50)
//    private String sku;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @Column(nullable = false, length = 20)
//    private String unit = "piece";
//
//    @Column(name = "current_stock", nullable = false)
//    private Integer currentStock = 0;
//
//    @Column(name = "min_stock_level", nullable = false)
//    private Integer minStockLevel = 5;
//
//    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
//    private BigDecimal costPrice = BigDecimal.ZERO;
//
//    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
//    private BigDecimal sellingPrice = BigDecimal.ZERO;
//
//    @Column(precision = 10, scale = 2)
//    private BigDecimal mrp;
//
//    @Column(length = 150)
//    private String supplier;
//
//    @Column(unique = true, length = 50)
//    private String barcode;
//
//    @Column(name = "expiry_date")
//    private LocalDate expiryDate;
//
//    @Column(name = "last_restocked")
//    private LocalDate lastRestocked;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private InventoryStatus status = InventoryStatus.IN_STOCK;
//
//    @Column(name = "is_for_retail", nullable = false)
//    private Boolean isForRetail = false;
//}
//
//// ─────────────────────────────────────────────────────────────
//// INVENTORY CATEGORY
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "inventory_categories")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class InventoryCategory {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @Column(nullable = false, unique = true, length = 100)
//    private String name;
//
//    @Column(name = "is_active", nullable = false)
//    private Boolean isActive = true;
//}
//
//// ─────────────────────────────────────────────────────────────
//// EXPENSE CATEGORY
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "expense_categories")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class ExpenseCategory {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @Column(nullable = false, unique = true, length = 100)
//    private String name;
//}
//
//// ─────────────────────────────────────────────────────────────
//// EXPENSE
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "expenses")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class Expense extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    private ExpenseCategory category;
//
//    @Column(nullable = false, length = 300)
//    private String description;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal amount;
//
//    @Column(name = "expense_date", nullable = false)
//    private LocalDate expenseDate = LocalDate.now();
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "paid_by", nullable = false)
//    private PaymentMethod paidBy = PaymentMethod.CASH;
//
//    @Column(name = "receipt_url", columnDefinition = "TEXT")
//    private String receiptUrl;
//
//    @Column(name = "has_receipt", nullable = false)
//    private Boolean hasReceipt = false;
//
//    @Column(columnDefinition = "TEXT")
//    private String notes;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ExpenseStatus status = ExpenseStatus.PENDING;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "approved_by")
//    private User approvedBy;
//
//    @Column(name = "approved_at")
//    private Instant approvedAt;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;
//}
//
//// ─────────────────────────────────────────────────────────────
//// ATTENDANCE
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "attendance",
//    uniqueConstraints = @UniqueConstraint(columnNames = {"staff_id","work_date"}))
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class Attendance extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "staff_id", nullable = false)
//    private Staff staff;
//
//    @Column(name = "work_date", nullable = false)
//    private LocalDate workDate;
//
//    @Column(name = "check_in")
//    private LocalTime checkIn;
//
//    @Column(name = "check_out")
//    private LocalTime checkOut;
//
//    @Column(name = "hours_worked", nullable = false, precision = 4, scale = 2)
//    private BigDecimal hoursWorked = BigDecimal.ZERO;
//
//    @Column(name = "overtime_hours", nullable = false, precision = 4, scale = 2)
//    private BigDecimal overtimeHours = BigDecimal.ZERO;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private AttendanceStatus status = AttendanceStatus.PRESENT;
//
//    @Column(columnDefinition = "TEXT")
//    private String notes;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "marked_by")
//    private User markedBy;
//}
//
//// ─────────────────────────────────────────────────────────────
//// LOYALTY PROGRAM
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "loyalty_programs")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public
//class LoyaltyProgram extends BaseEntity {
//
//    @Column(nullable = false, length = 150)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "program_type", nullable = false)
//    private LoyaltyProgramType programType = LoyaltyProgramType.POINTS;
//
//    @Column(nullable = false, length = 10)
//    private String status = "ACTIVE";
//
//    @Column(name = "points_per_rupee", nullable = false, precision = 5, scale = 2)
//    private BigDecimal pointsPerRupee = BigDecimal.ONE;
//
//    @Column(name = "value_per_point", nullable = false, precision = 5, scale = 2)
//    private BigDecimal valuePerPoint = BigDecimal.valueOf(0.5);
//
//    @Column(name = "min_redeem_points", nullable = false)
//    private Integer minRedeemPoints = 100;
//
//    @Column(name = "max_redeem_pct", nullable = false, precision = 5, scale = 2)
//    private BigDecimal maxRedeemPct = BigDecimal.valueOf(20);
//
//    @Column(name = "bonus_on_signup", nullable = false)
//    private Integer bonusOnSignup = 0;
//
//    @Column(precision = 4, scale = 2)
//    private BigDecimal multiplier;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "tier_required")
//    private ClientTier tierRequired;
//
//    @Column(name = "bonus_points")
//    private Integer bonusPoints;
//
//    @Column(name = "referrer_points")
//    private Integer referrerPoints;
//
//    @Column(name = "referee_points")
//    private Integer refereePoints;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "applicable_to", nullable = false)
//    private RedeemType applicableTo = RedeemType.ALL;
//}
//
//// ─────────────────────────────────────────────────────────────
//// LOYALTY TRANSACTION
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "loyalty_transactions")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class LoyaltyTransaction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "client_id", nullable = false)
//    private Client client;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "program_id")
//    private LoyaltyProgram program;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "invoice_id")
//    private Invoice invoice;
//
//    @Column(name = "transaction_type", nullable = false, length = 20)
//    private String transactionType;  // EARN | REDEEM | BONUS | EXPIRE | ADJUST
//
//    @Column(nullable = false)
//    private Integer points;
//
//    @Column(name = "balance_after", nullable = false)
//    private Integer balanceAfter;
//
//    @Column(columnDefinition = "TEXT")
//    private String notes;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private Instant createdAt = Instant.now();
//}
//
//// ─────────────────────────────────────────────────────────────
//// MEMBERSHIP PACKAGE
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "membership_packages")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class MembershipPackage extends BaseEntity {
//
//    @Column(nullable = false, length = 150)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal price;
//
//    @Column(name = "validity_days", nullable = false)
//    private Integer validityDays = 90;
//
//    @Column(name = "included_count", nullable = false)
//    private Integer includedCount = 0;
//
//    @Column(name = "bonus_wallet", nullable = false, precision = 10, scale = 2)
//    private BigDecimal bonusWallet = BigDecimal.ZERO;
//
//    @Column(name = "discount_pct", nullable = false, precision = 5, scale = 2)
//    private BigDecimal discountPct = BigDecimal.ZERO;
//
//    @Column(name = "color_hex", nullable = false, length = 7)
//    private String colorHex = "#c9a96e";
//
//    @Column(nullable = false, length = 10)
//    private String status = "ACTIVE";
//
//    @Column(name = "sort_order", nullable = false)
//    private Integer sortOrder = 0;
//
//    @ElementCollection
//    @CollectionTable(name = "membership_package_services",
//            joinColumns = @JoinColumn(name = "package_id"))
//    @Column(name = "service_name")
//    private List<String> includedServices = new ArrayList<>();
//}
//
//// ─────────────────────────────────────────────────────────────
//// CLIENT MEMBERSHIP
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "client_memberships")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class ClientMembership extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "client_id", nullable = false)
//    private Client client;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "package_id", nullable = false)
//    private MembershipPackage membershipPackage;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "invoice_id")
//    private Invoice invoice;
//
//    @Column(name = "start_date", nullable = false)
//    private LocalDate startDate = LocalDate.now();
//
//    @Column(name = "expiry_date", nullable = false)
//    private LocalDate expiryDate;
//
//    @Column(name = "services_used", nullable = false)
//    private Integer servicesUsed = 0;
//
//    @Column(name = "wallet_balance", nullable = false, precision = 10, scale = 2)
//    private BigDecimal walletBalance = BigDecimal.ZERO;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private MembershipStatus status = MembershipStatus.ACTIVE;
//
//    @Column(name = "cancelled_at")
//    private Instant cancelledAt;
//
//    @Column(name = "cancel_reason")
//    private String cancelReason;
//}
//
//// ─────────────────────────────────────────────────────────────
//// GIFT VOUCHER
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "gift_vouchers")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class GiftVoucher extends BaseEntity {
//
//    @Column(nullable = false, unique = true, length = 30)
//    private String code;
//
//    @Column(name = "face_value", nullable = false, precision = 10, scale = 2)
//    private BigDecimal faceValue;
//
//    @Column(name = "remaining_value", nullable = false, precision = 10, scale = 2)
//    private BigDecimal remainingValue;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "issued_to_id")
//    private Client issuedTo;
//
//    @Column(name = "issued_to_name", length = 150)
//    private String issuedToName;
//
//    @Column(name = "issue_date", nullable = false)
//    private LocalDate issueDate = LocalDate.now();
//
//    @Column(name = "expiry_date", nullable = false)
//    private LocalDate expiryDate;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private VoucherStatus status = VoucherStatus.ACTIVE;
//
//    @Column(columnDefinition = "TEXT")
//    private String notes;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;
//}
//*/
