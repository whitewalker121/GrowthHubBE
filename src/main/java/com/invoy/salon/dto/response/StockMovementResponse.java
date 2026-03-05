package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class StockMovementResponse {
    private UUID id;
    private String movementType;
    private Integer quantity;
    private Integer beforeStock;
    private Integer afterStock;
    private String referenceType;
    private String notes;
    private String performedBy;
    private Instant createdAt;
}
