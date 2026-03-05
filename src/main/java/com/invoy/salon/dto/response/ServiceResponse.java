package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.ServiceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ServiceResponse {
    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private String name;
    private String description;
    private Integer durationMins;
    private BigDecimal price;
    private BigDecimal mrp;
    private BigDecimal gstPercent;
    private ServiceStatus status;
    private Boolean isPopular;
    private Integer sortOrder;
    private Instant createdAt;
}
