package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class InventoryItemResponse {
    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private String name;
    private String brand;
    private String sku;
    private String description;
    private String unit;
    private Integer currentStock;
    private Integer minStockLevel;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private String supplier;
    private String barcode;
    private LocalDate expiryDate;
    private LocalDate lastRestocked;
    private InventoryStatus status;
    private Boolean isForRetail;
    private Instant createdAt;
}
