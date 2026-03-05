package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceItemRequest {
    @NotBlank String itemName;
    String itemType;
    @Min(1) Integer quantity;
    @NotNull @DecimalMin("0.0") BigDecimal unitPrice;
    BigDecimal gstRate;
    Long serviceId;
    Long inventoryItemId;
    Long staffId;
}
