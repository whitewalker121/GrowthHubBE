package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InvoiceItemRequest {
    private UUID serviceId;         // null for ad-hoc items
    @NotBlank
    private String itemName;
    private String itemType = "SERVICE";
    private UUID staffId;

    @NotNull
    @Min(1)
    private Integer quantity;
    @NotNull
    @DecimalMin("0")
    private BigDecimal unitPrice;
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal discountPct = BigDecimal.ZERO;
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal gstPercent = BigDecimal.valueOf(18);
}
