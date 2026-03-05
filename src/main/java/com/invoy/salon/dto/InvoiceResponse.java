package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long clientId;
    private String clientName;
    private Long staffId;
    private String staffName;
    private LocalDate date;
    private List<InvoiceItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private String discountType;
    private BigDecimal gstAmount;
    private BigDecimal total;
    private String paymentMethod;
    private String status;
    private Integer loyaltyPointsEarned;
    private Integer loyaltyPointsRedeemed;
    private String notes;
}
