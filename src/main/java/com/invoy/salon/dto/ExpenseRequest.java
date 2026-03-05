package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    @NotBlank String category;
    @NotBlank String description;
    @NotNull @DecimalMin("0.01") BigDecimal amount;
    @NotNull LocalDate date;
    String paidBy;
    Boolean hasReceipt;
    String notes;
}
