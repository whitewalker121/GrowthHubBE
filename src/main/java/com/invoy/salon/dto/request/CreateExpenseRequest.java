package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateExpenseRequest {
    private UUID categoryId;

    @NotBlank
    @Size(max = 300)
    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private LocalDate expenseDate;
    @NotNull
    private PaymentMethod paidBy;

    private String notes;
    private Boolean hasReceipt = false;
}
