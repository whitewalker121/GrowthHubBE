package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.ExpenseStatus;
import com.growthhub.salon.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateExpenseRequest {
    private UUID categoryId;
    @Size(max = 300)
    private String description;
    @DecimalMin("0.01")
    private BigDecimal amount;
    private LocalDate expenseDate;
    private PaymentMethod paidBy;
    private String notes;
    private Boolean hasReceipt;
    private ExpenseStatus status;
}
