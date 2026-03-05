package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.InventoryStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateInventoryItemRequest {
    private UUID categoryId;
    @Size(max = 200)
    private String name;
    @Size(max = 100)
    private String brand;
    @Size(max = 50)
    private String sku;
    private String description;
    @Size(max = 20)
    private String unit;
    @Min(0)
    private Integer minStockLevel;
    @DecimalMin("0")
    private BigDecimal costPrice;
    @DecimalMin("0")
    private BigDecimal sellingPrice;
    @DecimalMin("0")
    private BigDecimal mrp;
    @Size(max = 150)
    private String supplier;
    private LocalDate expiryDate;
    private Boolean isForRetail;
    private InventoryStatus status;
}
