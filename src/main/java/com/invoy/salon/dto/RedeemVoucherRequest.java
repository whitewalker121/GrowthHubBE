package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RedeemVoucherRequest {
    @NotBlank String code;
    @NotNull @DecimalMin("0.01") BigDecimal amount;
    Long invoiceId;
}
