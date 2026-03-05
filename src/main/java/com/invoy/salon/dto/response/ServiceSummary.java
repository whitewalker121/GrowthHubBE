package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ServiceSummary {
    private UUID id;
    private String name;
    private Integer durationMins;
    private BigDecimal price;
}
