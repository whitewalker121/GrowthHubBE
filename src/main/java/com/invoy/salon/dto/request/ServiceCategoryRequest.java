package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceCategoryRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    @Size(max = 10)
    private String icon;
    @Size(max = 7)
    private String color;
    private Integer sortOrder = 0;
    private Boolean isActive = true;
}
