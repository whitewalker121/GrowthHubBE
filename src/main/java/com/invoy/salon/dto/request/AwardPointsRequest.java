package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AwardPointsRequest {
    @NotNull
    private UUID clientId;
    @NotNull
    @Min(1)
    private Integer points;
    private String notes;
    private String transactionType = "BONUS";  // BONUS | ADJUST
}
