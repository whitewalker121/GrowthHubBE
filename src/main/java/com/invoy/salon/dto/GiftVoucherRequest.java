package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GiftVoucherRequest {
    Long clientId;
    String issuedToName;
    @NotNull @DecimalMin("1.0") BigDecimal value;
    @NotNull @Min(1) Integer validityDays;
    String occasion;
}
