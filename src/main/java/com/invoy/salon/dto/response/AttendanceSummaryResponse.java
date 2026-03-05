package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AttendanceSummaryResponse {
    private LocalDate date;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    private int halfDayCount;
    private BigDecimal totalHours;
    private List<AttendanceResponse> records;
}
