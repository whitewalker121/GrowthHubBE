package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.*;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class LoyaltyServiceImpl {

    private final LoyaltyProgramRepository programRepo;
    private final MembershipPackageRepository packageRepo;
    private final ClientMembershipRepository clientMembershipRepo;
    private final ClientLoyaltyAccountRepository accountRepo;
    private final LoyaltyTransactionRepository txRepo;
    private final GiftVoucherRepository voucherRepo;
    private final ClientRepository clientRepo;

    // ── Programs ──
    public LoyaltyProgram createProgram(LoyaltyProgramRequest req) {
        LoyaltyProgram p = new LoyaltyProgram();
        p.setName(req.getName()); p.setType(req.getType()); p.setDescription(req.getDescription());
        p.setPointsPerRupee(req.getPointsPerRupee()); p.setValuePerPoint(req.getValuePerPoint());
        p.setMinRedeemPoints(req.getMinRedeemPoints()); p.setMaxRedeemPct(req.getMaxRedeemPct());
        p.setBonusOnSignup(req.getBonusOnSignup()); p.setMultiplier(req.getMultiplier());
        p.setTierRequired(req.getTierRequired()); p.setBonusPoints(req.getBonusPoints());
        p.setReferrerPoints(req.getReferrerPoints()); p.setRefereePoints(req.getRefereePoints());
        p.setApplicableTo(req.getApplicableTo());
        p.setStatus(req.getStatus() != null ? req.getStatus() : "active");
        return programRepo.save(p);
    }

    @Transactional(readOnly = true)
    public List<LoyaltyProgram> listPrograms() { return programRepo.findAll(); }

    public LoyaltyProgram toggleProgramStatus(Long id) {
        LoyaltyProgram p = programRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LoyaltyProgram", id));
        p.setStatus("active".equals(p.getStatus()) ? "inactive" : "active");
        return programRepo.save(p);
    }

    // ── Membership Packages ──
    public MembershipPackage createPackage(MembershipPackageRequest req) {
        MembershipPackage pkg = MembershipPackage.builder()
            .name(req.getName()).price(req.getPrice()).validity(req.getValidity())
            .includedServiceCount(req.getIncludedServiceCount()).bonusWallet(req.getBonusWallet())
            .discountPct(req.getDiscountPct()).color(req.getColor())
            .services(req.getServices()).status(req.getStatus() != null ? req.getStatus() : "active")
            .build();
        return packageRepo.save(pkg);
    }

    @Transactional(readOnly = true)
    public List<MembershipPackage> listPackages() { return packageRepo.findAll(); }

    public ClientMembership sellMembership(SellMembershipRequest req) {
        Client client = clientRepo.findById(req.getClientId())
            .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()));
        MembershipPackage pkg = packageRepo.findById(req.getPackageId())
            .orElseThrow(() -> new ResourceNotFoundException("MembershipPackage", req.getPackageId()));

        ClientMembership cm = ClientMembership.builder()
            .client(client).membershipPackage(pkg)
            .purchaseDate(LocalDate.now())
            .expiryDate(LocalDate.now().plusDays(pkg.getValidity()))
            .servicesRemaining(pkg.getIncludedServiceCount())
            .walletBalance(pkg.getBonusWallet())
            .status("active")
            .build();
        client.setMembershipType(pkg.getName());
        clientRepo.save(client);
        return clientMembershipRepo.save(cm);
    }

    // ── Points ──
    @Transactional(readOnly = true)
    public ClientLoyaltyAccount getAccount(Long clientId) {
        return accountRepo.findByClientId(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Loyalty account for client", clientId));
    }

    public ClientLoyaltyAccount adjustPoints(AdjustPointsRequest req) {
        ClientLoyaltyAccount acct = accountRepo.findByClientId(req.getClientId())
            .orElseThrow(() -> new ResourceNotFoundException("Loyalty account for client", req.getClientId()));
        int pts = req.getPoints();
        if (pts < 0 && acct.getPointsBalance() < Math.abs(pts))
            throw new BusinessException("Insufficient points balance");
        acct.setPointsBalance(acct.getPointsBalance() + pts);
        if (pts > 0) acct.setTotalPointsEarned(acct.getTotalPointsEarned() + pts);
        else acct.setTotalPointsRedeemed(acct.getTotalPointsRedeemed() + Math.abs(pts));
        accountRepo.save(acct);

        Client client = clientRepo.findById(req.getClientId()).orElseThrow();
        txRepo.save(LoyaltyTransaction.builder()
            .client(client).transactionType(pts > 0 ? "manual_credit" : "manual_debit")
            .points(pts).description(req.getReason())
            .transactionDate(LocalDateTime.now())
            .build());
        return acct;
    }

    @Transactional(readOnly = true)
    public List<LoyaltyTransaction> getTransactionHistory(Long clientId) {
        return txRepo.findByClientIdOrderByTransactionDateDesc(clientId);
    }

    // ── Gift Vouchers ──
    public GiftVoucher issueVoucher(GiftVoucherRequest req) {
        Client client = req.getClientId() != null
            ? clientRepo.findById(req.getClientId()).orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()))
            : null;

        String code = generateVoucherCode();
        GiftVoucher v = GiftVoucher.builder()
            .code(code).client(client)
            .issuedToName(req.getIssuedToName())
            .originalValue(req.getValue()).remainingValue(req.getValue())
            .issuedDate(LocalDate.now())
            .expiryDate(LocalDate.now().plusDays(req.getValidityDays()))
            .occasion(req.getOccasion()).status("active")
            .build();
        return voucherRepo.save(v);
    }

    public GiftVoucher redeemVoucher(RedeemVoucherRequest req) {
        GiftVoucher v = voucherRepo.findByCode(req.getCode())
            .orElseThrow(() -> new ResourceNotFoundException("Voucher with code " + req.getCode() + " not found"));
        if ("redeemed".equals(v.getStatus()))
            throw new BusinessException("Voucher already fully redeemed");
        if ("expired".equals(v.getStatus()) || v.getExpiryDate().isBefore(LocalDate.now()))
            throw new BusinessException("Voucher has expired");
        if (v.getRemainingValue().compareTo(req.getAmount()) < 0)
            throw new BusinessException("Insufficient voucher balance. Remaining: " + v.getRemainingValue());

        v.setRemainingValue(v.getRemainingValue().subtract(req.getAmount()));
        if (v.getRemainingValue().compareTo(java.math.BigDecimal.ZERO) == 0)
            v.setStatus("redeemed");
        else
            v.setStatus("partial");
        return voucherRepo.save(v);
    }

    @Transactional(readOnly = true)
    public List<GiftVoucher> listVouchers() { return voucherRepo.findAll(); }

    @Transactional(readOnly = true)
    public GiftVoucher checkVoucher(String code) {
        return voucherRepo.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Voucher not found: " + code));
    }

    private String generateVoucherCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder("GIFT-");
        Random rng = new Random();
        for (int i = 0; i < 5; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }
}
