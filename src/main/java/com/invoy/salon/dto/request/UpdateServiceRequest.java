package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.ServiceStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateServiceRequest {
    private UUID categoryId;
    @Size(max = 150)
    private String name;
    private String description;
    @Min(5)
    private Integer durationMins;
    @DecimalMin("0")
    private BigDecimal price;
    @DecimalMin("0")
    private BigDecimal mrp;
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal gstPercent;
    private Boolean isPopular;
    private Integer sortOrder;
    private ServiceStatus status;
}
