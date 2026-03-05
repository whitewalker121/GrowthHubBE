package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateMembershipPackageRequest {
    @NotBlank
    @Size(max = 150)
    private String name;
    private String description;
    @NotNull
    @DecimalMin("0")
    private BigDecimal price;
    @NotNull
    @Min(1)
    private Integer validityDays;
    @Min(0)
    private Integer includedCount = 0;
    @DecimalMin("0")
    private BigDecimal bonusWallet = BigDecimal.ZERO;
    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal discountPct = BigDecimal.ZERO;
    @Size(max = 7)
    private String colorHex = "#c9a96e";
    private List<String> includedServices;
    private Integer sortOrder = 0;
}
