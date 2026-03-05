package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceRequest {
    @NotNull Long clientId;
    Long staffId;
    LocalDate date;
    @NotEmpty List<InvoiceItemRequest> items;
    BigDecimal discount;
    String discountType;
    String paymentMethod;
    Integer loyaltyPointsToRedeem;
    String notes;
}
