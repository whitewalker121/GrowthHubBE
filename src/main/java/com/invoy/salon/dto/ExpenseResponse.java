package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private String category;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String paidBy;
    private Boolean hasReceipt;
    private String status;
    private String notes;
}
