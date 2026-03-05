package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class IssueVoucherRequest {
    private UUID issuedToClientId;
    private String issuedToName;    // for anonymous

    @NotNull
    @DecimalMin("1")
    private BigDecimal faceValue;

    @NotNull
    @Min(1)
    private Integer validityDays;

    private String notes;
}
