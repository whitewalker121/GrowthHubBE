package com.growthhub.salon.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MembershipPackageRequest {
    private String name;
    private BigDecimal price;
    private Integer validity;
    private Integer includedServiceCount;
    private BigDecimal bonusWallet;
    private BigDecimal discountPct;
    private String color;
    private List<String> services;
    private String status;
}
