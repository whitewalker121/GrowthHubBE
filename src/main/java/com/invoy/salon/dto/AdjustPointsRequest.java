package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AdjustPointsRequest {
    @NotNull Long clientId;
    @NotNull Integer points;   // positive = add, negative = deduct
    @NotBlank String reason;
}
