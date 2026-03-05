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
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.*;
//
//// ═══════════════════════════════════════════════════════════
//// INVOICE SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class InvoiceService {
//
//    private final InvoiceRepository invoiceRepo;
//    private final ClientRepository clientRepo;
//    private final AppointmentRepository apptRepo;
//    private final ServiceRepository serviceRepo;
//    private final StaffRepository staffRepo;
//    private final GiftVoucherRepository voucherRepo;
//    private final LoyaltyProgramRepository loyaltyProgramRepo;
//    private final LoyaltyTransactionRepository loyaltyTxRepo;
//
//    public PagedResponse<InvoiceResponse> list(UUID clientId, LocalDate from, LocalDate to, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDate"));
//
//        Page<Invoice> result;
//        if (clientId != null) {
//            result = invoiceRepo.findByClientIdOrderByInvoiceDateDesc(clientId, pageable);
//        } else {
//            result = invoiceRepo.findAll(pageable);
//        }
//
//        return PagedResponse.<InvoiceResponse>builder()
//            .content(result.getContent().stream().map(this::toResponse).toList())
//            .page(result.getNumber()).size(result.getSize())
//            .totalElements(result.getTotalElements())
//            .totalPages(result.getTotalPages()).last(result.isLast())
//            .build();
//    }
//
//    public InvoiceResponse getById(UUID id) {
//        return toResponse(findOrThrow(id));
//    }
//
//    public InvoiceResponse getByNumber(String number) {
//        Invoice inv = invoiceRepo.findByInvoiceNumber(number)
//            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + number));
//        return toResponse(inv);
//    }
//
//    @Transactional
//    public InvoiceResponse create(CreateInvoiceRequest req) {
//        Client client = clientRepo.findById(req.getClientId())
//            .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()));
//
//        Invoice invoice = Invoice.builder()
//            .invoiceNumber(generateInvoiceNumber())
//            .client(client)
//            .invoiceDate(LocalDate.now())
//            .paymentMethod(req.getPaymentMethod())
//            .notes(req.getNotes())
//            .build();
//
//        // Build line items and accumulate totals
//        BigDecimal subtotal = BigDecimal.ZERO;
//        List<InvoiceItem> items = new ArrayList<>();
//
//        for (InvoiceItemRequest itemReq : req.getItems()) {
//            BigDecimal baseTotal = itemReq.getUnitPrice()
//                .multiply(BigDecimal.valueOf(itemReq.getQuantity()));
//            BigDecimal discountAmt = baseTotal.multiply(
//                itemReq.getDiscountPct().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
//            BigDecimal afterDiscount = baseTotal.subtract(discountAmt);
//            BigDecimal gstAmt = afterDiscount.multiply(
//                itemReq.getGstPercent().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
//            BigDecimal lineTotal = afterDiscount.add(gstAmt);
//
//            InvoiceItem item = InvoiceItem.builder()
//                .invoice(invoice)
//                .itemName(itemReq.getItemName())
//                .itemType(itemReq.getItemType() != null ? itemReq.getItemType() : "SERVICE")
//                .quantity(itemReq.getQuantity())
//                .unitPrice(itemReq.getUnitPrice())
//                .discountPct(itemReq.getDiscountPct())
//                .gstPercent(itemReq.getGstPercent())
//                .gstAmount(gstAmt.setScale(2, BigDecimal.ROUND_HALF_UP))
//                .lineTotal(lineTotal.setScale(2, BigDecimal.ROUND_HALF_UP))
//                .build();
//
//            if (itemReq.getServiceId() != null) {
//                serviceRepo.findById(itemReq.getServiceId()).ifPresent(item::setService);
//            }
//            if (itemReq.getStaffId() != null) {
//                staffRepo.findById(itemReq.getStaffId()).ifPresent(item::setStaff);
//            }
//
//            items.add(item);
//            subtotal = subtotal.add(afterDiscount);
//        }
//
//        invoice.setItems(items);
//
//        // Apply invoice-level discount
//        BigDecimal invDiscount = subtotal.multiply(
//            req.getDiscountPercent().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
//        subtotal = subtotal.subtract(invDiscount);
//
//        // GST (recalculate from items)
//        BigDecimal totalGst = items.stream()
//            .map(InvoiceItem::getGstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal totalAmount = subtotal.add(totalGst);
//
//        invoice.setSubtotal(subtotal.setScale(2, BigDecimal.ROUND_HALF_UP));
//        invoice.setDiscountAmount(invDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
//        invoice.setDiscountPercent(req.getDiscountPercent());
//        invoice.setGstAmount(totalGst.setScale(2, BigDecimal.ROUND_HALF_UP));
//        invoice.setTotalAmount(totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
//        invoice.setAmountPaid(totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));  // default fully paid
//        invoice.setStatus(InvoiceStatus.PAID);
//
//        // Apply gift voucher
//        if (req.getVoucherCode() != null && !req.getVoucherCode().isBlank()) {
//            GiftVoucher voucher = voucherRepo.findByCode(req.getVoucherCode())
//                .orElseThrow(() -> new ResourceNotFoundException("Gift voucher not found: " + req.getVoucherCode()));
//            if (voucher.getStatus() == VoucherStatus.REDEEMED || voucher.getRemainingValue().compareTo(BigDecimal.ZERO) <= 0) {
//                throw new BusinessException("Voucher has already been fully redeemed");
//            }
//            invoice.setVoucher(voucher);
//        }
//
//        // Loyalty points earned (1 point per ₹1 paid by default)
//        LoyaltyProgram program = loyaltyProgramRepo.findByStatus("ACTIVE").stream().findFirst().orElse(null);
//        if (program != null) {
//            int pointsEarned = totalAmount.intValue();  // simplified: 1 point = 1 rupee
//            int pointsRedeemed = req.getPointsToRedeem() != null ? req.getPointsToRedeem() : 0;
//
//            if (pointsRedeemed > 0 && pointsRedeemed > client.getLoyaltyPoints()) {
//                throw new BusinessException("Insufficient loyalty points. Available: " + client.getLoyaltyPoints());
//            }
//
//            invoice.setPointsEarned(pointsEarned);
//            invoice.setPointsRedeemed(pointsRedeemed);
//
//            // Update client points
//            client.setLoyaltyPoints(client.getLoyaltyPoints() + pointsEarned - pointsRedeemed);
//        }
//
//        // Update client stats
//        client.setTotalVisits(client.getTotalVisits() + 1);
//        client.setTotalSpend(client.getTotalSpend().add(totalAmount));
//        client.setLastVisitAt(LocalDate.now());
//        clientRepo.save(client);
//
//        return toResponse(invoiceRepo.save(invoice));
//    }
//
//    @Transactional
//    public InvoiceResponse updateStatus(UUID id, UpdateInvoiceStatusRequest req) {
//        Invoice inv = findOrThrow(id);
//        inv.setStatus(req.getStatus());
//        if (req.getAmountPaid() != null) {
//            inv.setAmountPaid(req.getAmountPaid());
//            if (req.getAmountPaid().compareTo(inv.getTotalAmount()) >= 0) {
//                inv.setStatus(InvoiceStatus.PAID);
//            } else if (req.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
//                inv.setStatus(InvoiceStatus.PARTIAL);
//            }
//        }
//        if (req.getPaymentMethod() != null) inv.setPaymentMethod(req.getPaymentMethod());
//        return toResponse(invoiceRepo.save(inv));
//    }
//
//    private String generateInvoiceNumber() {
//        String prefix = "INV-" + LocalDate.now().getYear();
//        int seq = invoiceRepo.nextSequence(prefix);
//        return prefix + "-" + String.format("%04d", seq);
//    }
//
//    private Invoice findOrThrow(UUID id) {
//        return invoiceRepo.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
//    }
//
//    private InvoiceResponse toResponse(Invoice inv) {
//        List<InvoiceItemResponse> itemResponses = inv.getItems() == null ? List.of()
//            : inv.getItems().stream().map(i -> InvoiceItemResponse.builder()
//                .id(i.getId()).serviceId(i.getService() != null ? i.getService().getId() : null)
//                .itemName(i.getItemName()).itemType(i.getItemType())
//                .staff(i.getStaff() != null ? StaffSummary.builder()
//                    .id(i.getStaff().getId()).fullName(i.getStaff().getFullName())
//                    .role(i.getStaff().getRole()).avatarInitials(i.getStaff().getAvatarInitials())
//                    .status(i.getStaff().getStatus()).build() : null)
//                .quantity(i.getQuantity()).unitPrice(i.getUnitPrice())
//                .discountPct(i.getDiscountPct()).gstPercent(i.getGstPercent())
//                .gstAmount(i.getGstAmount()).lineTotal(i.getLineTotal())
//                .build()).toList();
//
//        Client c = inv.getClient();
//        return InvoiceResponse.builder()
//            .id(inv.getId()).invoiceNumber(inv.getInvoiceNumber())
//            .client(ClientSummary.builder().id(c.getId()).name(c.getName()).phone(c.getPhone())
//                .avatarInitials(c.getAvatarInitials()).membershipType(c.getMembershipType())
//                .loyaltyPoints(c.getLoyaltyPoints()).walletBalance(c.getWalletBalance())
//                .lastVisitAt(c.getLastVisitAt()).build())
//            .appointmentId(inv.getAppointment() != null ? inv.getAppointment().getId() : null)
//            .invoiceDate(inv.getInvoiceDate()).items(itemResponses)
//            .subtotal(inv.getSubtotal()).discountAmount(inv.getDiscountAmount())
//            .discountPercent(inv.getDiscountPercent()).gstAmount(inv.getGstAmount())
//            .totalAmount(inv.getTotalAmount()).amountPaid(inv.getAmountPaid())
//            .amountDue(inv.getTotalAmount().subtract(inv.getAmountPaid()))
//            .paymentMethod(inv.getPaymentMethod()).status(inv.getStatus())
//            .notes(inv.getNotes()).pointsEarned(inv.getPointsEarned())
//            .pointsRedeemed(inv.getPointsRedeemed()).createdAt(inv.getCreatedAt())
//            .build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// INVENTORY SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class InventoryService {
//
//    private final InventoryItemRepository itemRepo;
//    private final StockMovementRepository movementRepo;
//
//    public PagedResponse<InventoryItemResponse> list(UUID categoryId, String status, String search, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
//        Page<InventoryItem> result;
//
//        if (search != null && !search.isBlank()) {
//            result = itemRepo.search(search, pageable);
//        } else if (categoryId != null) {
//            result = itemRepo.findByCategoryId(categoryId, pageable);
//        } else {
//            result = itemRepo.findAll(pageable);
//        }
//
//        return PagedResponse.<InventoryItemResponse>builder()
//            .content(result.getContent().stream().map(this::toResponse).toList())
//            .page(result.getNumber()).size(result.getSize())
//            .totalElements(result.getTotalElements())
//            .totalPages(result.getTotalPages()).last(result.isLast())
//            .build();
//    }
//
//    public InventoryItemResponse getById(UUID id) {
//        return toResponse(findOrThrow(id));
//    }
//
//    public List<InventoryItemResponse> getLowStock() {
//        return itemRepo.findLowStockItems().stream().map(this::toResponse).toList();
//    }
//
//    @Transactional
//    public InventoryItemResponse create(CreateInventoryItemRequest req) {
//        if (req.getSku() != null && itemRepo.findBySku(req.getSku()).isPresent()) {
//            throw new DuplicateResourceException("SKU already exists: " + req.getSku());
//        }
//
//        InventoryItem item = InventoryItem.builder()
//            .name(req.getName()).brand(req.getBrand()).sku(req.getSku())
//            .description(req.getDescription()).unit(req.getUnit())
//            .currentStock(req.getCurrentStock()).minStockLevel(req.getMinStockLevel())
//            .costPrice(req.getCostPrice()).sellingPrice(req.getSellingPrice())
//            .mrp(req.getMrp()).supplier(req.getSupplier()).barcode(req.getBarcode())
//            .expiryDate(req.getExpiryDate())
//            .isForRetail(req.getIsForRetail() != null ? req.getIsForRetail() : false)
//            .build();
//
//        return toResponse(itemRepo.save(item));
//    }
//
//    @Transactional
//    public InventoryItemResponse update(UUID id, UpdateInventoryItemRequest req) {
//        InventoryItem item = findOrThrow(id);
//
//        if (req.getName()          != null) item.setName(req.getName());
//        if (req.getBrand()         != null) item.setBrand(req.getBrand());
//        if (req.getSku()           != null) item.setSku(req.getSku());
//        if (req.getDescription()   != null) item.setDescription(req.getDescription());
//        if (req.getUnit()          != null) item.setUnit(req.getUnit());
//        if (req.getMinStockLevel() != null) item.setMinStockLevel(req.getMinStockLevel());
//        if (req.getCostPrice()     != null) item.setCostPrice(req.getCostPrice());
//        if (req.getSellingPrice()  != null) item.setSellingPrice(req.getSellingPrice());
//        if (req.getMrp()           != null) item.setMrp(req.getMrp());
//        if (req.getSupplier()      != null) item.setSupplier(req.getSupplier());
//        if (req.getExpiryDate()    != null) item.setExpiryDate(req.getExpiryDate());
//        if (req.getIsForRetail()   != null) item.setIsForRetail(req.getIsForRetail());
//        if (req.getStatus()        != null) item.setStatus(req.getStatus());
//
//        return toResponse(itemRepo.save(item));
//    }
//
//    @Transactional
//    public InventoryItemResponse adjustStock(StockAdjustmentRequest req) {
//        InventoryItem item = findOrThrow(req.getItemId());
//        int before = item.getCurrentStock();
//        int after;
//
//        switch (req.getMovementType().toUpperCase()) {
//            case "IN"          -> after = before + req.getQuantity();
//            case "OUT", "WASTE"-> after = before - req.getQuantity();
//            case "ADJUSTMENT"  -> after = req.getQuantity();  // set absolute
//            default            -> throw new BusinessException("Unknown movement type: " + req.getMovementType());
//        }
//
//        if (after < 0) throw new InsufficientStockException(item.getName(), req.getQuantity(), before);
//
//        StockMovement movement = StockMovement.builder()
//            .item(item).movementType(req.getMovementType().toUpperCase())
//            .quantity(req.getQuantity()).beforeStock(before).afterStock(after)
//            .notes(req.getNotes())
//            .build();
//        movementRepo.save(movement);
//
//        item.setCurrentStock(after);
//        if (req.getMovementType().equalsIgnoreCase("IN")) {
//            item.setLastRestocked(LocalDate.now());
//        }
//        return toResponse(itemRepo.save(item));
//    }
//
//    public List<StockMovementResponse> getMovements(UUID itemId) {
//        return movementRepo.findByItemIdOrderByCreatedAtDesc(itemId).stream()
//            .map(m -> StockMovementResponse.builder()
//                .id(m.getId()).movementType(m.getMovementType())
//                .quantity(m.getQuantity()).beforeStock(m.getBeforeStock())
//                .afterStock(m.getAfterStock()).referenceType(m.getReferenceType())
//                .notes(m.getNotes()).createdAt(m.getCreatedAt()).build())
//            .toList();
//    }
//
//    private InventoryItem findOrThrow(UUID id) {
//        return itemRepo.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", id));
//    }
//
//    private InventoryItemResponse toResponse(InventoryItem i) {
//        return InventoryItemResponse.builder()
//            .id(i.getId())
//            .categoryId(i.getCategory() != null ? i.getCategory().getId() : null)
//            .categoryName(i.getCategory() != null ? i.getCategory().getName() : null)
//            .name(i.getName()).brand(i.getBrand()).sku(i.getSku())
//            .description(i.getDescription()).unit(i.getUnit())
//            .currentStock(i.getCurrentStock()).minStockLevel(i.getMinStockLevel())
//            .costPrice(i.getCostPrice()).sellingPrice(i.getSellingPrice()).mrp(i.getMrp())
//            .supplier(i.getSupplier()).barcode(i.getBarcode())
//            .expiryDate(i.getExpiryDate()).lastRestocked(i.getLastRestocked())
//            .status(i.getStatus()).isForRetail(i.getIsForRetail()).createdAt(i.getCreatedAt())
//            .build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// EXPENSE SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ExpenseService {
//
//    private final ExpenseRepository expenseRepo;
//    private final ExpenseCategoryRepository categoryRepo;
//    private final UserRepository userRepo;
//
//    public PagedResponse<ExpenseResponse> list(UUID categoryId, LocalDate from, LocalDate to, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "expenseDate"));
//
//        if (from == null) from = LocalDate.now().withDayOfMonth(1);
//        if (to   == null) to   = LocalDate.now();
//
//        Page<Expense> result = (categoryId != null)
//            ? expenseRepo.findByCategoryIdAndExpenseDateBetween(categoryId, from, to, pageable)
//            : expenseRepo.findByExpenseDateBetweenOrderByExpenseDateDesc(from, to, pageable);
//
//        return PagedResponse.<ExpenseResponse>builder()
//            .content(result.getContent().stream().map(this::toResponse).toList())
//            .page(result.getNumber()).size(result.getSize())
//            .totalElements(result.getTotalElements())
//            .totalPages(result.getTotalPages()).last(result.isLast())
//            .build();
//    }
//
//    public ExpenseResponse getById(UUID id) {
//        return toResponse(findOrThrow(id));
//    }
//
//    public ExpenseSummaryResponse getSummary(LocalDate from, LocalDate to) {
//        if (from == null) from = LocalDate.now().withDayOfMonth(1);
//        if (to   == null) to   = LocalDate.now();
//
//        BigDecimal total = expenseRepo.sumApprovedByDateRange(from, to).orElse(BigDecimal.ZERO);
//        List<Object[]> rows = expenseRepo.expensesByCategory(from, to);
//
//        List<ExpenseSummaryResponse.CategoryBreakdown> breakdowns = rows.stream()
//            .map(r -> ExpenseSummaryResponse.CategoryBreakdown.builder()
//                .category((String) r[0])
//                .amount((BigDecimal) r[1])
//                .percentage(total.compareTo(BigDecimal.ZERO) > 0
//                    ? ((BigDecimal) r[1]).divide(total, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100 : 0)
//                .build())
//            .toList();
//
//        return ExpenseSummaryResponse.builder()
//            .totalApproved(total).totalPending(BigDecimal.ZERO)
//            .byCategory(breakdowns).build();
//    }
//
//    @Transactional
//    public ExpenseResponse create(CreateExpenseRequest req) {
//        Expense expense = Expense.builder()
//            .description(req.getDescription()).amount(req.getAmount())
//            .expenseDate(req.getExpenseDate()).paidBy(req.getPaidBy())
//            .notes(req.getNotes())
//            .hasReceipt(req.getHasReceipt() != null ? req.getHasReceipt() : false)
//            .status(ExpenseStatus.PENDING)
//            .build();
//
//        if (req.getCategoryId() != null) {
//            categoryRepo.findById(req.getCategoryId()).ifPresent(expense::setCategory);
//        }
//
//        return toResponse(expenseRepo.save(expense));
//    }
//
//    @Transactional
//    public ExpenseResponse update(UUID id, UpdateExpenseRequest req) {
//        Expense expense = findOrThrow(id);
//
//        if (req.getCategoryId()   != null) categoryRepo.findById(req.getCategoryId()).ifPresent(expense::setCategory);
//        if (req.getDescription()  != null) expense.setDescription(req.getDescription());
//        if (req.getAmount()       != null) expense.setAmount(req.getAmount());
//        if (req.getExpenseDate()  != null) expense.setExpenseDate(req.getExpenseDate());
//        if (req.getPaidBy()       != null) expense.setPaidBy(req.getPaidBy());
//        if (req.getNotes()        != null) expense.setNotes(req.getNotes());
//        if (req.getHasReceipt()   != null) expense.setHasReceipt(req.getHasReceipt());
//        if (req.getStatus()       != null) expense.setStatus(req.getStatus());
//
//        return toResponse(expenseRepo.save(expense));
//    }
//
//    @Transactional
//    public ExpenseResponse approve(UUID id, String approverEmail) {
//        Expense expense = findOrThrow(id);
//        if (expense.getStatus() != ExpenseStatus.PENDING) {
//            throw new BusinessException("Only PENDING expenses can be approved");
//        }
//        expense.setStatus(ExpenseStatus.APPROVED);
//        expense.setApprovedAt(java.time.Instant.now());
//        userRepo.findByEmail(approverEmail).ifPresent(expense::setApprovedBy);
//        return toResponse(expenseRepo.save(expense));
//    }
//
//    @Transactional
//    public void delete(UUID id) {
//        expenseRepo.deleteById(id);
//    }
//
//    private Expense findOrThrow(UUID id) {
//        return expenseRepo.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
//    }
//
//    private ExpenseResponse toResponse(Expense e) {
//        return ExpenseResponse.builder()
//            .id(e.getId())
//            .categoryId(e.getCategory() != null ? e.getCategory().getId() : null)
//            .categoryName(e.getCategory() != null ? e.getCategory().getName() : null)
//            .description(e.getDescription()).amount(e.getAmount())
//            .expenseDate(e.getExpenseDate()).paidBy(e.getPaidBy())
//            .receiptUrl(e.getReceiptUrl()).hasReceipt(e.getHasReceipt())
//            .notes(e.getNotes()).status(e.getStatus())
//            .approvedBy(e.getApprovedBy() != null ? e.getApprovedBy().getFullName() : null)
//            .approvedAt(e.getApprovedAt()).createdAt(e.getCreatedAt())
//            .build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// ATTENDANCE SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class AttendanceService {
//
//    private final AttendanceRepository attendanceRepo;
//    private final StaffRepository staffRepo;
//    private final StaffService staffService;
//
//    public AttendanceSummaryResponse getByDate(LocalDate date) {
//        if (date == null) date = LocalDate.now();
//
//        List<Attendance> records = attendanceRepo.findByWorkDate(date);
//
//        long present  = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
//        long absent   = records.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
//        long late     = records.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
//        long halfDay  = records.stream().filter(a -> a.getStatus() == AttendanceStatus.HALF_DAY).count();
//        BigDecimal totalHours = records.stream().map(Attendance::getHoursWorked)
//            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return AttendanceSummaryResponse.builder()
//            .date(date).presentCount((int) present).absentCount((int) absent)
//            .lateCount((int) late).halfDayCount((int) halfDay).totalHours(totalHours)
//            .records(records.stream().map(this::toResponse).toList())
//            .build();
//    }
//
//    public List<AttendanceResponse> getByStaff(UUID staffId, LocalDate from, LocalDate to) {
//        if (from == null) from = LocalDate.now().withDayOfMonth(1);
//        if (to   == null) to   = LocalDate.now();
//        return attendanceRepo.findByStaffIdAndWorkDateBetweenOrderByWorkDateAsc(staffId, from, to)
//            .stream().map(this::toResponse).toList();
//    }
//
//    @Transactional
//    public AttendanceResponse mark(MarkAttendanceRequest req, String markedByEmail) {
//        Staff staff = staffRepo.findById(req.getStaffId())
//            .orElseThrow(() -> new ResourceNotFoundException("Staff", req.getStaffId()));
//
//        // Find or create
//        Attendance attendance = attendanceRepo
//            .findByStaffIdAndWorkDate(staff.getId(), req.getWorkDate())
//            .orElseGet(() -> Attendance.builder().staff(staff).workDate(req.getWorkDate()).build());
//
//        attendance.setCheckIn(req.getCheckIn());
//        attendance.setCheckOut(req.getCheckOut());
//        attendance.setStatus(req.getStatus());
//        attendance.setNotes(req.getNotes());
//
//        // Auto-calculate hours worked
//        if (req.getCheckIn() != null && req.getCheckOut() != null) {
//            long minutes = java.time.Duration.between(req.getCheckIn(), req.getCheckOut()).toMinutes();
//            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
//            attendance.setHoursWorked(hours);
//            BigDecimal standard = BigDecimal.valueOf(9);  // 9-hour shift
//            attendance.setOvertimeHours(hours.compareTo(standard) > 0 ? hours.subtract(standard) : BigDecimal.ZERO);
//        } else {
//            attendance.setHoursWorked(BigDecimal.ZERO);
//            attendance.setOvertimeHours(BigDecimal.ZERO);
//        }
//
//        return toResponse(attendanceRepo.save(attendance));
//    }
//
//    @Transactional
//    public List<AttendanceResponse> markBulk(BulkAttendanceRequest req, String markedByEmail) {
//        return req.getRecords().stream()
//            .map(r -> mark(r, markedByEmail))
//            .toList();
//    }
//
//    private AttendanceResponse toResponse(Attendance a) {
//        return AttendanceResponse.builder()
//            .id(a.getId()).staff(staffService.toSummary(a.getStaff()))
//            .workDate(a.getWorkDate()).checkIn(a.getCheckIn()).checkOut(a.getCheckOut())
//            .hoursWorked(a.getHoursWorked()).overtimeHours(a.getOvertimeHours())
//            .status(a.getStatus()).notes(a.getNotes()).createdAt(a.getCreatedAt())
//            .build();
//    }
//}
