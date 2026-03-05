package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.*;
import com.growthhub.salon.service.impl.LoyaltyServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/loyalty") @RequiredArgsConstructor @Tag(name = "Loyalty & Rewards")
public class LoyaltyController {
    private final LoyaltyServiceImpl loyaltyService;

    // ── Programs ──
    @PostMapping("/programs")
    public ResponseEntity<ApiResponse<LoyaltyProgram>> createProgram(@RequestBody LoyaltyProgramRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Program created", loyaltyService.createProgram(req)));
    }
    @GetMapping("/programs")
    public ResponseEntity<ApiResponse<List<LoyaltyProgram>>> listPrograms() {
        return ResponseEntity.ok(ApiResponse.ok(loyaltyService.listPrograms()));
    }
    @PatchMapping("/programs/{id}/toggle")
    public ResponseEntity<ApiResponse<LoyaltyProgram>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Status toggled", loyaltyService.toggleProgramStatus(id)));
    }

    // ── Membership Packages ──
    @PostMapping("/packages")
    public ResponseEntity<ApiResponse<MembershipPackage>> createPackage(@RequestBody MembershipPackageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Package created", loyaltyService.createPackage(req)));
    }
    @GetMapping("/packages")
    public ResponseEntity<ApiResponse<List<MembershipPackage>>> listPackages() {
        return ResponseEntity.ok(ApiResponse.ok(loyaltyService.listPackages()));
    }
    @PostMapping("/memberships/sell")
    public ResponseEntity<ApiResponse<ClientMembership>> sellMembership(@Valid @RequestBody SellMembershipRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Membership sold", loyaltyService.sellMembership(req)));
    }

    // ── Points ──
    @GetMapping("/accounts/{clientId}")
    public ResponseEntity<ApiResponse<ClientLoyaltyAccount>> getAccount(@PathVariable Long clientId) {
        return ResponseEntity.ok(ApiResponse.ok(loyaltyService.getAccount(clientId)));
    }
    @PostMapping("/points/adjust")
    public ResponseEntity<ApiResponse<ClientLoyaltyAccount>> adjustPoints(@Valid @RequestBody AdjustPointsRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Points adjusted", loyaltyService.adjustPoints(req)));
    }
    @GetMapping("/transactions/{clientId}")
    public ResponseEntity<ApiResponse<List<LoyaltyTransaction>>> transactions(@PathVariable Long clientId) {
        return ResponseEntity.ok(ApiResponse.ok(loyaltyService.getTransactionHistory(clientId)));
    }

    // ── Gift Vouchers ──
    @PostMapping("/vouchers")
    public ResponseEntity<ApiResponse<GiftVoucher>> issueVoucher(@Valid @RequestBody GiftVoucherRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Voucher issued", loyaltyService.issueVoucher(req)));
    }
    @GetMapping("/vouchers")
    public ResponseEntity<ApiResponse<List<GiftVoucher>>> listVouchers() {
        return ResponseEntity.ok(ApiResponse.ok(loyaltyService.listVouchers()));
    }
    @GetMapping("/vouchers/check/{code}")
    public ResponseEntity<ApiResponse<GiftVoucher>> checkVoucher(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(loyaltyService.checkVoucher(code)));
    }
    @PostMapping("/vouchers/redeem")
    public ResponseEntity<ApiResponse<GiftVoucher>> redeemVoucher(@Valid @RequestBody RedeemVoucherRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Voucher redeemed", loyaltyService.redeemVoucher(req)));
    }
}
