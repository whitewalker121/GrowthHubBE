package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.InvoiceStatus;
import com.growthhub.salon.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InvoiceResponse {
    private UUID id;
    private String invoiceNumber;
    private ClientSummary client;
    private UUID appointmentId;
    private LocalDate invoiceDate;
    private List<InvoiceItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal amountDue;
    private PaymentMethod paymentMethod;
    private InvoiceStatus status;
    private String notes;
    private Integer pointsEarned;
    private Integer pointsRedeemed;
    private Instant createdAt;
}
