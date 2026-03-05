package com.growthhub.salon.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BulkAttendanceRequest {
    @NotNull
    private LocalDate workDate;
    @NotEmpty
    @Valid
    private List<MarkAttendanceRequest> records;
}
