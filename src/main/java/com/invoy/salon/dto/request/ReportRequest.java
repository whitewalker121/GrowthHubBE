package com.growthhub.salon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ReportRequest {
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
    private UUID staffId;       // optional filter
    private UUID categoryId;    // optional filter
}
