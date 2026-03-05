package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ServiceResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private Integer duration;
    private BigDecimal price;
    private BigDecimal mrp;
    private BigDecimal gstRate;
    private String status;
    private Boolean popular;
}
