package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardStats {
    private BigDecimal todayRevenue;
    private Long todayAppointments;
    private Long presentStaff;
    private Long pendingAppointments;
    private BigDecimal monthRevenue;
    private Double monthGrowthPct;
    private BigDecimal avgTicket;
    private Long totalActiveClients;
    private List<Map<String, Object>> revenueChart;
    private List<Map<String, Object>> topServices;
}
