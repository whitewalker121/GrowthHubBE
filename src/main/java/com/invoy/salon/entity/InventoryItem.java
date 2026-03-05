package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;
    private String brand;

    @Column(unique = true)
    private String sku;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 0;

    private String unit;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "selling_price", precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    private String supplier;

    @Column(name = "last_restocked")
    private LocalDate lastRestocked;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(length = 20)
    private String status = "in-stock";  // in-stock, low-stock, out-of-stock
}
