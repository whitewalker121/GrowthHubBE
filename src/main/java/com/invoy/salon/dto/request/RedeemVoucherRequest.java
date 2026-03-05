package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RedeemVoucherRequest {
    @NotBlank
    private String code;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    @NotNull
    private UUID invoiceId;
}
