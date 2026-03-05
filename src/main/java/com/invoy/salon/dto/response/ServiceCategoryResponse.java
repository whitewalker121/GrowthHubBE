package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ServiceCategoryResponse {
    private UUID id;
    private String name;
    private String icon;
    private String color;
    private Integer sortOrder;
    private Boolean isActive;
    private long serviceCount;
}
