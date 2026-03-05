package com.growthhub.salon.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class UpdateSettingsRequest {
    private String salonName;
    private String tagline;
    private String phone;
    private String email;
    private String address;
    private String gstNumber;
    private String timezone;
    private String logoUrl;
    private LocalTime workingStart;
    private LocalTime workingEnd;
    private Integer slotDurationMinutes;
    private Integer advanceBookingDays;
    private Integer cancellationHours;
    private Boolean autoConfirm;
    private Boolean reminderSms;
    private Integer reminderHours;
    private BigDecimal defaultGstPercent;
    private String invoicePrefix;
    private Boolean showGstBreakdown;
    private Boolean roundOffTotal;
    private BigDecimal defaultDiscountPct;
    private Boolean smsOnBooking;
    private Boolean smsOnReminder;
    private Boolean emailReceipt;
    private Boolean lowStockAlert;
    private Boolean dailySummary;
}
