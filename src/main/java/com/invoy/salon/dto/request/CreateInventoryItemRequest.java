package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateInventoryItemRequest {
    private UUID categoryId;
    @NotBlank
    @Size(max = 200)
    private String name;
    @Size(max = 100)
    private String brand;
    @Size(max = 50)
    private String sku;
    private String description;
    @NotBlank
    @Size(max = 20)
    private String unit;

    @NotNull
    @Min(0)
    private Integer currentStock;
    @NotNull
    @Min(0)
    private Integer minStockLevel;
    @NotNull
    @DecimalMin("0")
    private BigDecimal costPrice;
    @DecimalMin("0")
    private BigDecimal sellingPrice = BigDecimal.ZERO;
    @DecimalMin("0")
    private BigDecimal mrp;

    @Size(max = 150)
    private String supplier;
    @Size(max = 50)
    private String barcode;
    private LocalDate expiryDate;
    private Boolean isForRetail = false;
}
