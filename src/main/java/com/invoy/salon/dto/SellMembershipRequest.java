package com.growthhub.salon.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SellMembershipRequest {
    @NotNull Long clientId;
    @NotNull Long packageId;
    Long invoiceId;
}
