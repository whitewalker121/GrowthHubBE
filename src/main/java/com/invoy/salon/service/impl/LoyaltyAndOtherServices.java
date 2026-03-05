//package com.growthhub.salon.service.impl;
//
//import com.growthhub.salon.dto.request.*;
//import com.growthhub.salon.dto.response.*;
//import com.growthhub.salon.entity.*;
//import com.growthhub.salon.enums.*;
//import com.growthhub.salon.exception.*;
//import com.growthhub.salon.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.*;
//
//// ═══════════════════════════════════════════════════════════
//// LOYALTY SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class LoyaltyService {
//
//    private final LoyaltyProgramRepository programRepo;
//    private final LoyaltyTransactionRepository txRepo;
//    private final ClientRepository clientRepo;
//
//    public List<LoyaltyProgramResponse> listPrograms() {
//        return programRepo.findAll().stream().map(this::toProgramResponse).toList();
//    }
//
//    public LoyaltyProgramResponse getProgram(UUID id) {
//        return toProgramResponse(findProgramOrThrow(id));
//    }
//
//    @Transactional
//    public LoyaltyProgramResponse createProgram(CreateLoyaltyProgramRequest req) {
//        LoyaltyProgram p = LoyaltyProgram.builder()
//            .name(req.getName()).description(req.getDescription())
//            .programType(req.getProgramType()).status("ACTIVE")
//            .pointsPerRupee(req.getPointsPerRupee() != null ? req.getPointsPerRupee() : BigDecimal.ONE)
//            .valuePerPoint(req.getValuePerPoint() != null ? req.getValuePerPoint() : BigDecimal.valueOf(0.5))
//            .minRedeemPoints(req.getMinRedeemPoints() != null ? req.getMinRedeemPoints() : 100)
//            .maxRedeemPct(BigDecimal.valueOf(req.getMaxRedeemPct() != null ? req.getMaxRedeemPct() : 20))
//            .bonusOnSignup(req.getBonusOnSignup() != null ? req.getBonusOnSignup() : 0)
//            .multiplier(req.getMultiplier()).tierRequired(req.getTierRequired())
//            .bonusPoints(req.getBonusPoints()).referrerPoints(req.getReferrerPoints())
//            .refereePoints(req.getRefereePoints())
//            .applicableTo(req.getApplicableTo() != null ? req.getApplicableTo() : RedeemType.ALL)
//            .build();
//        return toProgramResponse(programRepo.save(p));
//    }
//
//    @Transactional
//    public LoyaltyProgramResponse toggleStatus(UUID id) {
//        LoyaltyProgram p = findProgramOrThrow(id);
//        p.setStatus(p.getStatus().equals("ACTIVE") ? "INACTIVE" : "ACTIVE");
//        return toProgramResponse(programRepo.save(p));
//    }
//
//    @Transactional
//    public LoyaltyTransactionResponse awardPoints(AwardPointsRequest req, String awardedBy) {
//        Client client = clientRepo.findById(req.getClientId())
//            .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()));
//
//        int newBalance = client.getLoyaltyPoints() + req.getPoints();
//        client.setLoyaltyPoints(newBalance);
//        clientRepo.save(client);
//
//        LoyaltyTransaction tx = LoyaltyTransaction.builder()
//            .client(client).transactionType(req.getTransactionType())
//            .points(req.getPoints()).balanceAfter(newBalance).notes(req.getNotes())
//            .build();
//
//        tx = txRepo.save(tx);
//        return LoyaltyTransactionResponse.builder()
//            .id(tx.getId()).transactionType(tx.getTransactionType())
//            .points(tx.getPoints()).balanceAfter(tx.getBalanceAfter())
//            .notes(tx.getNotes()).createdAt(tx.getCreatedAt()).build();
//    }
//
//    public PagedResponse<LoyaltyTransactionResponse> getClientTransactions(UUID clientId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<LoyaltyTransaction> result = txRepo.findByClientId(clientId, pageable);
//        return PagedResponse.<LoyaltyTransactionResponse>builder()
//            .content(result.getContent().stream().map(t ->
//                LoyaltyTransactionResponse.builder()
//                    .id(t.getId()).transactionType(t.getTransactionType())
//                    .points(t.getPoints()).balanceAfter(t.getBalanceAfter())
//                    .notes(t.getNotes()).createdAt(t.getCreatedAt()).build()).toList())
//            .page(result.getNumber()).size(result.getSize())
//            .totalElements(result.getTotalElements())
//            .totalPages(result.getTotalPages()).last(result.isLast())
//            .build();
//    }
//
//    private LoyaltyProgram findProgramOrThrow(UUID id) {
//        return programRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("LoyaltyProgram", id));
//    }
//
//    private LoyaltyProgramResponse toProgramResponse(LoyaltyProgram p) {
//        return LoyaltyProgramResponse.builder()
//            .id(p.getId()).name(p.getName()).description(p.getDescription())
//            .programType(p.getProgramType()).status(p.getStatus())
//            .pointsPerRupee(p.getPointsPerRupee()).valuePerPoint(p.getValuePerPoint())
//            .minRedeemPoints(p.getMinRedeemPoints()).maxRedeemPct(p.getMaxRedeemPct())
//            .bonusOnSignup(p.getBonusOnSignup()).multiplier(p.getMultiplier())
//            .tierRequired(p.getTierRequired()).bonusPoints(p.getBonusPoints())
//            .referrerPoints(p.getReferrerPoints()).refereePoints(p.getRefereePoints())
//            .applicableTo(p.getApplicableTo()).createdAt(p.getCreatedAt()).build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// MEMBERSHIP SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class MembershipService {
//
//    private final MembershipPackageRepository packageRepo;
//    private final ClientMembershipRepository enrolmentRepo;
//    private final ClientRepository clientRepo;
//    private final InvoiceRepository invoiceRepo;
//
//    public List<MembershipPackageResponse> listPackages() {
//        return packageRepo.findAll().stream().map(this::toPackageResponse).toList();
//    }
//
//    @Transactional
//    public MembershipPackageResponse createPackage(CreateMembershipPackageRequest req) {
//        MembershipPackage pkg = MembershipPackage.builder()
//            .name(req.getName()).description(req.getDescription())
//            .price(req.getPrice()).validityDays(req.getValidityDays())
//            .includedCount(req.getIncludedCount() != null ? req.getIncludedCount() : 0)
//            .bonusWallet(req.getBonusWallet() != null ? req.getBonusWallet() : BigDecimal.ZERO)
//            .discountPct(req.getDiscountPct() != null ? req.getDiscountPct() : BigDecimal.ZERO)
//            .colorHex(req.getColorHex() != null ? req.getColorHex() : "#c9a96e")
//            .status("ACTIVE")
//            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
//            .includedServices(req.getIncludedServices() != null ? req.getIncludedServices() : List.of())
//            .build();
//        return toPackageResponse(packageRepo.save(pkg));
//    }
//
//    @Transactional
//    public MembershipPackageResponse updatePackage(UUID id, CreateMembershipPackageRequest req) {
//        MembershipPackage pkg = packageRepo.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("MembershipPackage", id));
//
//        if (req.getName()           != null) pkg.setName(req.getName());
//        if (req.getDescription()    != null) pkg.setDescription(req.getDescription());
//        if (req.getPrice()          != null) pkg.setPrice(req.getPrice());
//        if (req.getValidityDays()   != null) pkg.setValidityDays(req.getValidityDays());
//        if (req.getIncludedCount()  != null) pkg.setIncludedCount(req.getIncludedCount());
//        if (req.getBonusWallet()    != null) pkg.setBonusWallet(req.getBonusWallet());
//        if (req.getDiscountPct()    != null) pkg.setDiscountPct(req.getDiscountPct());
//        if (req.getColorHex()       != null) pkg.setColorHex(req.getColorHex());
//        if (req.getIncludedServices() != null) pkg.setIncludedServices(req.getIncludedServices());
//        if (req.getSortOrder()      != null) pkg.setSortOrder(req.getSortOrder());
//
//        return toPackageResponse(packageRepo.save(pkg));
//    }
//
//    @Transactional
//    public ClientMembershipResponse enrol(EnrolMembershipRequest req) {
//        Client client = clientRepo.findById(req.getClientId())
//            .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()));
//        MembershipPackage pkg = packageRepo.findById(req.getPackageId())
//            .orElseThrow(() -> new ResourceNotFoundException("MembershipPackage", req.getPackageId()));
//
//        LocalDate start  = req.getStartDate() != null ? req.getStartDate() : LocalDate.now();
//        LocalDate expiry = start.plusDays(pkg.getValidityDays());
//
//        ClientMembership enrolment = ClientMembership.builder()
//            .client(client).membershipPackage(pkg)
//            .startDate(start).expiryDate(expiry)
//            .walletBalance(pkg.getBonusWallet())
//            .status(MembershipStatus.ACTIVE)
//            .build();
//
//        if (req.getInvoiceId() != null) {
//            invoiceRepo.findById(req.getInvoiceId()).ifPresent(enrolment::setInvoice);
//        }
//
//        return toEnrolmentResponse(enrolmentRepo.save(enrolment));
//    }
//
//    public List<ClientMembershipResponse> getClientMemberships(UUID clientId) {
//        return enrolmentRepo.findByClientIdAndStatus(clientId, MembershipStatus.ACTIVE)
//            .stream().map(this::toEnrolmentResponse).toList();
//    }
//
//    @Transactional
//    public ClientMembershipResponse cancel(UUID enrolmentId, String reason) {
//        ClientMembership enrolment = enrolmentRepo.findById(enrolmentId)
//            .orElseThrow(() -> new ResourceNotFoundException("ClientMembership", enrolmentId));
//        enrolment.setStatus(MembershipStatus.CANCELLED);
//        enrolment.setCancelledAt(java.time.Instant.now());
//        enrolment.setCancelReason(reason);
//        return toEnrolmentResponse(enrolmentRepo.save(enrolment));
//    }
//
//    private MembershipPackageResponse toPackageResponse(MembershipPackage p) {
//        long sold = enrolmentRepo.findByClientIdAndStatus(UUID.randomUUID(), MembershipStatus.ACTIVE).size(); // placeholder
//        return MembershipPackageResponse.builder()
//            .id(p.getId()).name(p.getName()).description(p.getDescription())
//            .price(p.getPrice()).validityDays(p.getValidityDays())
//            .includedCount(p.getIncludedCount()).bonusWallet(p.getBonusWallet())
//            .discountPct(p.getDiscountPct()).colorHex(p.getColorHex()).status(p.getStatus())
//            .includedServices(p.getIncludedServices()).sortOrder(p.getSortOrder())
//            .createdAt(p.getCreatedAt()).build();
//    }
//
//    private ClientMembershipResponse toEnrolmentResponse(ClientMembership e) {
//        Client c = e.getClient();
//        MembershipPackage p = e.getMembershipPackage();
//        return ClientMembershipResponse.builder()
//            .id(e.getId())
//            .client(ClientSummary.builder().id(c.getId()).name(c.getName()).phone(c.getPhone())
//                .avatarInitials(c.getAvatarInitials()).membershipType(c.getMembershipType())
//                .loyaltyPoints(c.getLoyaltyPoints()).walletBalance(c.getWalletBalance())
//                .lastVisitAt(c.getLastVisitAt()).build())
//            .packageId(p.getId()).packageName(p.getName())
//            .startDate(e.getStartDate()).expiryDate(e.getExpiryDate())
//            .servicesUsed(e.getServicesUsed()).servicesTotal(p.getIncludedCount())
//            .walletBalance(e.getWalletBalance()).status(e.getStatus())
//            .createdAt(e.getCreatedAt()).build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// GIFT VOUCHER SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class GiftVoucherService {
//
//    private final GiftVoucherRepository voucherRepo;
//    private final ClientRepository clientRepo;
//
//    public PagedResponse<GiftVoucherResponse> list(VoucherStatus status, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<GiftVoucher> result = (status != null)
//            ? voucherRepo.findByStatus(status, pageable)
//            : voucherRepo.findAll(pageable);
//        return PagedResponse.<GiftVoucherResponse>builder()
//            .content(result.getContent().stream().map(this::toResponse).toList())
//            .page(result.getNumber()).size(result.getSize())
//            .totalElements(result.getTotalElements())
//            .totalPages(result.getTotalPages()).last(result.isLast())
//            .build();
//    }
//
//    public GiftVoucherResponse getByCode(String code) {
//        return toResponse(voucherRepo.findByCode(code)
//            .orElseThrow(() -> new ResourceNotFoundException("Voucher not found: " + code)));
//    }
//
//    @Transactional
//    public GiftVoucherResponse issue(IssueVoucherRequest req) {
//        String code = "GIFT-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
//
//        GiftVoucher voucher = GiftVoucher.builder()
//            .code(code).faceValue(req.getFaceValue())
//            .remainingValue(req.getFaceValue())
//            .issueDate(LocalDate.now())
//            .expiryDate(LocalDate.now().plusDays(req.getValidityDays()))
//            .status(VoucherStatus.ACTIVE).notes(req.getNotes())
//            .issuedToName(req.getIssuedToName())
//            .build();
//
//        if (req.getIssuedToClientId() != null) {
//            clientRepo.findById(req.getIssuedToClientId()).ifPresent(voucher::setIssuedTo);
//        }
//
//        return toResponse(voucherRepo.save(voucher));
//    }
//
//    @Transactional
//    public GiftVoucherResponse redeem(RedeemVoucherRequest req) {
//        GiftVoucher voucher = voucherRepo.findByCode(req.getCode())
//            .orElseThrow(() -> new ResourceNotFoundException("Voucher not found: " + req.getCode()));
//
//        if (voucher.getStatus() == VoucherStatus.REDEEMED || voucher.getStatus() == VoucherStatus.EXPIRED) {
//            throw new BusinessException("Voucher is no longer valid");
//        }
//        if (req.getAmount().compareTo(voucher.getRemainingValue()) > 0) {
//            throw new BusinessException("Redeem amount exceeds voucher balance of ₹" + voucher.getRemainingValue());
//        }
//
//        BigDecimal newBalance = voucher.getRemainingValue().subtract(req.getAmount());
//        voucher.setRemainingValue(newBalance);
//        voucher.setStatus(newBalance.compareTo(BigDecimal.ZERO) == 0
//            ? VoucherStatus.REDEEMED : VoucherStatus.PARTIAL);
//
//        return toResponse(voucherRepo.save(voucher));
//    }
//
//    private GiftVoucherResponse toResponse(GiftVoucher v) {
//        return GiftVoucherResponse.builder()
//            .id(v.getId()).code(v.getCode()).faceValue(v.getFaceValue())
//            .remainingValue(v.getRemainingValue())
//            .usedValue(v.getFaceValue().subtract(v.getRemainingValue()))
//            .issuedToId(v.getIssuedTo() != null ? v.getIssuedTo().getId() : null)
//            .issuedToName(v.getIssuedTo() != null ? v.getIssuedTo().getName() : v.getIssuedToName())
//            .issueDate(v.getIssueDate()).expiryDate(v.getExpiryDate())
//            .status(v.getStatus()).notes(v.getNotes()).createdAt(v.getCreatedAt())
//            .build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// REPORT SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ReportService {
//
//    private final InvoiceRepository invoiceRepo;
//    private final AppointmentRepository apptRepo;
//    private final ClientRepository clientRepo;
//    private final ExpenseRepository expenseRepo;
//
//    public DashboardResponse getDashboard() {
//        LocalDate today = LocalDate.now();
//        LocalDate monthStart = today.withDayOfMonth(1);
//        LocalDate lastMonthStart = monthStart.minusMonths(1);
//        LocalDate lastMonthEnd   = monthStart.minusDays(1);
//
//        BigDecimal todayRevenue = invoiceRepo.sumRevenueByDateRange(today, today).orElse(BigDecimal.ZERO);
//        long todayAppts   = apptRepo.countActiveByDate(today);
//        long newClients   = clientRepo.countNewClients(monthStart, today);
//        BigDecimal monthRev = invoiceRepo.sumRevenueByDateRange(monthStart, today).orElse(BigDecimal.ZERO);
//        BigDecimal lastMonthRev = invoiceRepo.sumRevenueByDateRange(lastMonthStart, lastMonthEnd).orElse(BigDecimal.ZERO);
//
//        double growthPct = lastMonthRev.compareTo(BigDecimal.ZERO) > 0
//            ? monthRev.subtract(lastMonthRev).divide(lastMonthRev, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100 : 0;
//
//        // Revenue for last 7 days
//        List<DashboardResponse.RevenuePoint> chart = new ArrayList<>();
//        for (int i = 6; i >= 0; i--) {
//            LocalDate d = today.minusDays(i);
//            BigDecimal rev = invoiceRepo.sumRevenueByDateRange(d, d).orElse(BigDecimal.ZERO);
//            long cnt = invoiceRepo.countPaidByDate(d);
//            chart.add(DashboardResponse.RevenuePoint.builder()
//                .label(d.toString()).revenue(rev).appointmentCount(cnt).build());
//        }
//
//        // Payment methods breakdown
//        List<Object[]> pmRows = invoiceRepo.revenueByPaymentMethod(monthStart, today);
//        List<DashboardResponse.PaymentMethodBreakdown> pmBreakdown = pmRows.stream()
//            .map(r -> DashboardResponse.PaymentMethodBreakdown.builder()
//                .method(r[0].toString())
//                .amount((BigDecimal) r[1])
//                .percentage(monthRev.compareTo(BigDecimal.ZERO) > 0
//                    ? ((BigDecimal) r[1]).divide(monthRev, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100 : 0)
//                .build())
//            .toList();
//
//        return DashboardResponse.builder()
//            .todayRevenue(todayRevenue).todayAppointments(todayAppts)
//            .pendingAppointments(apptRepo.findByAppointmentDateAndStatusIn(today,
//                List.of(AppointmentStatus.CONFIRMED, AppointmentStatus.PENDING)).size())
//            .newClientsThisMonth(newClients).monthRevenue(monthRev)
//            .monthGrowthPct(growthPct)
//            .revenueChart(chart).paymentMethods(pmBreakdown)
//            .build();
//    }
//
//    public ReportResponse getReport(ReportRequest req) {
//        BigDecimal totalRevenue = invoiceRepo.sumRevenueByDateRange(req.getFromDate(), req.getToDate())
//            .orElse(BigDecimal.ZERO);
//        BigDecimal totalExpenses = expenseRepo.sumApprovedByDateRange(req.getFromDate(), req.getToDate())
//            .orElse(BigDecimal.ZERO);
//        long newClients = clientRepo.countNewClients(req.getFromDate(), req.getToDate());
//
//        List<DashboardResponse.RevenuePoint> daily = new ArrayList<>();
//        for (LocalDate d = req.getFromDate(); !d.isAfter(req.getToDate()); d = d.plusDays(1)) {
//            BigDecimal rev = invoiceRepo.sumRevenueByDateRange(d, d).orElse(BigDecimal.ZERO);
//            long cnt = invoiceRepo.countPaidByDate(d);
//            daily.add(DashboardResponse.RevenuePoint.builder()
//                .label(d.toString()).revenue(rev).appointmentCount(cnt).build());
//        }
//
//        List<Object[]> expRows = expenseRepo.expensesByCategory(req.getFromDate(), req.getToDate());
//        List<ExpenseSummaryResponse.CategoryBreakdown> expBreakdown = expRows.stream()
//            .map(r -> ExpenseSummaryResponse.CategoryBreakdown.builder()
//                .category((String) r[0]).amount((BigDecimal) r[1])
//                .percentage(totalExpenses.compareTo(BigDecimal.ZERO) > 0
//                    ? ((BigDecimal) r[1]).divide(totalExpenses, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100 : 0)
//                .build())
//            .toList();
//
//        return ReportResponse.builder()
//            .fromDate(req.getFromDate()).toDate(req.getToDate())
//            .totalRevenue(totalRevenue).newClients(newClients)
//            .dailyRevenue(daily).expensesByCategory(expBreakdown)
//            .totalExpenses(totalExpenses)
//            .netProfit(totalRevenue.subtract(totalExpenses))
//            .build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// SETTINGS SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class SettingsService {
//
//    private final SalonSettingsRepository settingsRepo;
//
//    public SettingsResponse get() {
//        SalonSettings s = settingsRepo.findAll().stream().findFirst()
//            .orElseThrow(() -> new ResourceNotFoundException("Salon settings not initialised"));
//        return toResponse(s);
//    }
//
//    @Transactional
//    public SettingsResponse update(UpdateSettingsRequest req) {
//        SalonSettings s = settingsRepo.findAll().stream().findFirst()
//            .orElseThrow(() -> new ResourceNotFoundException("Salon settings not initialised"));
//
//        if (req.getSalonName()          != null) s.setSalonName(req.getSalonName());
//        if (req.getTagline()            != null) s.setTagline(req.getTagline());
//        if (req.getPhone()              != null) s.setPhone(req.getPhone());
//        if (req.getEmail()              != null) s.setEmail(req.getEmail());
//        if (req.getAddress()            != null) s.setAddress(req.getAddress());
//        if (req.getGstNumber()          != null) s.setGstNumber(req.getGstNumber());
//        if (req.getTimezone()           != null) s.setTimezone(req.getTimezone());
//        if (req.getLogoUrl()            != null) s.setLogoUrl(req.getLogoUrl());
//        if (req.getWorkingStart()       != null) s.setWorkingStart(req.getWorkingStart());
//        if (req.getWorkingEnd()         != null) s.setWorkingEnd(req.getWorkingEnd());
//        if (req.getSlotDurationMinutes()!= null) s.setSlotDurationMinutes(req.getSlotDurationMinutes());
//        if (req.getAdvanceBookingDays() != null) s.setAdvanceBookingDays(req.getAdvanceBookingDays());
//        if (req.getCancellationHours()  != null) s.setCancellationHours(req.getCancellationHours());
//        if (req.getAutoConfirm()        != null) s.setAutoConfirm(req.getAutoConfirm());
//        if (req.getReminderSms()        != null) s.setReminderSms(req.getReminderSms());
//        if (req.getReminderHours()      != null) s.setReminderHours(req.getReminderHours());
//        if (req.getDefaultGstPercent()  != null) s.setDefaultGstPercent(req.getDefaultGstPercent());
//        if (req.getInvoicePrefix()      != null) s.setInvoicePrefix(req.getInvoicePrefix());
//        if (req.getShowGstBreakdown()   != null) s.setShowGstBreakdown(req.getShowGstBreakdown());
//        if (req.getRoundOffTotal()      != null) s.setRoundOffTotal(req.getRoundOffTotal());
//        if (req.getDefaultDiscountPct() != null) s.setDefaultDiscountPct(req.getDefaultDiscountPct());
//        if (req.getSmsOnBooking()       != null) s.setSmsOnBooking(req.getSmsOnBooking());
//        if (req.getSmsOnReminder()      != null) s.setSmsOnReminder(req.getSmsOnReminder());
//        if (req.getEmailReceipt()       != null) s.setEmailReceipt(req.getEmailReceipt());
//        if (req.getLowStockAlert()      != null) s.setLowStockAlert(req.getLowStockAlert());
//        if (req.getDailySummary()       != null) s.setDailySummary(req.getDailySummary());
//
//        return toResponse(settingsRepo.save(s));
//    }
//
//    private SettingsResponse toResponse(SalonSettings s) {
//        return SettingsResponse.builder()
//            .id(s.getId()).salonName(s.getSalonName()).tagline(s.getTagline())
//            .phone(s.getPhone()).email(s.getEmail()).address(s.getAddress())
//            .gstNumber(s.getGstNumber()).currencyCode(s.getCurrencyCode())
//            .timezone(s.getTimezone()).logoUrl(s.getLogoUrl())
//            .workingStart(s.getWorkingStart()).workingEnd(s.getWorkingEnd())
//            .slotDurationMinutes(s.getSlotDurationMinutes())
//            .advanceBookingDays(s.getAdvanceBookingDays())
//            .cancellationHours(s.getCancellationHours()).autoConfirm(s.getAutoConfirm())
//            .reminderSms(s.getReminderSms()).reminderHours(s.getReminderHours())
//            .defaultGstPercent(s.getDefaultGstPercent()).invoicePrefix(s.getInvoicePrefix())
//            .showGstBreakdown(s.getShowGstBreakdown()).roundOffTotal(s.getRoundOffTotal())
//            .defaultDiscountPct(s.getDefaultDiscountPct()).smsOnBooking(s.getSmsOnBooking())
//            .smsOnReminder(s.getSmsOnReminder()).emailReceipt(s.getEmailReceipt())
//            .lowStockAlert(s.getLowStockAlert()).dailySummary(s.getDailySummary())
//            .updatedAt(s.getUpdatedAt())
//            .build();
//    }
//}
