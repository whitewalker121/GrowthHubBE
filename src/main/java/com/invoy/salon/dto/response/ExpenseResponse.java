package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.ExpenseStatus;
import com.growthhub.salon.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ExpenseResponse {
    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private PaymentMethod paidBy;
    private String receiptUrl;
    private Boolean hasReceipt;
    private String notes;
    private ExpenseStatus status;
    private String approvedBy;
    private Instant approvedAt;
    private Instant createdAt;
}
