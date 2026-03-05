package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InventoryRequest {
    @NotBlank String name;
    String category;
    String brand;
    String sku;
    @NotNull @Min(0) Integer stock;
    @NotNull @Min(0) Integer minStock;
    String unit;
    BigDecimal costPrice;
    BigDecimal sellingPrice;
    String supplier;
    LocalDate expiryDate;
}
