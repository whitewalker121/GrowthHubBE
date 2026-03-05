package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private String name;
    private String category;
    private String brand;
    private String sku;
    private Integer stock;
    private Integer minStock;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private String supplier;
    private LocalDate lastRestocked;
    private LocalDate expiryDate;
    private String status;
}
