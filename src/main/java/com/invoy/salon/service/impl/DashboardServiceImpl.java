package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.DashboardStats;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class DashboardServiceImpl {

    private final InvoiceRepository invoiceRepo;
    private final AppointmentRepository apptRepo;
    private final AttendanceRepository attendanceRepo;
    private final ClientRepository clientRepo;

    public DashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate lastMonthStart = monthStart.minusMonths(1);
        LocalDate lastMonthEnd = monthStart.minusDays(1);

        BigDecimal todayRevenue = invoiceRepo.sumRevenueByDate(today);
        BigDecimal monthRevenue = invoiceRepo.sumRevenueBetween(monthStart, today);
        BigDecimal lastMonthRevenue = invoiceRepo.sumRevenueBetween(lastMonthStart, lastMonthEnd);

        double growthPct = lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0
            ? monthRevenue.subtract(lastMonthRevenue).doubleValue() / lastMonthRevenue.doubleValue() * 100
            : 0;

        long todayAppts   = apptRepo.countByDate(today);
        long pendingAppts = apptRepo.findByDateAndStatus(today, "upcoming").size();
        long presentStaff = attendanceRepo.countPresentByDate(today);
        long totalClients = clientRepo.count();

        long paidInvoices = invoiceRepo.findByDateBetween(monthStart, today).size();
        BigDecimal avgTicket = paidInvoices > 0
            ? monthRevenue.divide(BigDecimal.valueOf(paidInvoices), 2, java.math.BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;

        // Revenue trend (last 7 months)
        List<Map<String, Object>> chart = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate mStart = today.withDayOfMonth(1).minusMonths(i);
            LocalDate mEnd   = mStart.withDayOfMonth(mStart.lengthOfMonth());
            BigDecimal rev   = invoiceRepo.sumRevenueBetween(mStart, mEnd);
            chart.add(Map.of("month", mStart.getMonth().toString().substring(0, 3), "revenue", rev));
        }

        return DashboardStats.builder()
            .todayRevenue(todayRevenue).todayAppointments(todayAppts)
            .presentStaff(presentStaff).pendingAppointments(pendingAppts)
            .monthRevenue(monthRevenue).monthGrowthPct(Math.round(growthPct * 10.0) / 10.0)
            .avgTicket(avgTicket).totalActiveClients(totalClients)
            .revenueChart(chart)
            .build();
    }
}
