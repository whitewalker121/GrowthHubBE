package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.*;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class InvoiceServiceImpl {

    private final InvoiceRepository invoiceRepo;
    private final ClientRepository clientRepo;
    private final StaffRepository staffRepo;
    private final ClientLoyaltyAccountRepository loyaltyAccountRepo;
    private final LoyaltyTransactionRepository loyaltyTxRepo;

    public InvoiceResponse create(InvoiceRequest req) {
        Client client = clientRepo.findById(req.getClientId())
            .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()));
        Staff staff = req.getStaffId() != null
            ? staffRepo.findById(req.getStaffId()).orElseThrow(() -> new ResourceNotFoundException("Staff", req.getStaffId()))
            : null;

        // Build line items
        List<InvoiceItem> lineItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        for (InvoiceItemRequest ir : req.getItems()) {
            BigDecimal gstRate = ir.getGstRate() != null ? ir.getGstRate() : BigDecimal.valueOf(18);
            BigDecimal qty = BigDecimal.valueOf(ir.getQuantity() != null ? ir.getQuantity() : 1);
            BigDecimal lineBase = ir.getUnitPrice().multiply(qty);
            BigDecimal lineGst  = lineBase.multiply(gstRate).divide(BigDecimal.valueOf(100), 2,BigDecimal.ROUND_HALF_UP);
            BigDecimal lineTotal = lineBase.add(lineGst);
            subtotal = subtotal.add(lineBase);
            totalGst = totalGst.add(lineGst);

            Staff lineStaff = ir.getStaffId() != null
                ? staffRepo.findById(ir.getStaffId()).orElse(null) : null;

            lineItems.add(InvoiceItem.builder()
                .itemName(ir.getItemName()).itemType(ir.getItemType())
                .quantity(ir.getQuantity() != null ? ir.getQuantity() : 1)
                .unitPrice(ir.getUnitPrice()).gstRate(gstRate)
                .lineTotal(lineTotal).serviceId(ir.getServiceId())
                .inventoryItemId(ir.getInventoryItemId()).staff(lineStaff)
                .build());
        }

        // Apply discount
        BigDecimal discount = req.getDiscount() != null ? req.getDiscount() : BigDecimal.ZERO;
        if ("percent".equals(req.getDiscountType())) {
            discount = subtotal.multiply(discount).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        }
        BigDecimal total = subtotal.add(totalGst).subtract(discount);

        // Loyalty points redemption
        int pointsRedeemed;
        if (req.getLoyaltyPointsToRedeem() != null && req.getLoyaltyPointsToRedeem() > 0) {
            var loyaltyAccount = loyaltyAccountRepo.findByClientId(client.getId());
            if (loyaltyAccount.isPresent()) {
                int available = loyaltyAccount.get().getPointsBalance();
                pointsRedeemed = Math.min(req.getLoyaltyPointsToRedeem(), available);
                BigDecimal redemptionValue = BigDecimal.valueOf(pointsRedeemed).multiply(BigDecimal.valueOf(0.5));
                total = total.subtract(redemptionValue).max(BigDecimal.ZERO);
            } else {
                pointsRedeemed = 0;
            }
        } else {
            pointsRedeemed = 0;
        }

        // Points earned (1 point per rupee of total)
        int pointsEarned = total.intValue();

        Invoice invoice = Invoice.builder()
            .invoiceNumber(generateInvoiceNumber())
            .client(client).staff(staff)
            .date(req.getDate() != null ? req.getDate() : LocalDate.now())
            .subtotal(subtotal).discount(discount)
            .discountType(req.getDiscountType() != null ? req.getDiscountType() : "flat")
            .gstAmount(totalGst).total(total)
            .paymentMethod(req.getPaymentMethod())
            .status("paid")
            .loyaltyPointsEarned(pointsEarned)
            .loyaltyPointsRedeemed(pointsRedeemed)
            .notes(req.getNotes())
            .build();
        invoice.setItems(lineItems);
        lineItems.forEach(li -> li.setInvoice(invoice));
        Invoice saved = invoiceRepo.save(invoice);

        // Update loyalty account
        BigDecimal finalTotal = total;
        loyaltyAccountRepo.findByClientId(client.getId()).ifPresent(acct -> {
            acct.setPointsBalance(acct.getPointsBalance() + pointsEarned - pointsRedeemed);
            acct.setTotalPointsEarned(acct.getTotalPointsEarned() + pointsEarned);
            acct.setTotalPointsRedeemed(acct.getTotalPointsRedeemed() + pointsRedeemed);
            acct.setTier(computeTier(client.getTotalSpend() != null ? client.getTotalSpend().add(finalTotal) : finalTotal));
            loyaltyAccountRepo.save(acct);
        });

        // Update client stats
        client.setTotalVisits(client.getTotalVisits() + 1);
        client.setTotalSpend(client.getTotalSpend() != null ? client.getTotalSpend().add(total) : total);
        client.setLastVisit(LocalDate.now());
        clientRepo.save(client);

        if (pointsEarned > 0) {
            loyaltyTxRepo.save(LoyaltyTransaction.builder()
                .client(client).transactionType("earned").points(pointsEarned)
                .description("Points earned on invoice " + saved.getInvoiceNumber())
                .invoiceId(saved.getId()).transactionDate(java.time.LocalDateTime.now())
                .build());
        }
        if (pointsRedeemed > 0) {
            loyaltyTxRepo.save(LoyaltyTransaction.builder()
                .client(client).transactionType("redeemed").points(-pointsRedeemed)
                .description("Points redeemed on invoice " + saved.getInvoiceNumber())
                .invoiceId(saved.getId()).transactionDate(java.time.LocalDateTime.now())
                .build());
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        return toResponse(invoiceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice", id)));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> listByClient(Long clientId) {
        return invoiceRepo.findByClientIdOrderByDateDesc(clientId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> listByDateRange(LocalDate from, LocalDate to) {
        return invoiceRepo.findByDateBetween(from, to).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private String generateInvoiceNumber() {
        int year = LocalDate.now().getYear();
        String prefix = "INV-" + year + "-";
        int next = invoiceRepo.maxSequenceForPrefix(prefix).orElse(0) + 1;
        return prefix + String.format("%04d", next);
    }

    private String computeTier(BigDecimal totalSpend) {
        double spend = totalSpend.doubleValue();
        if (spend >= 75000) return "Platinum";
        if (spend >= 25000) return "Gold";
        if (spend >= 10000) return "Silver";
        return "Basic";
    }

    public InvoiceResponse toResponse(Invoice inv) {
        List<InvoiceItemResponse> itemResponses = inv.getItems() != null
            ? inv.getItems().stream().map(i -> InvoiceItemResponse.builder()
                .id(i.getId()).itemName(i.getItemName()).itemType(i.getItemType())
                .quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).gstRate(i.getGstRate())
                .lineTotal(i.getLineTotal()).serviceId(i.getServiceId())
                .staffId(i.getStaff() != null ? i.getStaff().getId() : null)
                .staffName(i.getStaff() != null ? i.getStaff().getName() : null)
                .build()).collect(Collectors.toList())
            : List.of();

        return InvoiceResponse.builder()
            .id(inv.getId()).invoiceNumber(inv.getInvoiceNumber())
            .clientId(inv.getClient().getId()).clientName(inv.getClient().getName())
            .staffId(inv.getStaff() != null ? inv.getStaff().getId() : null)
            .staffName(inv.getStaff() != null ? inv.getStaff().getName() : null)
            .date(inv.getDate()).items(itemResponses)
            .subtotal(inv.getSubtotal()).discount(inv.getDiscount())
            .discountType(inv.getDiscountType()).gstAmount(inv.getGstAmount()).total(inv.getTotal())
            .paymentMethod(inv.getPaymentMethod()).status(inv.getStatus())
            .loyaltyPointsEarned(inv.getLoyaltyPointsEarned())
            .loyaltyPointsRedeemed(inv.getLoyaltyPointsRedeemed())
            .notes(inv.getNotes())
            .build();
    }
}
