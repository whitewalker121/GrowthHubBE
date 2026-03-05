package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceItemResponse {
    private Long id;
    private String itemName;
    private String itemType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal gstRate;
    private BigDecimal lineTotal;
    private Long serviceId;
    private Long staffId;
    private String staffName;
}
