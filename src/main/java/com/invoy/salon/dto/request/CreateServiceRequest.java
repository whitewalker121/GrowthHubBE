package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.ServiceStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateServiceRequest {
    @NotNull
    private UUID categoryId;
    @NotBlank
    @Size(max = 150)
    private String name;
    private String description;

    @NotNull
    @Min(5)
    private Integer durationMins;

    @NotNull
    @DecimalMin("0")
    private BigDecimal price;

    @DecimalMin("0")
    private BigDecimal mrp;

    @NotNull
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal gstPercent;

    private Boolean isPopular = false;
    private Integer sortOrder = 0;
    private ServiceStatus status = ServiceStatus.ACTIVE;
}
