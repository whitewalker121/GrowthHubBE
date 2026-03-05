package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CreateInvoiceRequest {
    @NotNull
    private UUID clientId;
    private UUID appointmentId;

    @NotEmpty
    @Valid
    private List<InvoiceItemRequest> items;

    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @NotNull
    private PaymentMethod paymentMethod;

    private Integer pointsToRedeem = 0;
    private String voucherCode;
    private UUID membershipId;
    private String notes;
}
