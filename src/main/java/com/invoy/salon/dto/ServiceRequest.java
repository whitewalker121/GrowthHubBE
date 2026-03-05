package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceRequest {
    @NotNull Long categoryId;
    @NotBlank String name;
    String description;
    @NotNull @Min(1) Integer duration;
    @NotNull @DecimalMin("0.0") BigDecimal price;
    BigDecimal mrp;
    BigDecimal gstRate;
    String status;
    Boolean popular;
}
