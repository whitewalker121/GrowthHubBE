package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal todayRevenue;
    private long todayAppointments;
    private long pendingAppointments;
    private long newClientsThisMonth;
    private BigDecimal monthRevenue;
    private double monthGrowthPct;
    private BigDecimal avgTicketValue;
    private List<RevenuePoint> revenueChart;
    private List<PaymentMethodBreakdown> paymentMethods;
    private List<TopServiceStat> topServices;
    private List<TopStaffStat> topStaff;
    private List<AppointmentResponse> todayAppointmentList;

    @Data
    @Builder
    public static class RevenuePoint {
        private String label;
        private BigDecimal revenue;
        private long appointmentCount;
    }

    @Data
    @Builder
    public static class PaymentMethodBreakdown {
        private String method;
        private BigDecimal amount;
        private double percentage;
    }

    @Data
    @Builder
    public static class TopServiceStat {
        private String serviceName;
        private long count;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class TopStaffStat {
        private String staffName;
        private long appointments;
        private BigDecimal revenue;
        private BigDecimal rating;
    }
}
