package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalRevenue;
    private long totalInvoices;
    private long totalAppointments;
    private long newClients;
    private BigDecimal avgTicket;
    private List<DashboardResponse.RevenuePoint> dailyRevenue;
    private List<DashboardResponse.TopServiceStat> topServices;
    private List<DashboardResponse.TopStaffStat> topStaff;
    private List<DashboardResponse.PaymentMethodBreakdown> paymentMethods;
    private List<ExpenseSummaryResponse.CategoryBreakdown> expensesByCategory;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
}
