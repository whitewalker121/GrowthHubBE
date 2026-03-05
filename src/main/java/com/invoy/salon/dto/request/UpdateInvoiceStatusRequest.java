package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.InvoiceStatus;
import com.growthhub.salon.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateInvoiceStatusRequest {
    @NotNull
    private InvoiceStatus status;
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
}
