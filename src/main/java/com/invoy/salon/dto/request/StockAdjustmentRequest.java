package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StockAdjustmentRequest {
    @NotNull
    private UUID itemId;
    @NotBlank
    private String movementType;   // IN | OUT | ADJUSTMENT | WASTE
    @NotNull
    @Min(1)
    private Integer quantity;
    private String notes;
}
