package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.VoucherStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class GiftVoucherResponse {
    private UUID id;
    private String code;
    private BigDecimal faceValue;
    private BigDecimal remainingValue;
    private BigDecimal usedValue;
    private UUID issuedToId;
    private String issuedToName;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private VoucherStatus status;
    private String notes;
    private Instant createdAt;
}
