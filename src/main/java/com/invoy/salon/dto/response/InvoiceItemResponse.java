package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InvoiceItemResponse {
    private UUID id;
    private UUID serviceId;
    private String itemName;
    private String itemType;
    private StaffSummary staff;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPct;
    private BigDecimal gstPercent;
    private BigDecimal gstAmount;
    private BigDecimal lineTotal;
}
