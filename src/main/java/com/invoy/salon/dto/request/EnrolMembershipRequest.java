package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EnrolMembershipRequest {
    @NotNull
    private UUID clientId;
    @NotNull
    private UUID packageId;
    private UUID invoiceId;         // existing invoice if pre-paid
    private LocalDate startDate;    // defaults to today
}
